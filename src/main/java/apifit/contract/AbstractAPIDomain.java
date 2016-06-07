package apifit.contract;

import static apifit.common.ApiFitConstants.COOKIES;
import static apifit.common.ApiFitConstants.PROXY;
import static apifit.common.ApiFitConstants.STATUS_KO;
import static apifit.common.ApiFitConstants.STATUS_OK;
import static apifit.common.ApiFitConstants.STATUS_UNKNOWN;

import java.util.Hashtable;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;

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
	protected APIClient request;
	
	protected void initStandardExecution(String requestType, String testSessionId) {
		executionStatus = STATUS_UNKNOWN;
		request = new APIClient(requestType);
		//request.setContentEncoder(GZIP, null);

		Object cookies = TestSessionCache.getInstance().getObjectInTestSession(testSessionId+COOKIES); 
		if (cookies != null) {
			request.setCookies((Hashtable<String, String>) cookies);
		}
		
		Object proxy = TestSessionCache.getInstance().getObjectInTestSession(PROXY);
		if (proxy != null) {
			request.setProxy((String) proxy);
		}
	}
	
	protected void standardExecution(String contentType, String URL, int checkStatus, String payload) throws ApiFitException {
		if (request.execute(contentType, URL, checkStatus, payload)) {
			executionStatus = STATUS_OK;
			executionBody = request.getResponseBody();
		} else {
			executionStatus = STATUS_KO;
			executionErrorMessage = request.getResponseBody();
		}
		statusCode = request.getStatusCode();
		executionTime = request.getRequestTime();
		ApiFitLogger.log(request.getRequestFlow().toString());
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
	
	protected String extractValueFromPayload(String payload, String param) {
		JsonToolBox jsonToolBox = new JsonToolBox();
		return (String) jsonToolBox.getJsonParamValue(payload, param);
	}
	
}
