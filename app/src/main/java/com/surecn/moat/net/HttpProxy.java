package com.surecn.moat.net;

import android.content.Context;
import android.util.Log;
import com.surecn.moat.exception.NetworkInvalidException;
import com.surecn.moat.http.HttpFileBody;
import com.surecn.moat.http.HttpForm;
import com.surecn.moat.net.annotation.FIELD;
import com.surecn.moat.net.annotation.FILE;
import com.surecn.moat.net.annotation.GET;
import com.surecn.moat.net.annotation.HEADER;
import com.surecn.moat.net.annotation.OKHTTP;
import com.surecn.moat.net.annotation.PATH;
import com.surecn.moat.net.annotation.POST;
import com.surecn.moat.net.annotation.PUT;
import com.surecn.moat.net.annotation.REPEATCOUNT;
import com.surecn.moat.net.annotation.TCP;
import com.surecn.moat.net.annotation.TIMEOUT;
import com.surecn.moat.net.httphandler.IHttpHandler;
import com.surecn.moat.net.httphandler.OkHttpHandler;
import com.surecn.moat.net.interceptor.RequestInterceptor;
import com.surecn.moat.net.interceptor.ResponseInterceptor;
import com.surecn.moat.tools.utils.AppUtils;
import java.lang.annotation.Annotation;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-05
 * Time: 14:01
 */
public class HttpProxy extends BaseProxy {

    private RequestInterceptor mRequestInterceptor;

    private ResponseInterceptor mResponseInterceptor;

    private IHttpHandler mHttHandler;

    private String mUrl;

    public HttpProxy(Context context) {
        super(context);
    }

    private boolean delay(HttpForm form) {
        int repeat = form.getRepeatCount();
        Log.e("HttpBehavior", "delayCount: " + form.getRepeatCount());
        if (repeat > 0) {
            form.setRepeatCount(repeat - 1);
        }
        return repeat > 0 ? true : false;
    }

    @Override
    public Object handler(MethodInfo methodInfo) {
        //将方法转化为httpform
        HttpForm form = convert(methodInfo);
        //调用请求钱的拦截器
        if (mRequestInterceptor != null && mRequestInterceptor.intercept(form)) {
            return null;
        }

        IHttpResult result = null;
        do {
            //请求前判断网络是否联通
            if (!AppUtils.NetworkUtils.isNetworkAvailable(getContext())) {
                throw new NetworkInvalidException();
            }
            //做http请求
            if (mHttHandler == null) {
                mHttHandler = new OkHttpHandler();
            }
            result = mHttHandler.request(form);
        } while (result != null && result.getResponseCode() != 200 && delay(form));

        if (result == null || result.getResponseCode() != 200) {
            return null;
        }
        if (mResponseInterceptor != null) {
            return mResponseInterceptor.intercept(result, methodInfo.mReturnType);
        }
        return result.getResponseText();
    }

    private HttpForm convert(MethodInfo methodInfo) {
        Object [] args = methodInfo.mArgs;
        Annotation[][] annoParams = methodInfo.mMethod.getParameterAnnotations();
        HttpForm form = new HttpForm();
        //处理方法的注解
        for(Annotation anno : methodInfo.mMethod.getDeclaredAnnotations()) {
            if (anno instanceof GET) {
                form.setMethod(HttpForm.HttpMethod.GET);
                form.setUrl(mUrl + ((GET) anno).value());
            } else if (anno instanceof POST) {
                form.setMethod(HttpForm.HttpMethod.POST);
                form.setUrl(mUrl + ((POST) anno).value());
            } else if (anno instanceof PUT) {
                form.setMethod(HttpForm.HttpMethod.PUT);
                form.setUrl(mUrl + ((PUT) anno).value());
            } else if (anno instanceof TIMEOUT) {
                form.setTimeout(((TIMEOUT) anno).value());
            } else if (anno instanceof TCP) {
                form.setProtocol(HttpForm.Protocol.TCP);
            } else if (anno instanceof OKHTTP) {
                form.setProtocol(HttpForm.Protocol.OKHTTP);
            }
        }

        //处理参数的注解
        for (int i = 0, len = annoParams.length; i < len; i++) {
            String name = "";
            for (Annotation an : annoParams[i]) {
                if (an instanceof FILE) {
                    name = ((FILE) an).value();
                    form.addFileBody(new HttpFileBody(getContext(), name, String.valueOf(args[i]), ""));
                    break;
                } else if (an instanceof FIELD) {
                    name = ((FIELD) an).value();
                    form.addTextParameter(name, String.valueOf(args[i]));
                    break;
                } else if (an instanceof HEADER) {
                    name = ((HEADER) an).value();
                    form.addTextParameter(name, String.valueOf(args[i]));
                } else if (an instanceof PATH) {
                    name = ((PATH) an).value();
                    form.setUrl(form.getUrl().replace("{" + name + "}", String.valueOf(args[i])));
                } else if (an instanceof REPEATCOUNT) {
                    int count = ((REPEATCOUNT) an).value();
                    if (count >= 0) {
                        form.setRepeatCount(count);
                    }
                }
            }
        }
        return form;
    }

    public static class Builder {

        private HttpProxy mHttpProxy;

        public Builder(Context context) {
            mHttpProxy = new HttpProxy(context);
        }

        public Builder setUrl(String url) {
            mHttpProxy.mUrl = url;
            return this;
        }

        public Builder setRequestInterceptor(RequestInterceptor requestInterceptor) {
            mHttpProxy.mRequestInterceptor = requestInterceptor;
            return this;
        }

        public Builder setResponseInterceptor(ResponseInterceptor responseInterceptor) {
            mHttpProxy.mResponseInterceptor = responseInterceptor;
            return this;
        }

        public Builder setHttHandler(IHttpHandler resultHandler) {
            mHttpProxy.mHttHandler = resultHandler;
            return this;
        }

        public <T> T create(Class<T> cls) {
            return mHttpProxy.create(cls);
        }
    }


}
