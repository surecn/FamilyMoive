package com.surecn.moat.http;

import com.surecn.moat.net.IHttpResult;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-11-05
 * Time: 16:53
 */
public class OkHttpResult implements IHttpResult {

    private Response mResponse;

    public OkHttpResult(Response response) {
        mResponse = response;
    }

    @Override
    public int getResponseCode() {
        return mResponse.code();
    }

    @Override
    public InputStream getResponseStream() {
        return mResponse.body().byteStream();
    }

    @Override
    public String getResponseText() {
        try {
            return mResponse.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public long getContentLength() {
        return mResponse.body().contentLength();
    }

    @Override
    public String getHeader(String key) {
        return mResponse.header(key);
    }
}
