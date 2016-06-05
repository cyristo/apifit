package apifit.fixture;

import apifit.common.ApiFitCache;

public class PropertyConfigurationFixture {

	public void set(String key, String value) {
		ApiFitCache.getInstance().addOrUpdateConfigProperty(key, value);
	}

	public String get(String key) {
		return ApiFitCache.getInstance().getConfigProperty(key);
	}

	
}
