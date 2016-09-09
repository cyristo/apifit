package apifit.fixture;

import static apifit.common.ApiFitConstants.STATUS_OK;

import java.util.UUID;

import apifit.common.TestSessionCache;

public class SessionFixture {

	private String testSessionId;
	
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




}
