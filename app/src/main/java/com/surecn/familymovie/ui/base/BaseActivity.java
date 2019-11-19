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

import static android.widget.RelativeLayout.CENTER_IN_PARENT;

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
        super.setContentView(layoutResID);
        mFocusLayout = new FocusLayout(this);
        bindListener();//绑定焦点变化事件
        addContentView(mFocusLayout,
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));//添加焦点层
        mViewLoading = LayoutInflater.from(this).inflate(R.layout.layout_loading, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(CENTER_IN_PARENT);
        addContentView(mViewLoading, layoutParams);
    }

    private void bindListener() {
        //获取根元素
        View mContainerView = this.getWindow().getDecorView();//.findViewById(android.R.id.content);
        //得到整个view树的viewTreeObserver
        ViewTreeObserver viewTreeObserver = mContainerView.getViewTreeObserver();
        //给观察者设置焦点变化监听
        viewTreeObserver.addOnGlobalFocusChangeListener(mFocusLayout);
    }

    public void showLoading() {
        mViewLoading.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mViewLoading.setVisibility(View.GONE);
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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
