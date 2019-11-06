package com.surecn.moat.tools;

/**
 * User: surecn(surecn@163.com)
 * Date: 2016-07-01
 * Time: 14:41
 */
public abstract class Singleton<T> {
    private T mInstance;

    protected abstract T create();

    public final T get() {
        synchronized (this) {
            if (mInstance == null) {
                mInstance = create();
            }
            return mInstance;
        }
    }
}
