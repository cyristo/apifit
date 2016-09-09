package apifit.fixture;

import static apifit.common.ApiFitConstants.APIFIT_CHECK_STATUS;
import static apifit.common.ApiFitConstants.APIFIT_CONTENT_TYPE;
import static apifit.common.ApiFitConstants.APIFIT_HOST;
import static apifit.common.ApiFitConstants.APIFIT_PATH;
import static apifit.common.ApiFitConstants.APIFIT_PAYLOAD;
import static apifit.common.ApiFitConstants.APIFIT_PORT;
import static apifit.common.ApiFitConstants.APIFIT_SCHEME;
import static apifit.common.ApiFitConstants.APIFIT_STATUS_CODE;
import static apifit.common.ApiFitConstants.GET;
import static apifit.common.ApiFitConstants.HTML_CONTENT_TYPE;
import static apifit.common.ApiFitConstants.JSON_CONTENT_TYPE;
import static apifit.common.ApiFitConstants.XML_CONTENT_TYPE;
import static apifit.common.DataPattern.doPattern;
import static apifit.common.DataPattern.isApiFitPattern;
import static apifit.common.DataPattern.isDatePattern;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import apifit.api.APIToolBox;
import apifit.common.ApiFitCache;
import apifit.common.GracefulNamer;
import apifit.contract.AbstractFixture;
import apifit.contract.IDynamicDecisionTableFixture;
import apifit.domain.APIDomain;
import apifit.json.JsonToolBox;
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
	private Integer checkStatus = 200;

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
	}

	public void set(String header, String value) {
		header = header.trim();
		value = value.trim();
		if (header.equals(APIFIT_PAYLOAD)) {
			payload = value;
		} else if (header.equals(APIFIT_CHECK_STATUS)) {
			checkStatus = new Integer(value);
		} else if (header.startsWith("[") && header.endsWith("]")) {
			URL = StringUtils.replace(URL, header, value);
		} else {
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
		}
	}

	public String get(String requestedValue) {		
		String returnedValue = "";
		if (requestedValue.equals(APIFIT_STATUS_CODE)) {
			returnedValue = statusCode.toString();
		} else if (isApiFitPattern(requestedValue)) {
			//if (isCounterPattern(requestedValue)) {
				returnedValue = doPattern(requestedValue, executionSuccessBody).toString();
			//} else if (isStringPattern(requestedValue)) {
				
			//}
		} else if (GracefulNamer.disgrace(requestedValue).equals("StatusCode")) {
			returnedValue = statusCode.toString();
		} else if (GracefulNamer.disgrace(requestedValue).equals("ExecutionStatus")) {
			returnedValue = executionStatus;
		} else if (GracefulNamer.disgrace(requestedValue).equals("ExecutionSuccessBody")) {
			returnedValue = executionSuccessBody;
		} else if (GracefulNamer.disgrace(requestedValue).equals("ExecutionErrorMessage")) {
			returnedValue = executionErrorMessage;
		} else if (GracefulNamer.disgrace(requestedValue).equals("ExecutionTime")) {
			returnedValue = executionTime.toString();
		} else {
			returnedValue = getParamFromResultBody(requestedValue);	
		}
		return returnedValue;
	}

	public void execute() {
		domain = new APIDomain(httpVerb, URL, payload, contentType, checkStatus);
		super.execute();	
	}

	public String URL() {
		return URL;
	}
	
	public void reset() {
		URL = baseURL;
		nbParams = 0;
	}
	
	private String getParamFromResultBody(String requestedValue) {
		
		String returnedValue = null;
		
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
}
