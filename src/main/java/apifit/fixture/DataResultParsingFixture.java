package apifit.fixture;

import static apifit.common.ApiFitConstants.EXECUTION_SUCCESS_BODY;

import apifit.common.TestSessionCache;
import apifit.contract.AbstractFixture;
import apifit.contract.IDynamicDecisionTableFixture;
import apifit.json.JsonToolBox;

public class DataResultParsingFixture extends AbstractFixture implements IDynamicDecisionTableFixture {

	private String data;
	private JsonToolBox jsonToolBox;
	
	public DataResultParsingFixture(String testSessionId) {
		this(testSessionId, EXECUTION_SUCCESS_BODY);
	}
	public DataResultParsingFixture(String testSessionId, String dataParsingType) {
		super(testSessionId, false);
		this.data = (String) TestSessionCache.getInstance().getObjectInTestSession(testSessionId.trim()+dataParsingType.trim());	
		jsonToolBox = new JsonToolBox();
		
	}

	public String get(String requestedValue) {		
		String value = jsonToolBox.getJsonParamValue(data, requestedValue).toString();
		return value;
	}
	
	public void set(String header, String value) {
		// TODO see when this is needed
	}



}
