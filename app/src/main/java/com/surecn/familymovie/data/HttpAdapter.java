package com.surecn.familymovie.data;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.surecn.familymovie.BuildConfig;
import com.surecn.moat.exception.HttpResponseException;
import com.surecn.moat.http.HttpForm;
import com.surecn.moat.net.IHttpResult;
import com.surecn.moat.net.NetProxyFactory;
import com.surecn.moat.net.httphandler.OkHttpHandler;
import com.surecn.moat.net.interceptor.RequestInterceptor;
import com.surecn.moat.net.interceptor.ResponseInterceptor;
import com.surecn.moat.tools.log;
import com.surecn.moat.tools.utils.AppUtils;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by surecn on 15/7/29.
 */
public class HttpAdapter {

    private static SubTitleService sSubTitleService;

    private static PlayerService sPlayerService;

    private static Gson sGson;

    private static Context sContext;

    static {
        sGson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        }).create();
    }

    public static void init(Context context) {
        sContext = context;
    }

    public static SubTitleService getSubTitleService() {
        if (sSubTitleService == null) {
            sSubTitleService = NetProxyFactory.getInstance(sContext).createHttpProxyBuilder()
                    .setUrl("http://api.assrt.net/v1/")
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public boolean intercept(HttpForm form) {
                            form.addTextParameter("token", "QV79WOwoYBammXVBU7o2e14C2CEvaKEJ");
                            return false;
                        }
                    })
                    .setResponseInterceptor(new ResponseInterceptor() {
                        @Override
                        public Object intercept(IHttpResult result, Type t) {
                            String text = AppUtils.IOUtils.readString(result.getResponseStream());
                            log.d("HTTP ResponseText:" + text);
                            SubResult res = null;
                            try {
                                res = sGson.fromJson(text, type(SubResult.class, t));
                            } catch (IllegalStateException e) {
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (res != null) {
                                if (res.code == 0) {
                                    return res.data.subs;
                                }
                                throw new HttpResponseException(res.code);
                            }
                            return null;
                        }
                    })
                    .setHttHandler(new OkHttpHandler())
                    .create(SubTitleService.class);
        }
        return sSubTitleService;
    }

    public static PlayerService getPlayerService() {
        if (sPlayerService == null) {
            sPlayerService = NetProxyFactory.getInstance(sContext).createHttpProxyBuilder()
                    .setUrl(BuildConfig.SERVER_URL)//"https://www.smyyh.cn/"
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public boolean intercept(HttpForm form) {
                            form.addTextParameter("channel", BuildConfig.CHANNEL);
                            return false;
                        }
                    })
                    .setResponseInterceptor(new ResponseInterceptor() {
                        @Override
                        public Object intercept(IHttpResult result, Type t) {
                            String text = AppUtils.IOUtils.readString(result.getResponseStream());
                            log.d("HTTP ResponseText:" + text);
                            DataResult res = null;
                            try {
                                res = sGson.fromJson(text, type(DataResult.class, t));
                                if (res != null) {
                                    if (res.code == 0) {
                                        return res.data;
                                    }
                                    throw new HttpResponseException(res.code);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    })
                    .setHttHandler(new OkHttpHandler())
                    .create(PlayerService.class);
        }
        return sPlayerService;
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

    public static class DataResult<T> {
        @Expose
        @SerializedName("data")
        T data;

        @Expose
        @SerializedName("code")
        int code;
    }

    public static class SubResult<T> {

        @Expose
        @SerializedName("sub")
        Sub<T> data;

        @Expose
        @SerializedName("status")
        int code;
    }

    public static class Sub<T> {
        @Expose
        @SerializedName("subs")
        T subs;
    }
}
