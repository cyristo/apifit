package apifit.contract;

import static apifit.common.ApiFitConstants.COOKIES;
import static apifit.common.ApiFitConstants.EXECUTION_SUCCESS_BODY;
import static apifit.common.ApiFitConstants.HEADERS;
import static apifit.common.ApiFitConstants.POST;
import static apifit.common.ApiFitConstants.PUT;
//import static apifit.common.ApiFitConstants.PROXY_HOST;
//import static apifit.common.ApiFitConstants.PROXY_PORT;
import static apifit.common.ApiFitConstants.STATUS_KO;
import static apifit.common.ApiFitConstants.STATUS_OK;
import static apifit.common.ApiFitConstants.STATUS_UNKNOWN;

import java.util.Hashtable;

import apifit.api.APIClient;
import apifit.common.ApiFitException;
import apifit.common.ApiFitLogger;
import apifit.common.TestSessionCache;
import apifit.json.JsonToolBox;

public abstract class AbstractAPIDomain implements IDomain {

	protected String executionStatus = STATUS_KO;
	protected String executionBody = "";
	protected String executionErrorMessage = "";
	protected Integer statusCode = null;
	protected Long executionTime = (long) -1;
	protected IDTO dto;
	protected APIClient apiClient;
	protected String requestType;
	protected String testSessionId;
	
	protected void initStandardExecution(String requestType, String testSessionId) {
		this.requestType = requestType;
		this.testSessionId = testSessionId;
		executionStatus = STATUS_UNKNOWN;
		apiClient = new APIClient(requestType);

		Object cookies = TestSessionCache.getInstance().getObjectInTestSession(testSessionId+COOKIES); 
		if (cookies != null) {
			apiClient.setCookies((Hashtable<String, String>) cookies);
		}
		Object headers = TestSessionCache.getInstance().getObjectInTestSession(testSessionId+HEADERS); 
		if (headers != null) {
			apiClient.setRequestHeaders((Hashtable<String, String>) headers);
		}
		/*
		Object proxyHost = TestSessionCache.getInstance().getObjectInTestSession(PROXY_HOST);
		Object proxyPort = TestSessionCache.getInstance().getObjectInTestSession(PROXY_PORT);
		if (proxyHost != null) {
			if (proxyPort == null) proxyPort = new Integer(80);
			apiClient.setProxy((String) proxyHost, (Integer) proxyPort);
		}
		*/
	}
	
	protected void standardExecution(String contentType, String URL, int checkStatus, String payload) throws ApiFitException {
		if ((requestType.equals(POST) || requestType.equals(PUT)) && payload == null) {
			payload = (String) TestSessionCache.getInstance().getObjectInTestSession(testSessionId+EXECUTION_SUCCESS_BODY);
		}
		if (apiClient.execute(contentType, URL, checkStatus, payload)) {
			executionStatus = STATUS_OK;
			executionBody = apiClient.getResponseBody();
		} else {
			executionStatus = STATUS_KO;
			executionErrorMessage = apiClient.getResponseBody();
		}
		statusCode = apiClient.getStatusCode();
		executionTime = apiClient.getRequestTime();
		ApiFitLogger.log(apiClient.getRequestFlow().toString());
	}

	
	public String getExecutionStatus() {
		return executionStatus;
	}

	public String getExecutionBody() {
		return executionBody;
	}
	
	public String getExecutionErrorMessage() {
		return executionErrorMessage;
	}

	public Integer getStatusCode() {
		return statusCode;
	}
	
	public Long getExecutionTime() {
		return executionTime;
	}
	
	protected String extractValueFromJsonPayload(String payload, String param) {
		JsonToolBox jsonToolBox = new JsonToolBox();
		return (String) jsonToolBox.getJsonParamValue(payload, param);
	}
	
}
