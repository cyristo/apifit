package apifit.contract;

import java.util.List;

public interface IDecisionTableFixture {

	public void table(List<List<String>> table); 
	public void beginTable();
	public void reset();
	public void execute();
	public void endTable(); 
	
	public String executionStatus();
	public String executionSuccessBody();
	public String executionErrorMessage();
	public Integer statusCode();
	public Long executionTime();

	
}
