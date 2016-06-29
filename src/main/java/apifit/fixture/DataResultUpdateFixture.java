package apifit.fixture;

import static apifit.common.ApiFitConstants.EXECUTION_SUCCESS_BODY;

import java.util.Enumeration;
import java.util.Hashtable;

import apifit.common.ApiFitException;
import apifit.common.TestSessionCache;
import apifit.contract.AbstractFixture;
import apifit.contract.IDynamicDecisionTableFixture;
import apifit.json.JsonToolBox;

public class DataResultUpdateFixture extends AbstractFixture implements IDynamicDecisionTableFixture {

	private String dataToUpdate;
	private JsonToolBox jsonToolBox;
	private Hashtable<String, String> table;
	boolean exceptionThrown = false;
	
	public DataResultUpdateFixture(String testSessionId) {
		this(testSessionId, EXECUTION_SUCCESS_BODY);
	}
	public DataResultUpdateFixture(String testSessionId, String dataParsingType) {
		super(testSessionId, false);
		this.dataToUpdate = (String) TestSessionCache.getInstance().getObjectInTestSession(testSessionId+EXECUTION_SUCCESS_BODY);	
		jsonToolBox = new JsonToolBox();
		table = new Hashtable<String, String>();
	}

	public void set(String fieldPath, String newValue) {
		table.put(fieldPath, newValue);
	}

	public void execute() {
		
		Enumeration<String> en = table.keys();
		while (en.hasMoreElements()) {
			String fieldPath = (String) en.nextElement();
			String newValue = table.get(fieldPath);
			try {
				dataToUpdate = jsonToolBox.updateJsonAttribute(dataToUpdate, fieldPath, newValue);
			} catch (ApiFitException e) {
				exceptionThrown = true;
			}
		}

		if (!exceptionThrown) {
			TestSessionCache.getInstance().addOrUpdateObjectInTestSession(testSessionId+EXECUTION_SUCCESS_BODY, dataToUpdate);
		}
	}

	public String get(String requestedValue) {		
		if (!exceptionThrown) {
			return "OK";
		} else {
			return "KO";
		}
	}
}
