package apifit.common;

import java.util.Hashtable;

import apifit.api.APIHost;

public class ApiFitCache {

	private Hashtable<String, String> config;
	private String baseURL;
	private APIHost proxy;
	
	private ApiFitCache() {
	}
	
	/** Instance unique non préinitialisée */
	private static ApiFitCache INSTANCE = null;

	/** Point d'accès pour l'instance unique du singleton */
	public static ApiFitCache getInstance() {	
		if (INSTANCE == null) { 	
			synchronized(ApiFitCache.class) {
				if (INSTANCE == null) {	
					INSTANCE = new ApiFitCache();
					INSTANCE.initConfig();
				}
			}
		}
		return INSTANCE;
	}
	
	private void initConfig() {
		config = new Hashtable<String, String>();
	}
	
	public String getConfigProperty(String key) {
		return config.get(key);
	}
	public void addConfigProperty(String key, String value) {
		config.put(key, value);
	}
	public void updateConfigProperty(String key, String value) {
		config.replace(key, value);
	}
	public void addOrUpdateConfigProperty(String key, String value) {
		if (config.get(key) != null) {
			updateConfigProperty(key, value);
		} else {
			addConfigProperty(key, value);
		}
	}
	public void clearConfig() {
		config.clear();
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}
	public String getBaseURL() {
		return baseURL;
	}
	public void setProxy(APIHost proxy) {
		this.proxy = proxy;
	}
	public APIHost getProxy() {
		return proxy;
	}
}
