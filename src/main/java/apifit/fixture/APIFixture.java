package apifit.fixture;

import static apifit.common.ApiFitConstants.APIFIT_CHECK_STATUS;
import static apifit.common.ApiFitConstants.APIFIT_CONTENT_TYPE;
import static apifit.common.ApiFitConstants.APIFIT_HOST;
import static apifit.common.ApiFitConstants.APIFIT_PATH;
import static apifit.common.ApiFitConstants.APIFIT_PORT;
import static apifit.common.ApiFitConstants.APIFIT_SCHEME;
import static apifit.common.ApiFitConstants.APIFIT_STATUS_CODE;
import static apifit.common.ApiFitConstants.BLANK;
import static apifit.common.ApiFitConstants.DELETE;
import static apifit.common.ApiFitConstants.GET;
import static apifit.common.ApiFitConstants.HTML_CONTENT_TYPE;
import static apifit.common.ApiFitConstants.JSON_CONTENT_TYPE;
import static apifit.common.ApiFitConstants.PAYLOAD;
import static apifit.common.ApiFitConstants.POST;
import static apifit.common.ApiFitConstants.SCHEMA;
import static apifit.common.ApiFitConstants.XML_CONTENT_TYPE;
import static apifit.common.ApiFitConstants.XPATH;
import static apifit.common.DataPattern.doPattern;
import static apifit.common.DataPattern.isApiFitPattern;
import static apifit.common.DataPattern.isDatePattern;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;

import apifit.api.APIToolBox;
import apifit.common.ApiFitCache;
import apifit.common.ApiFitException;
import apifit.common.ApiFitLogger;
import apifit.common.GracefulNamer;
import apifit.common.TestSessionCache;
import apifit.contract.AbstractFixture;
import apifit.contract.IDynamicDecisionTableFixture;
import apifit.domain.APIDomain;
import apifit.json.JsonToolBox;
import apifit.json.ValidationUtils;
import apifit.xml.XmlToolBox;

public class APIFixture extends AbstractFixture implements IDynamicDecisionTableFixture {

	private String baseURL = "";
	private String URL = "";
	private String httpVerb = GET;
	private int nbParams;
	private String payload = null;
	private String contentType = JSON_CONTENT_TYPE;
	private APIToolBox httpToolBox;
	//private JsonToolBox jsonToolBox;
	private Integer checkStatus = -1;
	private String validationMessage = "";

	public APIFixture() {
		this(GET, 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_SCHEME), 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_HOST), 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_PORT), 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_PATH), 
				null);
	}

	public APIFixture(String httpVerb) {
		this(httpVerb, 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_SCHEME), 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_HOST), 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_PORT), 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_PATH), 
				null);
	}
	public APIFixture(String httpVerb, String host) {
		this(httpVerb, 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_SCHEME), 
				host, 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_PORT), 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_PATH), 
				null);
	}
	public APIFixture(String httpVerb, String host, String path) {
		this(httpVerb, 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_SCHEME), 
				host, 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_PORT), 
				path, 
				null);
	}

	public APIFixture(String httpVerb, String host, String path, String testSessionId) {
		this(httpVerb, 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_SCHEME), 
				host, 
				ApiFitCache.getInstance().getConfigProperty(APIFIT_PORT), 
				path, 
				testSessionId);
	}
	public APIFixture(String httpVerb, String scheme, String host, String port, String path) {
		this(httpVerb, scheme, host, port, path, null);
	}
	public APIFixture(String httpVerb, String scheme, String host, String port, String path, String testSessionId) {
		super(testSessionId);
		this.httpVerb = httpVerb;
		this.httpToolBox = new APIToolBox();
		if (scheme == null || scheme.trim().length() == 0) scheme = "http";
		if (port == null || port.trim().length() == 0) port = "80";
		this.baseURL = httpToolBox.buildURI(scheme, host, new Integer(port), path);
		URL = baseURL;
		nbParams = 0;
		payload = (String) TestSessionCache.getInstance().getObjectInTestSession(testSessionId+PAYLOAD);
		//TODO see why this !
		if (payload != null && contentType != HTML_CONTENT_TYPE) payload = payload.replace("<br/>", "");
		validationMessage = "";
	}

	public void set(String header, String value) {
		
		if (value.trim().length() == 0) return;
		header = header.trim();
		value = value.trim();
		if (value.trim().equals(BLANK)) value = "";
		
		if (header.equals(APIFIT_CHECK_STATUS)) {
			checkStatus = new Integer(value);
		} else if (header.startsWith("[") && header.endsWith("]")) {
			URL = StringUtils.replace(URL, header, value);
		} else {
			if (httpVerb.equals(GET) || httpVerb.equals(DELETE)) {
				nbParams++;
				if (isApiFitPattern(value)) {
					if (isDatePattern(value)) {
						LocalDateTime time = (LocalDateTime) doPattern(value);
						value = StringUtils.substringBefore(time.toString(), "T");
					}
				}
				value = StringUtils.replace(value, " ", "%20");
				if (nbParams == 1) URL = httpToolBox.addFirstParameter(URL, header, value);
				else URL = httpToolBox.addParameter(URL, header, value);
			} else {
				if (header.startsWith(XPATH+":")) {
					XmlToolBox xmlToolBox = new XmlToolBox();
					try {
						payload = xmlToolBox.updateXmlNodeValue(payload, StringUtils.substringAfter(header, XPATH+":"), value);
					} catch (ApiFitException ignore) {
						ApiFitLogger.log(ignore.getMessage());
					}
				} else {
					JsonToolBox jsonToolBox = new JsonToolBox();
					if (isApiFitPattern(value)) {
						if (isDatePattern(value)) {
							LocalDateTime time = (LocalDateTime) doPattern(value);
							value = StringUtils.substringBefore(time.toString(), "T");
						}
					}
					try {
						payload = jsonToolBox.updateJsonAttribute(payload, header, value);
					} catch (ApiFitException ignore) {
						ApiFitLogger.log(ignore.getMessage());
					}
				}
			}

		}
	}

	public String get(String requestedValue) {		
		String returnedValue = "";
		if (requestedValue.equals(APIFIT_STATUS_CODE)) {
			if (statusCode != null) returnedValue = statusCode.toString();
		} else if (isApiFitPattern(requestedValue)) {
			//if (isCounterPattern(requestedValue)) {
			returnedValue = doPattern(requestedValue, executionSuccessBody).toString();
			//} else if (isStringPattern(requestedValue)) {

			//}
		} else if (GracefulNamer.disgrace(requestedValue).equals("StatusCode")) {
			if (statusCode != null) returnedValue = statusCode.toString();
		} else if (GracefulNamer.disgrace(requestedValue).equals("ExecutionStatus")) {
			returnedValue = executionStatus;
		} else if (GracefulNamer.disgrace(requestedValue).equals("ExecutionSuccessBody")) {
			returnedValue = executionSuccessBody;
		} else if (GracefulNamer.disgrace(requestedValue).equals("ExecutionErrorMessage")) {
			returnedValue = executionErrorMessage;
		} else if (GracefulNamer.disgrace(requestedValue).equals("ExecutionTime")) {
			if (executionTime != null) returnedValue = executionTime.toString();
		} else if (GracefulNamer.disgrace(requestedValue).equals("IsResponseValid")) {
			returnedValue = schemaValidation();
		} else if (GracefulNamer.disgrace(requestedValue).equals("ValidationMessage")) {
			returnedValue = validationMessage;
		} else {
			returnedValue = getParamFromResultBody(requestedValue);	
		}
		return returnedValue;
	}

	public void execute() {
		//TODO to be handled in config
		if (checkStatus == -1) {
			if (httpVerb.equals(POST)) checkStatus = 201;
			else checkStatus = 200;
		}
		//TODO see why this
		domain = new APIDomain(httpVerb, URL, payload, contentType, checkStatus);
		super.execute();	
	}

	public String URL() {
		return URL;
	}

	public void reset() {
		URL = baseURL;
		nbParams = 0;
		payload = (String) TestSessionCache.getInstance().getObjectInTestSession(testSessionId+PAYLOAD);
		//TODO see why this !
		if (payload != null && contentType != HTML_CONTENT_TYPE) payload = payload.replace("<br/>", "");
	}

	private String getParamFromResultBody(String requestedValue) {

		String returnedValue = null;

		//TODO I think I need to put that in execution context, not in global context
		String storedContentType = ApiFitCache.getInstance().getConfigProperty(APIFIT_CONTENT_TYPE);

		if (storedContentType == null || storedContentType.length() == 0) {
			storedContentType = contentType;
		}

		if (storedContentType.equals(JSON_CONTENT_TYPE)) {
			JsonToolBox jsonToolBox = new JsonToolBox();
			returnedValue = jsonToolBox.getJsonParamValue(executionSuccessBody, requestedValue)+"";	
		} else if (storedContentType.equals(XML_CONTENT_TYPE)) {
			XmlToolBox xmlToolBox = new XmlToolBox();
			returnedValue = xmlToolBox.getXmlParamValue(executionSuccessBody, requestedValue)+"";	
		} else if (storedContentType.equals(HTML_CONTENT_TYPE)) {
			XmlToolBox xmlToolBox = new XmlToolBox();
			returnedValue = xmlToolBox.getHtmlParamValue(executionSuccessBody, requestedValue)+"";	
		}

		return returnedValue;
	}

	private String schemaValidation() {
		String ret = "FASLE";
		validationMessage = "";

		Object obj = TestSessionCache.getInstance().getObjectInTestSession(testSessionId+SCHEMA); 
		if (obj == null) return ret;

		String schema = obj.toString();

		try {
			ValidationUtils.validateJson(schema.replace("<br/>", ""), executionSuccessBody);
			ret = "TRUE";
		} catch (IOException e) {
			e.printStackTrace();
			validationMessage = e.getMessage();
		} catch (ProcessingException e) {
			validationMessage = e.getMessage();
		}

		return ret;
	}
}
