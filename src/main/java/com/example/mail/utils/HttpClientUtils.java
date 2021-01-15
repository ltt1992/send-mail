package com.example.mail.utils;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class HttpClientUtils {

	private static String DEFAULT_CHARSET = "UTF-8";
	private CloseableHttpClient httpClient = null;
	private PoolingHttpClientConnectionManager connectionManager;

	private static HttpClientUtils httpHandler = new HttpClientUtils();

	/**
	 * 工厂方法
	 * 
	 * @return
	 */
	public static HttpClientUtils getInstance() {
		return httpHandler;
	}

	private HttpClientUtils() {
		ConnectionKeepAliveStrategy kaStrategy = new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase("timeout")) {
						return Long.parseLong(value) * 1000;
					}
				}
				return 60 * 1000;// 如果没有约定，则默认定义时长为60s
			}
		};
		connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(200);
		connectionManager.setDefaultMaxPerRoute(50);// 例如默认每路由最高50并发，具体依据业务来定
		
		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= 3) {// 如果已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// ssl握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

		httpClient = HttpClients.custom().setConnectionManager(connectionManager).setKeepAliveStrategy(kaStrategy).setRetryHandler(httpRequestRetryHandler).build();
		if(httpClient != null && connectionManager != null){
			//监听空闲的httpclient，并且回收回线程池PoolingHttpClientConnectionManager
			IdleConnectionMonitorThread idelMonitor = new IdleConnectionMonitorThread(connectionManager);
			Thread idelMonitorTh = new Thread(idelMonitor);
			idelMonitorTh.start();
		}
	}
	
	private void config(HttpRequestBase httpRequestBase) {
        httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
        httpRequestBase.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpRequestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
        httpRequestBase.setHeader("Accept-Charset", "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");

        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000).setSocketTimeout(5000).build();
        httpRequestBase.setConfig(requestConfig);
    }

	public String executeGet(String uri) throws IOException {
		try {
			HttpGet httpget = new HttpGet(uri);
			config(httpget);
			CloseableHttpResponse response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将Map转变为NameValuePair
	 * @return
	 */
	protected List<NameValuePair> toList(Map<String, Object> p) {
		if (p == null || p.size() == 0) {
			return null;
		}
		List<NameValuePair> buffer = new ArrayList<NameValuePair>();
		for (String k : p.keySet()) {
			buffer.add(new BasicNameValuePair(k, String.valueOf(p.get(k))));
		}
		return buffer;
	}

	public String executePost(String uri, Map<String, Object> param) throws IOException {
		try {
			List<NameValuePair> plt = toList(param);
			HttpPost httppost = new HttpPost(uri);
			config(httppost);
			httppost.setEntity(new UrlEncodedFormEntity(plt, DEFAULT_CHARSET));
			CloseableHttpResponse response = httpClient.execute(httppost);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public String executePost(String uri, String body, Map<String, String> header) throws IOException {
		try {
			HttpPost httppost = new HttpPost(uri);
			config(httppost);
			if (header != null && header.size() > 0) {
				Set<String> keys = header.keySet();
				for (String key : keys) {
					httppost.addHeader(key, header.get(key));
				}
			}
			httppost.setEntity(new StringEntity(body, DEFAULT_CHARSET));
			CloseableHttpResponse response = httpClient.execute(httppost);
			HttpEntity entity = response.getEntity();
			String rs = EntityUtils.toString(entity);
			consume(response.getEntity());
			return rs;
		} catch (Throwable e) {
			System.out.println("-----------------------http start");
			e.printStackTrace();
			System.out.println("--------------------http end");
		}
		return null;
	}

	public void consume(final HttpEntity entity) throws IOException {
		if (entity == null) {
			return;
		}
		if (entity.isStreaming()) {
			final InputStream instream = entity.getContent();
			if (instream != null) {
				instream.close();
			}
		}
	}

	public static class IdleConnectionMonitorThread extends Thread {

		private final HttpClientConnectionManager connMgr;
		private volatile boolean shutdown = false;

		public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
			super();
			this.connMgr = connMgr;
		}

		@Override
		public void run() {
			try {
				while (!shutdown) {
					synchronized (this) {
						wait(5000);
						connMgr.closeExpiredConnections();
						connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
					}
				}
			} catch (InterruptedException ex) {
				// terminate
			}
		}

		public void shutdown() {
			shutdown = true;
			synchronized (this) {
				notifyAll();
			}
		}

	}
}
