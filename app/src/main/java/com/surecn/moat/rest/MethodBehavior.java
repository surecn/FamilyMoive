package com.surecn.moat.rest;

import android.content.Context;

import com.surecn.moat.http.HttpFileBody;
import com.surecn.moat.http.HttpForm;
import com.surecn.moat.rest.annotation.FIELD;
import com.surecn.moat.rest.annotation.FILE;
import com.surecn.moat.rest.annotation.GET;
import com.surecn.moat.rest.annotation.HEADER;
import com.surecn.moat.rest.annotation.OKHTTP;
import com.surecn.moat.rest.annotation.PATH;
import com.surecn.moat.rest.annotation.POST;
import com.surecn.moat.rest.annotation.PUT;
import com.surecn.moat.rest.annotation.REPEATCOUNT;
import com.surecn.moat.rest.annotation.TCP;
import com.surecn.moat.rest.annotation.TIMEOUT;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by surecn on 15/8/5.
 */
/*package*/class MethodBehavior {

    /*package*/static HttpForm behavior(Context context, HttpForm form, Method method, String url, Object [] args) {
        Annotation[][] annoParams = method.getParameterAnnotations();

        //处理方法的注解
        for(Annotation anno : method.getDeclaredAnnotations()) {
            if (anno instanceof GET) {
                form.setMethod(HttpForm.HttpMethod.GET);
                form.setUrl(url + ((GET) anno).value());
            } else if (anno instanceof POST) {
                form.setMethod(HttpForm.HttpMethod.POST);
                form.setUrl(url + ((POST) anno).value());
            } else if (anno instanceof PUT) {
                form.setMethod(HttpForm.HttpMethod.PUT);
                form.setUrl(url + ((PUT) anno).value());
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
                    form.addFileBody(new HttpFileBody(context, name, String.valueOf(args[i]), ""));
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

}
