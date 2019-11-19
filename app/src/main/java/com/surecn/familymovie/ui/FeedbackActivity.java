package com.surecn.familymovie.ui;

import android.os.Bundle;
import android.widget.ImageView;

import com.surecn.familymovie.R;
import com.surecn.familymovie.Setting;
import com.surecn.familymovie.ui.base.BaseActivity;
import com.surecn.familymovie.utils.ZXingUtils;
import com.surecn.moat.tools.utils.AppUtils;
import com.surecn.moat.tools.utils.DensityUtils;

import androidx.annotation.Nullable;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-13
 * Time: 15:37
 */
public class FeedbackActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_feedback);

        ImageView imageView = findViewById(R.id.image);
        String url = "https://www.smyyh.cn/player/feedback/mobile?uuid="+ Setting.uuid +"&versionName=" + AppUtils.getAppVersionName(this) + "&versionCode=" + AppUtils.getAppVersionCode(this);
        imageView.setImageBitmap(ZXingUtils.createQRImage(url, DensityUtils.dp2px(this, 200), DensityUtils.dp2px(this, 200)));
    }
}
