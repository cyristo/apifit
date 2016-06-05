package apifit.api;

import static apifit.common.ApiFitConstants.DELETE;
import static apifit.common.ApiFitConstants.GET;
import static apifit.common.ApiFitConstants.JSON_CONTENT_TYPE;
import static apifit.common.ApiFitConstants.LINE_SEPARATOR;
import static apifit.common.ApiFitConstants.POST;
import static apifit.common.ApiFitConstants.PUT;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import apifit.common.ApiFitException;
import apifit.contract.IAPIClient;
import apifit.json.JsonToolBox;

public class APIClient implements IAPIClient {

	private String requestType = null;
	private Integer statusCode = null;
	private String responseBody = null;
	private long requestTime;
	private Map<String, String> responseHeaders = null;
	private Map<String, String> requestHeaders = null;
	private StringBuffer requestFlow;

	public APIClient(String requestType) {
		this.requestType = requestType;
	}

	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public void addRequestHeader(String name, String value) {
		if (requestHeaders == null) requestHeaders = new Hashtable<String, String>();
		requestHeaders.put(name, value);
	}
	
	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}
	
	public String getResponseHeader(String name) {
		return responseHeaders.get(name);
	}
/*
	public void setContentEncoder(ContentDecoder decoder1, ContentDecoder decoder2) {
		if (decoder2 != null) {
			RestAssured.config = config().decoderConfig(decoderConfig().contentDecoders(decoder1, decoder2));
		} else {
			RestAssured.config = config().decoderConfig(decoderConfig().contentDecoders(decoder1));
		}
	}
*/
	public void setProxy(String proxy) {
		
	}

	public boolean execute(String contentType, String URL, int checkStatus) throws ApiFitException {
		return execute(contentType, URL, checkStatus, null);
	}

	public boolean execute(String contentType, String URL, int checkStatus, String payload) throws ApiFitException {

		boolean wellDone = false;
		requestFlow = new StringBuffer().append("=================> API EXECUTION FLOW AT ").append(LocalDateTime.now()).append(" <=================");
		requestFlow.append(LINE_SEPARATOR).append("REQUEST ACTION   : ").append(requestType).append(" ").append(URL);
		if (requestHeaders != null) requestFlow.append(LINE_SEPARATOR).append("REQUEST HEADERS  : ").append(requestHeaders);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpUriRequest httpRequest = getProperRequest(URL, contentType, payload);
		CloseableHttpResponse response = null; 
		requestTime = -1;
		
		try {
			long before = System.currentTimeMillis();
			response = httpclient.execute(httpRequest);
			long after = System.currentTimeMillis();
			requestTime = after - before;
			parseResponseEntity(response, payload);
		} catch (ClientProtocolException e) {
			requestFlow.append(LINE_SEPARATOR).append("REQUEST ERROR    : ").append("ClientProtocolException  -> ").append(e.getMessage());
			throw new ApiFitException(e);
		} catch (IOException e) {
			requestFlow.append(LINE_SEPARATOR).append("REQUEST ERROR    : ").append("IOException  -> ").append(e.getMessage());
			throw new ApiFitException(e);
		} finally {
		    try {
				response.close();
			} catch (IOException e) {
				requestFlow.append(LINE_SEPARATOR).append("REQUEST ERROR    : ").append("IOException  -> ").append(e.getMessage());
				throw new ApiFitException(e);
			}
		}

		statusCode = response.getStatusLine().getStatusCode();
		requestFlow.append(LINE_SEPARATOR).append("RESPONSE STATUS  : ").append(statusCode);
		requestFlow.append(LINE_SEPARATOR).append("RESPONSE TIME    : ").append(requestTime);
		requestFlow.append(LINE_SEPARATOR).append("RESPONSE HEADERS :");
		
		HeaderIterator it = response.headerIterator();
		responseHeaders = new Hashtable<String, String>();
		while (it.hasNext()) {
		    Header header = it.nextHeader(); 
		    requestFlow.append(LINE_SEPARATOR).append(header);
			responseHeaders.put(header.getName(), header.getValue());
		}
		
		requestFlow.append(LINE_SEPARATOR).append("RESPONSE BODY    :").append(LINE_SEPARATOR).append(responseBody);

		if (statusCode == checkStatus) {
			wellDone = true;
		}
		
		return wellDone;
	}

	public Integer getStatusCode() {
		return statusCode;
	}
	/*
	public String getHeaders() {
		return headers;
	}
	*/
	public String getResponseBody() {
		return responseBody;
	}

	public long getRequestTime() {
		return requestTime;
	}
	
	public StringBuffer getRequestFlow() {
		return requestFlow;
	}
	
	private HttpUriRequest getProperRequest(String URL, String contentType, String payload) {
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setRedirectsEnabled(true)
				.setMaxRedirects(10)
				.setAuthenticationEnabled(true)
				.setCircularRedirectsAllowed(true)
				.setContentCompressionEnabled(true)
				.build();
		
		if (requestType.equals(GET)) {
			HttpGet req = new HttpGet(URL);
			req.setConfig(requestConfig);
			return req;
		}
		if (requestType.equals(POST)) {
			HttpPost req = new HttpPost(URL);
			req.setConfig(requestConfig);
			req.setEntity(new StringEntity(payload, ContentType.create(contentType, "UTF-8")));
			return req;
		}
		if (requestType.equals(PUT)) {
			HttpPut req = new HttpPut(URL);
			req.setConfig(requestConfig);
			req.setEntity(new StringEntity(payload, ContentType.create(contentType, "UTF-8")));
			return req;
		}
		if (requestType.equals(DELETE)) {
			HttpDelete req = new HttpDelete(URL);
			req.setConfig(requestConfig);
			return req;
		}
		return null;
		
	}
	private void parseResponseEntity(CloseableHttpResponse response, String payload) throws ApiFitException {
		HttpEntity entity = response.getEntity();
		if (entity.getContentType().equals(JSON_CONTENT_TYPE)) {
			JsonToolBox toolBox = new JsonToolBox();
				try {
					responseBody = toolBox.formatPretty(EntityUtils.toString(entity));
				} catch (ParseException e) {
					requestFlow.append(LINE_SEPARATOR).append("RESPONSE ERROR   : ").append("ParseException  -> ").append(e.getMessage());
					throw new ApiFitException(e);
				} catch (IOException e) {
					requestFlow.append(LINE_SEPARATOR).append("RESPONSE ERROR   : ").append("IOException  -> ").append(e.getMessage());
					throw new ApiFitException(e);
				}
				if (payload != null) payload = toolBox.formatPretty(payload);
		} else {
			try {
				responseBody = EntityUtils.toString(entity);
			} catch (ParseException e) {
				requestFlow.append(LINE_SEPARATOR).append("RESPONSE ERROR   : ").append("ParseException  -> ").append(e.getMessage());
				throw new ApiFitException(e);
			} catch (IOException e) {
				requestFlow.append(LINE_SEPARATOR).append("RESPONSE ERROR   : ").append("IOException  -> ").append(e.getMessage());
				throw new ApiFitException(e);
			}
		}
	}
/*
	private RequestSpecification initRequestSpecification(String contentType, String payload) {
		RequestSpecification reqSpec;
		*
		Specify the cookies that'll be sent with the request as Map e.g:
		 Map<String, String> cookies = new HashMap<String, String>();
		 cookies.put("username", "John");
		 cookies.put("token", "1234");
		 given().cookies(cookies).then().expect().body(equalTo("username, token")).when().get("/cookie");
		 * 
		if (cookies != null) {
			if (payload != null) {
				reqSpec = given()
						.config(RestAssured.config().redirect(redirectConfig().followRedirects(true)))
						.contentType(contentType)
						.cookies(cookies)
						//.sessionId(sessionId)
						.body(payload)
						.when();				
			} else {
				reqSpec = given()
						.config(RestAssured.config().redirect(redirectConfig().followRedirects(true)))
						.contentType(contentType)
						.cookies(cookies)
						//.sessionId(sessionId)
						.when();
			}

		} else {
			if (payload != null) {
				reqSpec = given()
						.config(RestAssured.config().redirect(redirectConfig().followRedirects(true)))
						.contentType(contentType)
						.body(payload)
						.when();
			} else {
				reqSpec = given()
						.config(RestAssured.config().redirect(redirectConfig().followRedirects(true)))
						.contentType(contentType)
						.when();	
			}
		}
		return reqSpec;
	}
*/

}
