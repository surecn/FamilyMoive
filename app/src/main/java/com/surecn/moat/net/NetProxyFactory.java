package com.surecn.moat.net;

import android.content.Context;

/**
 * Created by surecn on 15/8/3.
 */
public class NetProxyFactory {

    private Context mContext;

    private static NetProxyFactory sNetProxyFactory;

    /**
     * 获取适配器
     * @param context
     * @param url 设置url
     * @return
     */
    public static NetProxyFactory getInstance(Context context) {
        if (sNetProxyFactory == null) {
            sNetProxyFactory = new NetProxyFactory(context);
        }
        return sNetProxyFactory;
    }

    private NetProxyFactory(Context context) {
        mContext = context;
    }

    /**
     * 创建Http接口的Service
     * @param cls
     * @param <T>
     * @return
     */
    public HttpProxy.Builder createHttpProxyBuilder() {
        return new HttpProxy.Builder(getContext());
    }

    /*package*/Context getContext() {
        return mContext;
    }


}
