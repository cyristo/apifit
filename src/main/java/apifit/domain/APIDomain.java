package apifit.domain;
import static apifit.common.ApiFitConstants.POST;
import static apifit.common.ApiFitConstants.PUT;
import apifit.common.ApiFitException;
import apifit.common.TestSessionCache;
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
	
	}

	public void setStatusToCheck(Integer statusToCheck) {
		this.statusToCheck = statusToCheck;
	}

}
