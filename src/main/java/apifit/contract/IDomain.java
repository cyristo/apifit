package apifit.contract;

import apifit.common.ApiFitException;

public interface IDomain {
	
	public void execute(String testSessionId) throws ApiFitException;
	public String getExecutionStatus();
	public Integer getStatusCode();
	public String getExecutionBody();
	public String getExecutionErrorMessage();
	public Long getExecutionTime();
	
}
