package com.surecn.familymovie.ui.player;

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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-03
 * Time: 19:09
 */
public class PlaySettingPanel extends LinearLayout {

    private WeakReference<VideoActivity> mActivityRef;

    public PlaySettingPanel(Context context) {
        super(context);
    }

    public PlaySettingPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlaySettingPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PlaySettingPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mActivityRef = new WeakReference<>((VideoActivity)getContext());

        View.OnClickListener onClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SettingItem settingItem = (SettingItem) v.getTag();
                save(settingItem);
                fillData(v, settingItem);
            }
        };
        Resources res = getResources();
        List<SettingItem> list = new ArrayList<>();
        list.add(new SettingItem(getResources().getString(R.string.video_setting_audio), R.mipmap.menu_subtitle, "audio"));
        list.add(new SettingItem(res.getString(R.string.video_setting_subtitle), R.mipmap.menu_subtitle, "subtitle"));
        list.add(new SettingItem(res.getString(R.string.video_setting_scale), R.mipmap.menu_scale, "scale"));
        list.add(new SettingItem(res.getString(R.string.video_setting_play_order), R.mipmap.menu_order, "order"));
        for (SettingItem settingItem : list) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_video_setting, this, false);
            view.setTag(settingItem);
            fillData(view, settingItem);
            view.setOnClickListener(onClickListener);
            addView(view);
        }

    }

    public void update(SettingListPanel.TrackItem audio, SettingListPanel.TrackItem subtitle) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            SettingItem settingItem = (SettingItem) view.getTag();
            if (settingItem.key.equals("audio")) {
                if (audio.tag == 0) {
                    settingItem.value = audio.text;
                } else if (audio.tag == 1) {
                    settingItem.value = getResources().getString(R.string.video_setting_list_local);
                } else if (audio.tag == 2) {
                    settingItem.value = getResources().getString(R.string.video_setting_list_network);
                }
                fillData(view, settingItem);
            } else if (settingItem.key.equals("subtitle")) {
                if (subtitle.tag == 0) {
                    settingItem.value = subtitle.text;
                } else if (subtitle.tag == 1) {
                    settingItem.value = getResources().getString(R.string.video_setting_list_local);
                } else if (subtitle.tag == 2) {
                    settingItem.value = getResources().getString(R.string.video_setting_list_network);
                }
                fillData(view, settingItem);
            }
        }
    }

    public void save(SettingItem settingItem) {
        VideoActivity videoActivity = mActivityRef.get();
        if (videoActivity == null) {
            return;
        }
        if (settingItem.key.equals("audio")) {
            videoActivity.showAudioList();
        } if (settingItem.key.equals("subtitle")) {
            videoActivity.showSubtitleList();
        } else if (settingItem.key.equals("scale")) {
            int index = videoActivity.getSharedPreferences("video_setting", Context.MODE_PRIVATE).getInt(settingItem.key, 0);
            index++;
            if (index > 4) {
                index = 0;
            }
            videoActivity.getSharedPreferences("video_setting", Context.MODE_PRIVATE).edit().putInt(settingItem.key, index).commit();
            videoActivity.setting(settingItem.key, index);
        } else if (settingItem.key.equals("order")) {
            int index = videoActivity.getSharedPreferences("video_setting", Context.MODE_PRIVATE).getInt(settingItem.key, 0);
            index = index == 1 ? 0 : 1;
            settingItem.value = String.valueOf(index);
            videoActivity.getSharedPreferences("video_setting", Context.MODE_PRIVATE).edit().putInt(settingItem.key, index).commit();
        }
    }

    public String getText(SettingItem settingItem) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("video_setting", Context.MODE_PRIVATE);
        if (settingItem.key.equals("audio")) {
            if (TextUtils.isEmpty(settingItem.value)) {
                return getResources().getString(R.string.video_setting_subtitle_unknow);
            }
            return settingItem.value;
        } else if (settingItem.key.equals("subtitle")) {
            if (TextUtils.isEmpty(settingItem.value)) {
                return getResources().getString(R.string.video_setting_subtitle_unknow);
            }
            return settingItem.value;
        } else if (settingItem.key.equals("scale")) {
            int index = sharedPreferences.getInt(settingItem.key, 0);
            return getResources().getStringArray(R.array.subtitle_values)[index];
        } else if (settingItem.key.equals("order")) {
            int index = sharedPreferences.getInt(settingItem.key, 0);
            return getResources().getStringArray(R.array.order_values)[index];
        }
        return null;
    }

    public void fillData(View view, SettingItem item) {
        ImageView imageView = view.findViewById(R.id.icon);
        imageView.setImageResource(item.icon);
        TextView textView = view.findViewById(R.id.title);
        textView.setText(item.title);
        TextView valueView = view.findViewById(R.id.value);
        valueView.setText(getText(item));
    }

    public class SettingItem {
        String title;
        String key;
        int icon;
        String value;

        public SettingItem(String title, int icon, String key) {
            this.title = title;
            this.icon = icon;
            this.key = key;
        }

    }

}
