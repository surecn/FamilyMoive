package com.surecn.moat.exception;

import android.content.Context;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-05
 * Time: 14:44
 */
public class HttpException extends BaseException {

    public HttpException(String msg) {
        super(msg);
    }

    @Override
    public boolean handle(Context context) {
        return false;
    }
}
