package com.surecn.familymoive.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.surecn.familymoive.R;
import com.surecn.familymoive.Setting;
import com.surecn.familymoive.data.HistoryModel;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.tools.setting.SettingManager;

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
        mViewValueAbout.setText("v" + getVersionName());
    }

    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
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
        }
        SettingManager.getInstance(this).save(Setting.class);
        initData();
    }
}
