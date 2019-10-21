package com.surecn.moat.rest;

import android.content.Context;
import android.webkit.URLUtil;

import com.surecn.moat.http.HttpForm;
import com.surecn.moat.rest.annotation.OKHTTP;
import com.surecn.moat.rest.annotation.TCP;
import com.surecn.moat.rest.httphandler.IHttpHandler;
import com.surecn.moat.rest.httphandler.OkHttpHandler;
import com.surecn.moat.rest.httphandler.TcpHandler;
import com.surecn.moat.rest.interceptor.RequestInterceptor;
import com.surecn.moat.rest.interceptor.ResponseInterceptor;
import com.surecn.moat.rest.interceptor.StringResponseInterceptor;

import java.util.HashMap;

/**
 * Created by surecn on 15/8/3.
 */
public class RestAdapter {

    private Context mContext;

    private RequestInterceptor mRequestInterceptor;

    private ResponseInterceptor mResponseInterceptor;

    private IHttpHandler mDefaultHandler;

    private HashMap<HttpForm.Protocol, IHttpHandler> mHttpHandler = new HashMap<HttpForm.Protocol, IHttpHandler>(); {
        try {
            mHttpHandler.put(HttpForm.Protocol.OKHTTP, OkHttpHandler.class.newInstance());
            mHttpHandler.put(HttpForm.Protocol.TCP, TcpHandler.class.newInstance());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private String mURL;

    private static RestAdapter sRestAdapter;

    private Class<? extends HttpForm> mFormClass = HttpForm.class;

    public void setFormClass(Class<? extends HttpForm> formClass) {
        this.mFormClass = formClass;
    }

    public HttpForm newHttpForm() {
        try {
            return mFormClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取适配器
     * @param context
     * @param url 设置url
     * @return
     */
    public static RestAdapter getAdapter(Context context, String url) {
        if (URLUtil.isHttpUrl(url) ||
                URLUtil.isHttpsUrl(url)) {
            if (sRestAdapter == null) {
                sRestAdapter = new RestAdapter(context, url);
            }
            return sRestAdapter;
        }
        return null;
    }

    private RestAdapter(Context context, String url) {
        mContext = context;
        mURL = url;
    }

    /**
     * 创建Http接口的Service
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> cls) {
        if (cls.isInterface()) {
            RestProxy proxy = new RestProxy(this, mURL);
            return proxy.create(cls);
        }
        return null;
    }

    /**
     * 设置http请求之前的拦截器
     * @param requestInterceptor
     */
    public void setRequestInterceptor(RequestInterceptor requestInterceptor) {
        if (requestInterceptor != null) {
            mRequestInterceptor = requestInterceptor;
        }
    }

    /*package*/RequestInterceptor getRequestInterceptor() {
        return mRequestInterceptor;
    }

    /**
     * 设置Response的拦截器,缺省下StringResponseInterceptor
     * @param responseInterceptor 可使用,StringResponseInterceptor,JSONResponseInterceptor,也可以实现ResponseInterceptor接口
     */
    public void setResponseInterceptor(ResponseInterceptor responseInterceptor) {
        if (responseInterceptor != null) {
            mResponseInterceptor = responseInterceptor;
        }
    }

    /*package*/ResponseInterceptor getResponseInterceptor() {
        if (mResponseInterceptor == null) {
            mResponseInterceptor = new StringResponseInterceptor();
        }
        return mResponseInterceptor;
    }

    /*package*/IHttpHandler getHttpHandler(HttpForm.Protocol protocol) {
        IHttpHandler iHttpHandler = mHttpHandler.get(protocol);
        if (mHttpHandler == null) {
            if (mDefaultHandler == null) {
                mDefaultHandler = new OkHttpHandler();
            }
            iHttpHandler = mDefaultHandler;
        }
        return iHttpHandler;
    }

    /*package*/Context getContext() {
        return mContext;
    }
}
