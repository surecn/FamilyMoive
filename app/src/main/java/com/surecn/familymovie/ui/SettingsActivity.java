package com.surecn.familymovie.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.surecn.familymovie.R;
import com.surecn.familymovie.Setting;
import com.surecn.familymovie.data.HistoryModel;
import com.surecn.familymovie.ui.base.TitleActivity;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.tools.setting.SettingManager;
import com.surecn.moat.tools.utils.AppUtils;

public class SettingsActivity extends TitleActivity implements View.OnClickListener{

    private TextView mViewValueOnlineSubtitle;

    private TextView mViewValueAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle(getString(R.string.setting));

        Schedule.linear(new UITask() {
            @Override
            public void run(TaskSchedule taskSchedule, Object result) {
                findViewById(R.id.online_subtitle).requestFocus();
            }
        }, 300);

        findViewById(R.id.online_subtitle).setOnClickListener(this);
        findViewById(R.id.clean).setOnClickListener(this);
        findViewById(R.id.feedback).setOnClickListener(this);

        mViewValueOnlineSubtitle = findViewById(R.id.online_subtitle_value);
        mViewValueAbout = findViewById(R.id.about_value);

        initData();
    }

    public void initData() {
        if (Setting.onlineSubtitle == 1) {
            mViewValueOnlineSubtitle.setText(R.string.setting_online_true);
        } else {
            mViewValueOnlineSubtitle.setText(R.string.setting_online_false);
        }
        mViewValueAbout.setText("v" + AppUtils.getAppVersionName(this));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.online_subtitle:
                Setting.onlineSubtitle = ~Setting.onlineSubtitle;
                break;
            case R.id.clean:
                new HistoryModel(this).clear();
                showToast(R.string.setting_toast_msg);
                break;
            case R.id.feedback:
                Intent intent = new Intent(this, FeedbackActivity.class);
                startActivity(intent);
                break;
        }
        SettingManager.getInstance(this).save(Setting.class);
        initData();
    }
}
