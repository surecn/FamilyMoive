package com.surecn.moat.exception;

import android.content.Context;
import android.widget.Toast;
import java.util.HashMap;

public class HttpResponseException extends BaseException {

	private int mCode;

	private static HashMap<Integer, String> mCodeMessage = new HashMap<Integer, String>();

	public static void registerErrorCode(int code, String message) {
		mCodeMessage.put(code, message);
	}

	public static void unregisterErrorCode(int code) {
		mCodeMessage.remove(code);
	}

	public HttpResponseException(int code) {
		mCode = code;
	}

	@Override
	public boolean handle(Context context) {
		if (mCodeMessage.containsKey(mCode)) {
			Toast.makeText(context, mCodeMessage.get(mCode), Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, "服务器处理错误--code:" + mCode + "]", Toast.LENGTH_LONG).show();
		}
		return false;
	}

}
