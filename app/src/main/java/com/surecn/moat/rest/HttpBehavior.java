package com.surecn.moat.rest;

import android.content.Context;
import android.util.Log;

import com.surecn.moat.exception.NetworkInvalidException;
import com.surecn.moat.http.HttpForm;
import com.surecn.moat.rest.interceptor.RequestInterceptor;
import com.surecn.moat.rest.interceptor.ResponseInterceptor;
import com.surecn.moat.utils.NetworkUtils;

/**
 * Created by surecn on 15/8/20.
 */
/*package*/class HttpBehavior {

    /*package*/static Object behavior(RestAdapter adapter, MethodInfo methodInfo) {
        final Context context = adapter.getContext();
        final RequestInterceptor requestInterceptor = adapter.getRequestInterceptor();
        final ResponseInterceptor responseInterceptor = adapter.getResponseInterceptor();

        HttpForm httpForm = adapter.newHttpForm();
        if (httpForm == null) {
            return null;
        }
        //将方法转化为httpform
        HttpForm form = MethodBehavior.behavior(context, httpForm, methodInfo.mMethod, methodInfo.mUrl, methodInfo.mArgs);
        //调用请求钱的拦截器
        if (requestInterceptor != null && !requestInterceptor.intercept(form)) {
            return null;
        }

        IHttpResult result = null;

        do {
            //请求前判断网络是否联通
            if (!NetworkUtils.isNetworkAvailable(context)) {
                throw new NetworkInvalidException();
            }


            //做http请求
            result = adapter.getHttpHandler(form.getProtocal()).request(form);

        } while (result.getResponseCode() != 200 && delay(form));

        if (result == null || result.getResponseCode() != 200) {
            return null;
        }
        //处理请求结果
        Object data = responseInterceptor.intercept(result, methodInfo.mReturnType);
        return data;
    }

    /*package*/static boolean delay(HttpForm form) {
        int repeat = form.getRepeatCount();
        Log.e("HttpBehavior", "delayCount: " + form.getRepeatCount());
        if (repeat > 0) {
            form.setRepeatCount(repeat - 1);
        }
        return repeat > 0 ? true : false;
    }
}
