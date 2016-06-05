package apifit.contract;

public interface IDynamicDecisionTableFixture extends IDecisionTableFixture {

	public void set(String header, String value);
	public String get(String requestedValue);
	
}
