package com.surecn.familymovie.ui.browser;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.surecn.familymovie.R;
import com.surecn.familymovie.data.FavoriteModel;
import com.surecn.familymovie.domain.FileItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-03
 * Time: 19:09
 */
public class SettingPanel extends LinearLayout {

    private FileItem fileItem;

    private boolean isFavorite;

    private WeakReference<FileActivity> mActivityRef;

    public SettingPanel(Context context) {
        super(context);
    }

    public SettingPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mActivityRef = new WeakReference<>((FileActivity)getContext());

//        OnClickListener onClickListener = new OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                FileActivity activity = mActivityRef.get();
//                if (activity == null) {
//                    return;
//                }
//                SettingItem settingItem = (SettingItem) v.getTag();
//                if (settingItem.key.equals("favorite")) {
//                    //activity.favorite();
//                    FavoriteModel favoriteModel = new FavoriteModel(getContext());
//                    if (!isFavorite) {
//                        favoriteModel.addFolder(fileItem.path, fileItem.server);
//                    } else {
//                        favoriteModel.deleteFolder(fileItem.path);
//                    }
//                    setVisibility(View.GONE);
//                }
//            }
//        };
        Resources res = getResources();
        List<SettingItem> list = new ArrayList<>();
        list.add(new SettingItem(res.getString(R.string.live_setting_favorite), "favorite"));
//        for (SettingItem settingItem : list) {
//            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_live_setting, this, false);
//            view.setTag(settingItem);
//            fillData(view, settingItem);
//            view.setOnClickListener(onClickListener);
//            addView(view);
//        }
    }

    public void show(FileItem fileItem) {
        this.fileItem = fileItem;
        isFavorite = new FavoriteModel(getContext()).isExist(fileItem.path);
        setVisibility(View.VISIBLE);
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
                settingItem.title = getResources().getString(isFavorite ? R.string.unfavorite : R.string.favorite);
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
