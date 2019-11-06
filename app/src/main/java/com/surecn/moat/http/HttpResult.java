package com.surecn.moat.http;

import android.util.Log;

import com.surecn.moat.net.IHttpResult;
import com.surecn.moat.tools.utils.AppUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-08-21
 * Time: 10:16
 */
public class HttpResult implements IHttpResult {

	private final static String TAG = "AndroidReactive";
	
	private HttpURLConnection mUrlConnection;
	
	public HttpResult(HttpURLConnection urlConnection) {
		mUrlConnection = urlConnection;
	}
	
	public int getResponseCode() {
		try {
			return mUrlConnection.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public InputStream getResponseStream() {
		try {
			return mUrlConnection.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getResponseText() {
		try {
			return AppUtils.IOUtils.readString(mUrlConnection.getInputStream());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			mUrlConnection.disconnect();
		}
		return null;
	}
	
	public long getContentLength() {
		return mUrlConnection.getContentLength();
	}

	@Override
	public String getHeader(String key) {
		return mUrlConnection.getHeaderField(key);
	}

}
