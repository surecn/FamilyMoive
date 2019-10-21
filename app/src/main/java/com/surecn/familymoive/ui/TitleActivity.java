package com.surecn.familymoive.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.surecn.familymoive.R;

import androidx.annotation.Nullable;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-15
 * Time: 13:17
 */
public class TitleActivity extends BaseActivity implements View.OnClickListener {

    private TextView mViewTitle;

    private ImageView mViewBack;

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
        mViewBack.setOnClickListener(this);
        getWindow().setBackgroundDrawableResource(R.mipmap.bg2);
    }

    protected void setTitle(String title) {
        mViewTitle = getActionBar().getCustomView().findViewById(R.id.title);
        mViewTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
            onBackPressed();
            break;
        }
    }



}
