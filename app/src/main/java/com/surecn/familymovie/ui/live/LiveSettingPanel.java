package com.surecn.familymovie.ui.live;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.surecn.familymovie.R;
import com.surecn.familymovie.domain.Channel;
import com.surecn.familymovie.ui.player.SettingListPanel;
import com.surecn.familymovie.ui.player.VideoActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-03
 * Time: 19:09
 */
public class LiveSettingPanel extends LinearLayout {

    private WeakReference<LiveActivity> mActivityRef;

    public LiveSettingPanel(Context context) {
        super(context);
    }

    public LiveSettingPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveSettingPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LiveSettingPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mActivityRef = new WeakReference<>((LiveActivity)getContext());

        OnClickListener onClickListener = new OnClickListener(){
            @Override
            public void onClick(View v) {
                LiveActivity activity = mActivityRef.get();
                if (activity == null) {
                    return;
                }
                SettingItem settingItem = (SettingItem) v.getTag();
                if (settingItem.key.equals("favorite")) {
                    activity.favoriteCurrentChannel();
                } else if (settingItem.key.equals("update")) {
                    activity.update();
                }
            }
        };
        Resources res = getResources();
        List<SettingItem> list = new ArrayList<>();
        list.add(new SettingItem(res.getString(R.string.live_setting_favorite), "favorite"));
        list.add(new SettingItem(res.getString(R.string.live_setting_update), "update"));
        for (SettingItem settingItem : list) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_live_setting, this, false);
            view.setTag(settingItem);
            fillData(view, settingItem);
            view.setOnClickListener(onClickListener);
            addView(view);
        }
    }

    public void show(boolean isFavorite) {
        update(isFavorite);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                getChildAt(0).requestFocus();
            }
        }, 50);
    }

    private void update(boolean isFavorite) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            SettingItem settingItem = (SettingItem) view.getTag();
            if (settingItem != null && settingItem.key.equals("favorite")) {
                settingItem.title = getResources().getString(isFavorite ? R.string.live_setting_unfavorite : R.string.live_setting_favorite);
                fillData(view, settingItem);
            }
        }
    }

    public void fillData(View view, SettingItem item) {
        TextView textView = view.findViewById(R.id.title);
        textView.setText(item.title);
    }

    public class SettingItem {
        String title;
        String key;
        int icon;
        String value;

        public SettingItem(String title, String key) {
            this.title = title;
            this.key = key;
        }

    }

}
