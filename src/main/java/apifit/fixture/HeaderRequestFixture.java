package apifit.fixture;

import static apifit.common.ApiFitConstants.HEADERS;

import java.util.Hashtable;

import apifit.common.ApiFitCache;
import apifit.common.TestSessionCache;

public class HeaderRequestFixture {

	private Hashtable<String, String> requestHeaders = null;
	private String testSessionId = null;
	
	
	private HeaderRequestFixture() {
	}
	
	public HeaderRequestFixture(String testSessionId) {
		this.testSessionId = testSessionId;
	}
	
	public void set(String key, String value) {
		if (requestHeaders == null) requestHeaders = new Hashtable<String, String>();
		requestHeaders.put(key, value);
		TestSessionCache.getInstance().addOrUpdateObjectInTestSession(testSessionId+HEADERS, requestHeaders);
	}

	public void execute() {
	
	}
}
