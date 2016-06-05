package apifit.contract;

import java.util.Map;

import apifit.common.ApiFitException;

public interface IAPIClient {

	public void setRequestHeaders(Map<String, String> cookies);
	public Map<String, String> getResponseHeaders();
	//public void setContentEncoder(ContentDecoder decoder1, ContentDecoder decoder2); 
	public void setProxy(String proxy);
	public boolean execute(String contentType, String baseURL,  int checkStatus, String payload) throws ApiFitException;
	public Integer getStatusCode();
	public String getResponseBody();
	public long getRequestTime();
	public StringBuffer getRequestFlow();
	
}
