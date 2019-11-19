package com.surecn.familymovie.ui.base;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.surecn.familymovie.R;
import com.surecn.familymovie.utils.DateUtils;

import java.util.Date;

import androidx.annotation.Nullable;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-15
 * Time: 13:17
 */
public class TitleActivity extends BaseActivity {

    private TextView mViewTitle;

    private ImageView mViewBack;

    private TextView mViewTime;


    //广播的注册，其中Intent.ACTION_TIME_CHANGED代表时间设置变化的时候会发出该广播
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.ACTION_TIME_TICK.equals(intent.getAction())){
                updateTime();
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.layout_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(null);
        mViewBack = actionBar.getCustomView().findViewById(R.id.back);
        mViewTime = actionBar.getCustomView().findViewById(R.id.time);
        mViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getWindow().setBackgroundDrawableResource(R.mipmap.bg2);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(broadcastReceiver, filter);

        updateTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void updateTime() {
        mViewTime.setText(DateUtils.toTime(new Date()));
    }

    public void setTitle(CharSequence title) {
        mViewTitle = getActionBar().getCustomView().findViewById(R.id.title);
        mViewTitle.setText(title);
    }

    public void setTitle(int titleId) {
        setTitle(getText(titleId));
    }

    protected void setHiddenBack(boolean flag) {
        View view = getActionBar().getCustomView().findViewById(R.id.back);
        if (flag) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }


}
