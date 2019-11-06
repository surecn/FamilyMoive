package com.surecn.moat.net.interceptor;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.surecn.moat.net.IHttpResult;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * Created by surecn on 15/8/20.
 * 处理{code: 200, msg: "", data:{}}这种返回格式的数据
 * service方法的返回结果返回data的值
 */
public class JSONResponseInterceptor implements ResponseInterceptor {

    private final static String TAG = "AndroidReactive";

    final static Gson sGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Override
    public Object intercept(IHttpResult result, Type t) {
        String response = result.getResponseText();
        try {
            Log.e(TAG, "HTTP ResponseText:" + response);
            Record res = null;
            try {
                //res = objectMapper.readValue(response, javaType);
                res = sGson.fromJson(response, type(Record.class, t));
                Log.e("HTTP", "HTTP ResponseText:" + res.data);
                if (res != null) {
                    if (res.code == 0) {
                        return res.data;
                    } else {
                        return null;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "HTTP ERROR:", e);
                e.printStackTrace();
            }
        } finally {
        }
        return null;
    }

    static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return Object.class;
            }
        };
    }

    public static class Record<T> {
        @Expose
        @SerializedName("data")
        T data;

        @Expose
        @SerializedName("code")
        int code;
    }
}
