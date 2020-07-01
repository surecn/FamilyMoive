package com.surecn.familymovie.common;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.surecn.familymovie.common.http.NanoHTTPD;
import com.surecn.familymovie.common.samba.httpd.NanoStreamer;

/**
 * Created by ram on 15/1/28.
 */
public class StreamService extends Service implements NanoHTTPD.NanoHTTPDListener {


    @Override
    public void onCreate() {
        super.onCreate();
        NanoStreamer nanoStreamer = NanoStreamer.INSTANCE();
        nanoStreamer.setNanoHTTPDListener(this);
        nanoStreamer.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NanoStreamer.INSTANCE().stop();
    }

    @Override
    public void onServerError() {
        //重启
        NanoStreamer nanoStreamer = NanoStreamer.INSTANCE();
        nanoStreamer.setNanoHTTPDListener(this);
        nanoStreamer.start();
    }
}
