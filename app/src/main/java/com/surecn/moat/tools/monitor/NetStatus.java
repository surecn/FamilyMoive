package com.surecn.moat.tools.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import com.surecn.moat.tools.utils.NetworkUtils;

/**
 * Created by surecn on 15/8/5.
 */
public class NetStatus extends BroadcastReceiver{

    private int mStatus;

    private Context mContext;

    private static NetStatus sNetStatus;

    public static NetStatus getInstance() {
        if (sNetStatus == null) {
            sNetStatus = new NetStatus();
        }
        return sNetStatus;
    }

    private NetStatus() {
    }

    public void init(Context context) {
        mContext = context;
        mStatus = NetworkUtils.getNetWorkType(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, filter);
    }

    public void onDestory() {
        mContext.unregisterReceiver(this);
    }

    public int getNetworkType() {
        return mStatus;
    }

    public boolean isNetworkAvailable() {
        return mStatus != NetworkUtils.NETWORKTYPE_INVALID;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            mStatus = NetworkUtils.getNetWorkType(context);
        }
    }
}
