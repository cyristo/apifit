package apifit.api;

public class APIToolBox {

	public String buildURI(String host) {
		return buildURI("http", host, 80, "");
	}
	public String buildURI(String scheme, String host) {
		return buildURI(scheme, host, 80, "");
	}
	public String buildURI(String scheme, String host, String path) {
		return buildURI(scheme, host, 80, path);
	}
	public String buildURI(String scheme, String host, Integer port) {
		return buildURI(scheme, host, port, "");
	}
	public String buildURI(String scheme, String host, Integer port, String path) {
		if (path == null) path = "";
		return scheme+"://"+host+":"+port+path;
	}
	
	public String addFirstParameter(String baseUri, String paramName, String paramValue) {
		return baseUri + "?"+paramName.trim()+"="+paramValue.trim();
	}
	
	public String addParameter(String baseUri, String paramName, String paramValue) {
		if (paramValue != null && paramValue.length() > 0) {
			return baseUri + "&"+paramName.trim()+"="+paramValue.trim();
		}
		return baseUri;
	}

}
