package apifit.fixture;

import static apifit.common.ApiFitConstants.HEADERS;

import java.util.HashMap;
import java.util.Hashtable;

import apifit.common.TestSessionCache;

public class HeaderRequestFixture {

	private HashMap<String, String> requestHeaders = null;
	private String testSessionId = null;
	
	
	private HeaderRequestFixture() {
	}
	
	public HeaderRequestFixture(String testSessionId) {
		this.testSessionId = testSessionId;
	}
	
	public void set(String key, String value) {
		if (requestHeaders == null) requestHeaders = new HashMap<String, String>();
		requestHeaders.put(key, value);
		TestSessionCache.getInstance().addOrUpdateObjectInTestSession(testSessionId+HEADERS, requestHeaders);
	}

	public void execute() {
	
	}
}
