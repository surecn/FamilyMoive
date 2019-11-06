package com.surecn.moat.http;


import com.surecn.moat.exception.HttpResponseException;
import com.surecn.moat.tools.log;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Set;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-11-05
 * Time: 15:56
 */
public class OKHttpUtils {

    private final static String BOUNDARY = "--------httppost123";
    public static MediaType sText = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static MediaType sPut = MediaType.parse("multipart/form-data; boundary=" + BOUNDARY);
    private static HashMap<HttpClientConfig, OkHttpClient> sHttpClients = new HashMap<>();

    public static Response request(HttpForm form) {
        try {
            Request.Builder requestBuilder = new Request.Builder();
            switch (form.getMethod()) {
                case GET:{
                    requestBuilder.url(form.getRequestUrl());
                    requestBuilder.method(form.getMethod().name(), null);
                    break;}
                case POST:{
                    requestBuilder.url(form.getUrl());
                    requestBuilder.method(form.getMethod().name(), RequestBody.create(sText, form.getPostData()));
                    break;}
                case PUT:{
                    requestBuilder.url(form.getUrl());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        form.writePutData(baos);
                        requestBuilder.method(form.getMethod().name(), RequestBody.create(sPut, baos.toByteArray()));
                    } finally {
                        baos.flush();
                        baos.close();
                    }
                }
            }
            log.d("HTTP URL:" + form.getRequestUrl());
            if (log.LOG_DEBUG) {
                HashMap<String, String> textParamaters = form.getTextParameters();
                for (String key : textParamaters.keySet()) {
                    log.d("HTTP PARAM:" + key + "=" + textParamaters.get(key));
                }
            }
//            Set<String> set = form.getHeaderParameters().keySet();
//            for (String key : set) {
//                requestBuilder.header(key, form.getHeaderParameters().get(key));
//            }
            Request request = requestBuilder.build();
            HttpClientConfig clientConfig = getHttpClientConfig(form);
            OkHttpClient okHttpClient = getOkHttpClient(clientConfig);
            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful())
                throw new HttpResponseException(response.code());
            return response;
        } catch (Exception e) {
            log.e(e);
        }
        return null;
    }

    private static OkHttpClient getOkHttpClient(HttpClientConfig config) {
        OkHttpClient okHttpClient = sHttpClients.get(config);
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
            sHttpClients.put(config, okHttpClient);
        }
        return okHttpClient;
    }

    private static HttpClientConfig getHttpClientConfig(HttpForm form) {
        HttpClientConfig httpClientConfig = new HttpClientConfig();
        httpClientConfig.connectTimeout(form.getTimeout() / 3).readTimeout(form.getTimeout() / 3).writeTimeout(form.getTimeout() / 3);
        return httpClientConfig;
    }

    public static class HttpClientConfig {
        private long mConnectionTimeout;
        private long mReadTimeout;
        private long mWriteTimeout;
        public HttpClientConfig connectTimeout(long timeout) {
            mConnectionTimeout = timeout;
            return this;
        }

        public HttpClientConfig readTimeout(long timeout) {
            mReadTimeout = timeout;
            return this;
        }

        public HttpClientConfig writeTimeout(long timeout) {
            mWriteTimeout = timeout;
            return this;
        }

        public long getConnectionTimeout() {
            return mConnectionTimeout;
        }

        public long getReadTimeout() {
            return mReadTimeout;
        }

        public long getWriteTimeout() {
            return mWriteTimeout;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof HttpClientConfig)) {
                return false;
            }
            HttpClientConfig clientConfig = (HttpClientConfig) obj;
            if (mConnectionTimeout == clientConfig.mConnectionTimeout
                    && mReadTimeout == clientConfig.mReadTimeout
                    && mWriteTimeout == clientConfig.mWriteTimeout) {
                return true;
            }
            return super.equals(obj);
        }
    }
}
