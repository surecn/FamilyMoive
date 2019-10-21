package com.surecn.moat.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * User: surecn(surecn@163.com)
 * Date: 2016-05-27
 * Time: 13:39
 */
    public class SafeHandler<T> extends Handler {

    private WeakReference<T> mRef;

    public SafeHandler(T t) {
        mRef = new WeakReference<T>(t);
    }

    public SafeHandler(T t, Looper looper) {
        super(looper);
        mRef = new WeakReference<T>(t);
    }

    protected T getContext() {
        return mRef.get();
    }

    @Override
    public void dispatchMessage(Message msg) {
        if (getContext() == null) {
            return;
        }
        super.dispatchMessage(msg);
    }
}
