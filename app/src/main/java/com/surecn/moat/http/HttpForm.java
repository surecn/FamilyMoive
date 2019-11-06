package com.surecn.moat.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


/**
 * User: surecn(surecn@163.com)
 * Date: 2015-08-21
 * Time: 10:16
 */
public class HttpForm implements HttpRequestData {

	private final static String TAG = "AndroidReactive";

	public final static String UTF_8 = "utf-8";

	private final static String BOUNDARY = "--------httppost123";

	private static int TIMEOUT = 30000;

	public enum HttpMethod {
		GET, POST, PUT
	}

	public enum Protocol {
		TCP, OKHTTP, HTTP
	}
	private String mUrl;
	private HttpMethod mMethod;
	private int mTimeout = -1;
	private HashMap<String, String> mTextParameters;
	private ArrayDeque<HttpFileBody> mFileParameters;
	private HashMap<String, String> mHeaderParameters;
	private int mRepeatCount = 0;
	private Protocol mProtocal = Protocol.OKHTTP;

	public HttpForm() {
		this("");
	}

	public HttpForm(String url) {
		mUrl = url;
		mMethod = HttpMethod.GET;
		mTextParameters = new HashMap<String, String>();
		mFileParameters = new ArrayDeque<HttpFileBody>();
		mHeaderParameters = new HashMap<String, String>();
	}

	public void setProtocol(Protocol protocol) {
		mProtocal = protocol;
	}

	public Protocol getProtocal() {
		return mProtocal;
	}

	/**
	 * 设置重试次数
	 */
	public void setRepeatCount(int count) {
		mRepeatCount = count;
	}

	/**
	 * 获取重试次数
	 */
	public int getRepeatCount() {
		return mRepeatCount;
	}

	/**
	 * 设置请求的超时时间
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		mTimeout = timeout;
	}

	/**
	 * 获取改请求的超时时间
	 * @return
	 */
	public int getTimeout() {
		return mTimeout;
	}

	/**
	 * 获取http请求的方法
	 * @return
	 */
	public HttpMethod getMethod() {
		return mMethod;
	}

	/**
	 * 设置请求的方法
	 * @param method
	 */
	public void setMethod(HttpMethod method) {
		this.mMethod = method;
	}

	/**
	 * 获取请求的完整url
	 * @param url
	 */
	public String getUrl() {
		return mUrl;
	}

	/**
	 * 设置请求的完整url
	 * @param url
	 */
	public void setUrl(String url) {
		this.mUrl = url;
	}

	/**
	 * 添加一个参数
	 * @param name
	 * @param value
	 */
	public void addTextParameter(String name, String value) {
		mTextParameters.put(name, value);
	}

	/**
	 * 设置上传的header
	 * @param name
	 * @param value
	 */
	public void addHeaderParameter(String name, String value) {
		mHeaderParameters.put(name, value);
	}

	/**
	 * 添加一个上传的文件
	 * @param httpFileBody
	 */
	public void addFileBody(HttpFileBody httpFileBody) {
		mFileParameters.add(httpFileBody);
	}

	/**
	 * 获取http请求的参数
	 * @return
	 */
	public HashMap<String, String> getTextParameters() {
		return mTextParameters;
	}

	/**
	 * 获取http请求的上传的文件
	 * @return
	 */
	public ArrayDeque<HttpFileBody> getFileParameters() {
		return mFileParameters;
	}

	/**
	 * 获取http请求的header
	 * @return
	 */
	public HashMap<String, String> getHeaderParameters() {
		return mHeaderParameters;
	}

	/**
	 * 清楚所有数据
	 */
	public void clear() {
		if (mTextParameters != null) {
			mTextParameters.clear();
			mTextParameters = null;
		}
		if (mFileParameters != null) {
			mFileParameters.clear();
			mFileParameters = null;
		}
		if (mHeaderParameters != null) {
			mHeaderParameters.clear();
			mHeaderParameters = null;
		}
		mUrl = null;
		mTimeout = -1;
	}

	//普通字符串数据
	private static void writeTextParamters(DataOutputStream ds, HashMap<String, String> textParamters) throws Exception {
		Set<String> keySet = textParamters.keySet();
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String name = it.next();
			String value = textParamters.get(name);
			ds.writeBytes("--" + BOUNDARY + "\r\n");
			ds.writeBytes("Content-Disposition: form-data; name=\"" + name
					+ "\"\r\n");
			ds.writeBytes("\r\n");
			ds.writeBytes(encode(value) + "\r\n");
		}
	}
	//文件数据
	private static void writeFileParamters(DataOutputStream ds, ArrayDeque<HttpFileBody> fileParamters) throws Exception {
		for (HttpFileBody fileBody : fileParamters) {
			String name = fileBody.getName();
			File file = new File(fileBody.getFilePath());
			ds.writeBytes("--" + BOUNDARY + "\r\n");
			ds.writeBytes("Content-Disposition: form-data; name=\"" + name
					+ "\"; filename=\"" + encode(file.getName()) + "\"\r\n");
			ds.writeBytes("Content-Type: " + fileBody.getContentType() + "\r\n");
			ds.writeBytes("\r\n");
			fileBody.writeToStream(ds);
			ds.writeBytes("\r\n");
		}
	}

	//添加结尾数据
	private static void writeParamtersEnd(DataOutputStream ds) throws Exception {
		ds.writeBytes("--" + BOUNDARY + "--" + "\r\n");
		ds.writeBytes("\r\n");
	}

	// 对包含中文的字符串进行转码，此为UTF-8。服务器那边要进行一次解码
	private static String encode(String value) throws Exception{
		return URLEncoder.encode(value, "UTF-8");
	}


	@Override
	public String getRequestUrl() {
		if (mMethod == HttpMethod.GET) {
			return mUrl + "?" + parameterToString();
		}
		return mUrl;
	}

	@Override
	public String getPostData() {
		return urlEncode(parameterToString());
	}

	@Override
	public void writePutData(OutputStream outputStream) {
		DataOutputStream dos = new DataOutputStream(outputStream);
		try {
			writeTextParamters(dos, getTextParameters());
			writeFileParamters(dos, getFileParameters());
			writeParamtersEnd(dos);
		}catch(Exception e) {}
	}

	public String parameterToString() {
		return hashMapToUrlString(getTextParameters());
	}

	//将hashmap中得参数用url方式连接
	public String hashMapToUrlString(HashMap<String, String> parameters) {
		StringBuffer urlbuff = new StringBuffer();
		if (parameters != null && parameters.size() > 0) {
			Set<String> keySet = parameters.keySet();
			for (String key : keySet) {
				urlbuff.append(urlEncode(key)).append("=").append(urlEncode(parameters.get(key))).append("&");
			}
			urlbuff.deleteCharAt(urlbuff.length() - 1);
		}
		return urlbuff.toString();
	}


	public String urlEncode(String data) {
		try {
			return URLEncoder.encode(data, UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return data;
	}
}
