package apifit.contract;

import com.google.common.collect.ArrayListMultimap;

import apifit.api.APIHost;
import apifit.common.ApiFitException;

public interface IAPIClient {

	public void setRequestHeaders(ArrayListMultimap<String, String> requestHeaders);
	public ArrayListMultimap<String, String> getResponseHeaders();
	//public void setProxy(APIHost proxy);
	public boolean execute(String contentType, String baseURL,  int checkStatus, String payload) throws ApiFitException;
	public Integer getStatusCode();
	public String getResponseBody();
	public long getRequestTime();
	public StringBuffer getRequestFlow();
	
}
