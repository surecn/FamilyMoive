package com.surecn.moat.exception;

import android.content.Context;


public abstract class BaseException extends RuntimeException {
	
	public BaseException() {
	}

	public BaseException(Throwable throwable) {
		super(throwable);
	}
	
	public BaseException(String message) {
		super(message);
	}
	
	public abstract boolean handle(Context context);

}
