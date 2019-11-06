package com.surecn.moat.sqliteadmin;

import com.surecn.familymoive.common.http.NanoHTTPD;

import java.util.Map;

public class HttpServer extends NanoHTTPD {

	private OnHttpRequest onHttpRequest;

	public HttpServer(String hostname, int port) {
		super(hostname, port);
	}

	public HttpServer(int port) {
		super(port);
	}

	public void setOnHttpRequest(OnHttpRequest onHttpRequest) {
		this.onHttpRequest = onHttpRequest;
	}

	public static interface OnHttpRequest {
		public Response onHttpRequest(String uri, Map<String, String> paramter);
	}

	@Override
	public Response serve(IHTTPSession session) {
		/*我在这里做了一个限制，只接受POST请求。这个是项目需求。*/
		Response response = onHttpRequest.onHttpRequest(session.getUri(), session.getParms());
		return response;
	}
}
