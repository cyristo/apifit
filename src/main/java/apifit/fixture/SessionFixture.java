package apifit.fixture;

import static apifit.common.ApiFitConstants.STATUS_OK;
import static apifit.common.ApiFitConstants.APIFIT_REQUEST_HEADERS;

import java.util.UUID;

import apifit.common.ApiFitCache;
import apifit.common.TestSessionCache;

public class SessionFixture {

	private String testSessionId;
	private String header = null;
	
	public SessionFixture() {
	}
	
	public SessionFixture(String testSessionId) {
		this.testSessionId = testSessionId;
	}

	public String testSessionId() {
		return testSessionId;
	}
	
	public String setUp() {
		UUID uuid = UUID.randomUUID();
		testSessionId = uuid.toString();
		if (header != null) TestSessionCache.getInstance().addOrUpdateObjectInTestSession(testSessionId+APIFIT_REQUEST_HEADERS, header);
		return STATUS_OK;
	}

	public String tearDown() {
		TestSessionCache.getInstance().removeAllObjectInTestSessionStartingWithKey(testSessionId);
		return STATUS_OK;
	}
	
	public String suiteSetUp() {
		return STATUS_OK;
	}

	public String suiteTearDown() {
		TestSessionCache.getInstance().clearCache();
		return STATUS_OK;
	}

	public void setHeader(String header) {
		this.header = header;
	}


}
