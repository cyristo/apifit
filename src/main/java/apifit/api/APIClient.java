package apifit.api;

import static apifit.common.ApiFitConstants.AUTH_HOST;
import static apifit.common.ApiFitConstants.AUTH_PORT;
import static apifit.common.ApiFitConstants.AUTH_PSWD;
import static apifit.common.ApiFitConstants.AUTH_USER;
import static apifit.common.ApiFitConstants.DELETE;
import static apifit.common.ApiFitConstants.GET;
import static apifit.common.ApiFitConstants.JSON_CONTENT_TYPE;
import static apifit.common.ApiFitConstants.LINE_SEPARATOR;
import static apifit.common.ApiFitConstants.POST;
import static apifit.common.ApiFitConstants.PUT;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.ArrayListMultimap;

import apifit.common.ApiFitCache;
import apifit.common.ApiFitException;
import apifit.contract.IAPIClient;
import apifit.json.JsonToolBox;

public class APIClient implements IAPIClient {

	private String requestType = null;
	private Integer statusCode = null;
	private String responseBody = null;
	private long requestTime;
	private ArrayListMultimap<String, String> responseHeaders = null;
	private ArrayListMultimap<String, String> requestHeaders = null;
	private StringBuffer requestFlow;
	private CookieStore cookieStore = null;
	private CredentialsProvider credsProvider = null;

	public APIClient(String requestType) {
		this.requestType = requestType;
	}

	public void setRequestHeaders(Hashtable<String, String> requestHeaderTable) {
		if (requestHeaderTable == null) return;
		Enumeration enumer = requestHeaderTable.keys();
		requestHeaders = ArrayListMultimap.create();
		while (enumer.hasMoreElements()) {
			String key = (String) enumer.nextElement();
			String value = requestHeaderTable.get(key);
			requestHeaders.put(key, value);
		}
	}
	
	public void setRequestHeaders(ArrayListMultimap<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public void addRequestHeader(String name, String value) {
		if (requestHeaders == null) requestHeaders = ArrayListMultimap.create();
		requestHeaders.put(name, value);
	}
	
	public ArrayListMultimap<String, String> getResponseHeaders() {
		return responseHeaders;
	}
	
	public List<String> getResponseHeader(String name) {
		return responseHeaders.get(name);
	}

	public Map<String, String> getCookiesToSet() {
		Map<String, String> cookiesInResponse = null;
		List<String> cookies = getResponseHeader("Set-Cookie");
		if (cookies != null && !cookies.isEmpty()) {
			cookiesInResponse = new Hashtable<String, String>();
			Iterator iter = cookies.iterator();
			while (iter.hasNext()) {
				String cookie = (String) iter.next();
				String name = StringUtils.substringBefore(cookie, "=");
				String value = StringUtils.substringBetween(cookie, "=", ";");
				cookiesInResponse.put(name, value);
			}
		}
		return cookiesInResponse;
	}
	public void setCookies(Hashtable<String, String> cookies) {
		if (cookies != null) {
			cookieStore = new BasicCookieStore();
			// Populate cookies if needed
			Enumeration<String> enumer = cookies.keys();
			BasicClientCookie cookie = null;
			StringBuffer cookieString = new StringBuffer();
			while (enumer.hasMoreElements()) {
				String name = (String) enumer.nextElement();
				String value = cookies.get(name);
				cookie = new BasicClientCookie(name, value);
				cookie.setDomain(".domain.ws");
				cookie.setPath("/");
				cookieStore.addCookie(cookie);
				cookieString.append(name).append("=").append(value);
				if (enumer.hasMoreElements()) cookieString.append("; ");
			}
			addRequestHeader("Cookie", cookieString.toString());
		}
	}
	
	public void setAuthParams(Hashtable<String, String> authParams) {
		if (authParams != null) {
			credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(
			    new AuthScope(authParams.get(AUTH_HOST), new Integer(authParams.get(AUTH_PORT))), 
			    new UsernamePasswordCredentials(authParams.get(AUTH_USER), authParams.get(AUTH_PSWD)));
		}
	}

	public boolean execute(String contentType, String URL, int checkStatus) throws ApiFitException {
		return execute(contentType, URL, checkStatus, null);
	}

	public boolean execute(String contentType, String URL, int checkStatus, String payload) throws ApiFitException {

		boolean wellDone = false;
		HttpUriRequest httpRequest = getProperRequest(URL, contentType, payload);
		CloseableHttpClient httpclient = getProperClient();

		CloseableHttpResponse response = null; 
		requestTime = -1;
		try {
			parseRequestEntity(httpRequest, payload);
			long before;
			//if (cookieStore != null) {
				/*
				HttpContext localContext = new BasicHttpContext();
				localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
				before = System.currentTimeMillis();
				response = httpclient.execute(httpRequest, localContext);
				*/
			if (cookieStore != null || credsProvider != null) {
				HttpClientContext context = HttpClientContext.create();
				if (credsProvider != null) context.setCredentialsProvider(credsProvider);
				if (cookieStore != null) context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
				before = System.currentTimeMillis();
				response = httpclient.execute(httpRequest, context);
			} else {
				before = System.currentTimeMillis();
				response = httpclient.execute(httpRequest);
			}
			long after = System.currentTimeMillis();
			statusCode = response.getStatusLine().getStatusCode();
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
				if (response != null) response.close();
			} catch (IOException e) {
				requestFlow.append(LINE_SEPARATOR).append("REQUEST ERROR    : ").append("IOException  -> ").append(e.getMessage());
				throw new ApiFitException(e);
			}
		}
		
		requestFlow.append(LINE_SEPARATOR).append("RESPONSE STATUS  : ").append(statusCode);
		requestFlow.append(LINE_SEPARATOR).append("RESPONSE TIME    : ").append(requestTime);
		requestFlow.append(LINE_SEPARATOR).append("RESPONSE HEADERS :");
		
		HeaderIterator it = response.headerIterator();
		responseHeaders = ArrayListMultimap.create();
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
				.setCookieSpec(CookieSpecs.DEFAULT)
				.setRedirectsEnabled(true)
				.setMaxRedirects(10)
				.setAuthenticationEnabled(true)
				.setCircularRedirectsAllowed(true)
				.setContentCompressionEnabled(true)
                .setConnectTimeout(20 * 1000)
                .setConnectionRequestTimeout(20 * 1000)
                .setSocketTimeout(20 * 1000)
				.build();
		
		if (requestType.equals(GET)) {
			HttpGet req = new HttpGet(URL);
			req.setConfig(requestConfig);
			addHeadersToRequest(req);
			return req;
		}
		if (requestType.equals(POST)) {
			HttpPost req = new HttpPost(URL);
			req.setConfig(requestConfig);
			req.setEntity(new StringEntity(payload, ContentType.create(contentType, "UTF-8")));
			addHeadersToRequest(req);
			return req;
		}
		if (requestType.equals(PUT)) {
			HttpPut req = new HttpPut(URL);
			req.setConfig(requestConfig);
			req.setEntity(new StringEntity(payload, ContentType.create(contentType, "UTF-8")));
			addHeadersToRequest(req);
			return req;
		}
		if (requestType.equals(DELETE)) {
			HttpDelete req = new HttpDelete(URL);
			req.setConfig(requestConfig);
			addHeadersToRequest(req);
			return req;
		}
		return null;
		
	}
	
	private CloseableHttpClient getProperClient() throws ApiFitException {
		
		HttpClientBuilder builder = HttpClients.custom()
				.setSSLSocketFactory(getSSLConnectionSocketFactory())
				.setRedirectStrategy(new LaxRedirectStrategy());
		
		if (cookieStore != null) {
			builder.setDefaultCookieStore(cookieStore);
		}
		
		APIHost proxyHost = ApiFitCache.getInstance().getProxy();
		if (proxyHost != null) {
			HttpHost host = new HttpHost(proxyHost.getHost(), proxyHost.getPort(), "http");
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(host);
			builder.setRoutePlanner(routePlanner);
		}
		
		/*
		if (cookieStore != null) {
			httpclient = HttpClients.custom()
					.setSSLSocketFactory(getSSLConnectionSocketFactory())
					.setRedirectStrategy(new LaxRedirectStrategy())
					.setDefaultCookieStore(cookieStore)
					.build();
		} else {
			httpclient = HttpClients.custom()
					.setSSLSocketFactory(getSSLConnectionSocketFactory())
					.setRedirectStrategy(new LaxRedirectStrategy())
					.build();
		}
		*/
		return builder.build();
	}
	
	private HttpUriRequest addHeadersToRequest(HttpUriRequest request) {
		if (requestHeaders != null) {
			Collection<Map.Entry<String, String>> coll = requestHeaders.entries();
			Iterator iter = coll.iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
				request.addHeader(entry.getKey(), entry.getValue());
			}
		}
		return request;
	}
	private void parseRequestEntity(HttpUriRequest httpRequest, String payload) {
		requestFlow = new StringBuffer().append("=================> API EXECUTION FLOW AT ").append(LocalDateTime.now()).append(" <=================");
		requestFlow.append(LINE_SEPARATOR).append("REQUEST ACTION   : ").append(requestType).append(" ").append(httpRequest.getURI());
		if (requestHeaders != null) {
			Header[] headers = httpRequest.getAllHeaders();
			requestFlow.append(LINE_SEPARATOR).append("REQUEST HEADERS  : ");
			for (int i = 0; i < headers.length; i++) {
				Header header = headers[i];
				requestFlow.append(LINE_SEPARATOR).append(header.getName()).append(":").append(header.getValue());
			}
		}
		if (payload != null) {
			requestFlow.append(LINE_SEPARATOR).append("REQUEST BODY   : ");
			requestFlow.append(LINE_SEPARATOR).append(payload);
		}
		
	}

	private void parseResponseEntity(CloseableHttpResponse response, String payload) throws ApiFitException {
		HttpEntity entity = response.getEntity();
		if (entity.getContentType().toString().equals(JSON_CONTENT_TYPE)) {
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
	
	private SSLConnectionSocketFactory getSSLConnectionSocketFactory() throws ApiFitException {
	    SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom()
			        .loadTrustMaterial(null, new TrustStrategy() {
						public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			                return true;
						}
			        })
			        .build();
		} catch (KeyManagementException e) {
			throw new ApiFitException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new ApiFitException(e);
		} catch (KeyStoreException e) {
			throw new ApiFitException(e);
		}

	    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
	            sslContext, NoopHostnameVerifier.INSTANCE);
	    
	    return sslsf;
	}

}
