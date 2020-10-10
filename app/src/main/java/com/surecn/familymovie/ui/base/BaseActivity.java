package com.surecn.familymovie.ui.base;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-09-14
 * Time: 17:50
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.surecn.familymovie.R;
import com.surecn.familymovie.ui.view.FocusLayout;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.UITask;
import com.umeng.analytics.MobclickAgent;
import androidx.annotation.Nullable;


public class BaseActivity extends Activity {

    private FocusLayout mFocusLayout;

    private View mViewLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(R.layout.base, null);
        ViewStub viewStub = view.findViewById(R.id.content);
        viewStub.setLayoutResource(layoutResID);
        View content = viewStub.inflate();

        mFocusLayout = view.findViewById(R.id.focus);
        mFocusLayout.bindListener(content);
//        addContentView(mFocusLayout,
//                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));//添加焦点层

        mViewLoading = view.findViewById(R.id.loading);

//
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        layoutParams.addRule(CENTER_IN_PARENT);
//        addContentView(mViewLoading, layoutParams);

        super.setContentView(view);
    }

    public void showLoading() {
        mViewLoading.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mViewLoading.setVisibility(View.GONE);
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    public void showToast(int resId) {
        showToast(getString(resId));
    }

    protected void delayFocus(final View view) {
        Schedule.linear(new UITask() {
            @Override
            public void run(TaskSchedule taskSchedule, Object result) {
                view.requestFocus();
            }
        }, 200);
    }
}
