package apifit.fixture;

import static apifit.common.ApiFitConstants.AUTH_HOST;
import static apifit.common.ApiFitConstants.AUTH_PARAMS;
import static apifit.common.ApiFitConstants.AUTH_PORT;
import static apifit.common.ApiFitConstants.AUTH_PSWD;
import static apifit.common.ApiFitConstants.AUTH_USER;

import java.util.Hashtable;

import apifit.common.TestSessionCache;

public class AuthentificationFixture {

	private Hashtable<String, String> authParams = null;
	private String testSessionId = null;
	private String user = null;
	private String password = null;
	private String host = null;
	private String port = null;
	
	private AuthentificationFixture() {
	}
	
	public AuthentificationFixture(String testSessionId) {
		this.testSessionId = testSessionId;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public void setPort(String port) {
		this.port = port;
	}
	
	public void execute() {
		if (host != null && port != null && user != null && password != null) {
			authParams = new Hashtable<String, String>();
			authParams.put(AUTH_HOST, host);
			authParams.put(AUTH_PORT, port);
			authParams.put(AUTH_USER, user);
			authParams.put(AUTH_PSWD, password);
			TestSessionCache.getInstance().addOrUpdateObjectInTestSession(testSessionId+AUTH_PARAMS, authParams);
		}
	}
	
}
