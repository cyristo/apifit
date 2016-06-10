package apifit.fixture;

import static apifit.common.ApiFitConstants.APIFIT_HOST;
import static apifit.common.ApiFitConstants.APIFIT_PATH;
import static apifit.common.ApiFitConstants.APIFIT_PORT;
import static apifit.common.ApiFitConstants.APIFIT_PROXY_HOST;
import static apifit.common.ApiFitConstants.APIFIT_PROXY_PORT;
import static apifit.common.ApiFitConstants.APIFIT_SCHEME;

import apifit.api.APIHost;
import apifit.api.APIToolBox;
import apifit.common.ApiFitCache;

public class PropertyConfigurationFixture {

	public void set(String key, String value) {
		ApiFitCache.getInstance().addOrUpdateConfigProperty(key, value);
	}

	public String get(String key) {
		return ApiFitCache.getInstance().getConfigProperty(key);
	}

	public void execute() {
		initBaseURL();
		initProxy();
	}
	
	private void initBaseURL() {
		String scheme = ApiFitCache.getInstance().getConfigProperty(APIFIT_SCHEME);
		if (scheme == null || scheme.trim().length() == 0 ) scheme = "http";
		String host = ApiFitCache.getInstance().getConfigProperty(APIFIT_HOST);
		String port = ApiFitCache.getInstance().getConfigProperty(APIFIT_PORT);
		if (port == null || port.trim().length() == 0 ) port = "80";
		String path = ApiFitCache.getInstance().getConfigProperty(APIFIT_PATH);
		
		APIToolBox box = new APIToolBox();
		String baseURL = box.buildURI(scheme, host, new Integer(port), path);
		ApiFitCache.getInstance().setBaseURL(baseURL);
		
	}
	private void initProxy() {
		String proxyHost = ApiFitCache.getInstance().getConfigProperty(APIFIT_PROXY_HOST);
		String integerString = ApiFitCache.getInstance().getConfigProperty(APIFIT_PROXY_PORT);
		if (proxyHost != null) {
			APIHost proxy = new APIHost();
			proxy.setHost(proxyHost);
			Integer proxyPort = 80;
			if (integerString != null) {
				proxyPort = new Integer(integerString);
			}
			proxy.setPort(proxyPort);
			ApiFitCache.getInstance().setProxy(proxy);
		}
		
	}


	
}
