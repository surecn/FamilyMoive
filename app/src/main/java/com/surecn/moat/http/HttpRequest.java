package com.surecn.moat.http;

import android.util.Log;

import com.surecn.moat.exception.HttpRequestException;
import com.surecn.moat.exception.HttpResponseException;
import com.surecn.moat.rest.IHttpResult;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-08-21
 * Time: 10:16
 */
@Deprecated
public class HttpRequest {

	private final static String TAG = "AndroidReactive";
	
	private final static String BOUNDARY = "--------httppost123";
	private static int TIMEOUT = 60000;

	/**
	 * 设置超时时间
	 * @param timeout
	 */
	public static void setDefaultTimeout(int timeout) {
		TIMEOUT = timeout;
	}

	/**
	 * 设置是否重定向
	 * @param followRedirects
	 */
	public static void setDefaultFollowRedirects(boolean followRedirects) {
		HttpURLConnection.setFollowRedirects(followRedirects);
	}

	/**
	 * 进行http请求
	 * @param httpForm
	 * @return
	 * @throws Exception
	 */
	public static IHttpResult request(HttpForm httpForm) {
		HttpURLConnection urlConnection = null;
		try {
			switch(httpForm.getMethod()) {
				case GET:
					urlConnection = openGetRequest(httpForm);
					break;
				case POST:
					urlConnection = openPostRequest(httpForm);
					break;
				case PUT:
					urlConnection = openPutRequest(httpForm);
					break;
			}
		} catch (Exception e) {
			throw new HttpRequestException(e);
		}

		int code = 200;
		try {
			code = urlConnection.getResponseCode();
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		if (code != 200) {
			throw new HttpResponseException(code);
		}
		return new HttpResult(urlConnection);
	}

	private static void setTimeout(HttpURLConnection conn, int timeout) {
		if (timeout > 0) {
			conn.setConnectTimeout(timeout / 2);
			conn.setReadTimeout(timeout / 2);
		} else {
			conn.setConnectTimeout(TIMEOUT / 2);
			conn.setReadTimeout(TIMEOUT / 2);
		}
	}

	private static void setHeaders(HttpURLConnection conn, HashMap<String, String> headers) {
		if (headers != null && headers.size() > 0) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				conn.addRequestProperty(entry.getKey(), entry.getValue());
			}
		}
	}
	
    //connection初始化
	private static HttpURLConnection initConnection(URL url, HttpForm httpForm) throws Exception {
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		setTimeout(urlConnection, httpForm.getTimeout());
		urlConnection.setDoInput(true);
		urlConnection.setUseCaches(false);
		HashMap<String, String> headers = httpForm.getHeaderParameters();
		setHeaders(urlConnection, headers);
		return urlConnection;
	}
	
	private static HttpURLConnection openGetRequest(HttpForm httpForm) throws Exception {
		String urlString = httpForm.getRequestUrl();
		Log.i(TAG, "HTTP Request GET:" + urlString);
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = initConnection(url, httpForm);
		urlConnection.setRequestMethod("GET");
		return urlConnection;
	}
	
	private static HttpURLConnection openPostRequest(HttpForm httpForm) throws Exception {
		Log.i(TAG, "HTTP Request POST:" + httpForm.getUrl());
		URL url = new URL(httpForm.getUrl());
		HttpURLConnection urlConnection = initConnection(url, httpForm);
		urlConnection.setDoOutput(true);
		urlConnection.setRequestMethod("POST");
		DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());
		try {
			dos.writeBytes(httpForm.getPostData());
		} finally {
			dos.flush();
			dos.close();
		}
		return urlConnection;
	}
	
	private static HttpURLConnection openPutRequest(HttpForm httpForm) throws Exception {
		Log.i(TAG, "HTTP Request PUT:" + httpForm.getUrl());
		URL url = new URL(httpForm.getUrl());
		HttpURLConnection urlConnection = initConnection(url, httpForm);
		urlConnection.setDoOutput(true);
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + BOUNDARY);
		OutputStream os = urlConnection.getOutputStream();
		try {
			httpForm.writePutData(os);
		} finally {
			os.flush();
			os.close();
		}
		return urlConnection;
	}

}

