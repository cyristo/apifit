package apifit.domain;

import apifit.common.ApiFitException;
import apifit.contract.AbstractAPIDomain;

public class APIDomain extends AbstractAPIDomain {

	private String URL; 
	private String httpVerb;
	private String payload;
	private String contentType;
	private Integer statusToCheck = 200;
	//private boolean checkStatus = false;
	
	public APIDomain(String httpVerb, String URL, String payload, String contentType) {
		this.httpVerb = httpVerb;
		this.URL = URL;
		this.payload = payload;
		this.contentType = contentType;
	}
	public APIDomain(String httpVerb, String URL, String payload, String contentType, Integer statusToCheck) {
		this.httpVerb = httpVerb;
		this.URL = URL;
		this.payload = payload;
		this.contentType = contentType;
		this.statusToCheck = statusToCheck;
	}

	public void execute(String testSessionId) throws ApiFitException {
		
		//************* standard execution
		initStandardExecution(httpVerb, testSessionId);		
		standardExecution(contentType, URL, statusToCheck, payload);

		/*
		executionStatus = ApiFitConstants.STATUS_UNKNOWN;

		HttpRequest req = new HttpRequest(httpVerb);
		Map<String, String> cookies = (Map<String, String>) 
				TestExecutionCache.getInstance().getObjectInTestExecutionContext(testSessionId+ApiFitConstants.COOKIES); 
		if (cookies != null) request.addCookies(cookies);
		
		boolean execStatus = req.execute(contentType, URL, statusToCheck, payload);
		
		if (checkStatus) {
			if (execStatus) {
				executionStatus = ApiFitConstants.STATUS_OK;
				executionBody = req.getResponseBody();
			} else {
				executionStatus = ApiFitConstants.STATUS_KO;
				executionErrorMessage = req.getResponseBody();
			}
		} else {
			executionBody = req.getResponseBody();
		}
		
		statusCode = req.getStatusCode();
		executionTime = req.getRequestTime();
		
		ApiFitLogger.log(req.getRequestTrace());
		*/
	}

	public void setStatusToCheck(Integer statusToCheck) {
		this.statusToCheck = statusToCheck;
	}

}
