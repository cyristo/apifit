package apifit.fixture;

import static apifit.common.ApiFitConstants.EXECUTION_SUCCESS_BODY;

import java.util.Enumeration;
import java.util.Hashtable;

import apifit.common.ApiFitException;
import apifit.common.TestSessionCache;
import apifit.contract.AbstractFixture;
import apifit.contract.IDynamicDecisionTableFixture;
import apifit.json.JsonToolBox;

public class DataResultAddFixture extends AbstractFixture implements IDynamicDecisionTableFixture {

	private String dataToUpdate;
	private JsonToolBox jsonToolBox;
	private Hashtable<String, String> table;
	boolean exceptionThrown = false;
	
	public DataResultAddFixture(String testSessionId) {
		super(testSessionId, false);
		this.dataToUpdate = (String) TestSessionCache.getInstance().getObjectInTestSession(testSessionId+EXECUTION_SUCCESS_BODY);	
		jsonToolBox = new JsonToolBox();
		table = new Hashtable<String, String>();
	}

	public void set(String fieldToAdd, String newValue) {
		table.put(fieldToAdd, newValue);
	}

	public void execute() {
		
		Enumeration<String> en = table.keys();
		while (en.hasMoreElements()) {
			String fieldToAdd = (String) en.nextElement();
			String newValue = table.get(fieldToAdd);
			try {
				dataToUpdate = jsonToolBox.addNodeToPayload(dataToUpdate, fieldToAdd, newValue);
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
