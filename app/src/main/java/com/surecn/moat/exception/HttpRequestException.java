package com.surecn.moat.exception;

import android.content.Context;
import android.widget.Toast;

public class HttpRequestException extends BaseException {

	public HttpRequestException(Throwable throwable) {
		super(throwable);
	}

	@Override
	public boolean handle(Context context) {
		Toast.makeText(context, "服务器处理错误:" + getMessage() + "]", Toast.LENGTH_LONG).show();
		return false;
	}

}
