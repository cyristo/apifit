package apifit.contract;

import static apifit.common.ApiFitConstants.EXECUTION_ERROR_MESSAGE;
import static apifit.common.ApiFitConstants.EXECUTION_SUCCESS_BODY;

import java.util.List;

import apifit.common.ApiFitException;
import apifit.common.ApiFitLogger;
import apifit.common.TestSessionCache;

public abstract class AbstractFixture implements IDecisionTableFixture {
	
	protected IDomain domain;
	protected String executionStatus = null;
	protected String executionSuccessBody = "";
	protected String executionErrorMessage = "";
	protected Integer statusCode = null;
	protected Long executionTime = (long) -1;
	protected String testSessionId = null;
	protected boolean execute = true;

	public AbstractFixture() {
	}
	public AbstractFixture(Boolean execute) {
		this.execute = execute;
	}
	public AbstractFixture(String testSessionId) {
		this.testSessionId = testSessionId;
	}
	public AbstractFixture(String testSessionId, Boolean execute) {
		this.execute = execute;
		this.testSessionId = testSessionId;
	}
	
	public void execute() {
		if (execute) {
			try {
				domain.execute(testSessionId);
			} catch (ApiFitException e){
				ApiFitLogger.log(e.getMessage());
			}
			executionStatus = domain.getExecutionStatus();
			executionSuccessBody = domain.getExecutionBody();
			executionErrorMessage = domain.getExecutionErrorMessage();
			if (testSessionId != null) {
				TestSessionCache.getInstance().addOrUpdateObjectInTestSession(testSessionId.trim()+EXECUTION_SUCCESS_BODY, executionSuccessBody);
				TestSessionCache.getInstance().addOrUpdateObjectInTestSession(testSessionId.trim()+EXECUTION_ERROR_MESSAGE, executionErrorMessage);				
			}
			statusCode = domain.getStatusCode();		
			executionTime = domain.getExecutionTime();
		}
	}
	
	public String executionStatus() {
		return executionStatus;
	}

	public String executionSuccessBody() {
		return executionSuccessBody;
	}
	
	public String executionErrorMessage() {
		return executionErrorMessage;
	}
	
	public Integer statusCode() {
		return statusCode;
	}
	
	public Long executionTime() {
		return executionTime;
	}
	
	public void table(List<List<String>> table) {
		// TODO Auto-generated method stub
	}

	public void beginTable() {
		// TODO Auto-generated method stub
	}

	public void reset() {
		// TODO Auto-generated method stub
	}

	public void endTable() {
		// TODO Auto-generated method stub
	}

}
