package com.surecn.familymovie.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.surecn.familymovie.R;
import com.surecn.familymovie.Setting;
import com.surecn.familymovie.UserTrack;
import com.surecn.familymovie.common.GridPaddingDecoration;
import com.surecn.familymovie.data.HttpAdapter;
import com.surecn.familymovie.domain.RecommendInfo;
import com.surecn.familymovie.domain.UpdateInfo;
import com.surecn.familymovie.ui.base.TitleActivity;
import com.surecn.familymovie.ui.browser.DiskActivity;
import com.surecn.familymovie.ui.browser.LanActivity;
import com.surecn.familymovie.ui.live.LiveActivity;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.Task;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.tools.setting.SettingManager;
import com.surecn.moat.tools.utils.AppUtils;
import com.surecn.moat.tools.utils.DensityUtils;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-22
 * Time: 17:43
 */
public class MainActivity extends TitleActivity implements View.OnClickListener {

    private long mBackPressTime = -1;

    private RecyclerView mViewList;

    private ItemAdapter mAdapter;

    private List<MainItem> mList;

    private ImageView mImageView;

    private TextView mViewText;

    String[] permissions = new String[]{Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE};

    //2、创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPerrrmissionList中
    List<String> mPermissionList = new ArrayList<>();

    private final int mRequestCode = 100;//权限请求码

    /**
     * 不再提示权限时的展示对话框
     */
    AlertDialog mPermissionDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
        setHiddenBack(true);
        register();
        initView();
        //delayFocus(findViewById(R.id.history));
        initData();
    }

    private void initView() {
        mViewText = findViewById(R.id.text);
        mImageView = findViewById(R.id.image);
        mViewList = findViewById(R.id.list);
        mViewList.setLayoutManager(new GridLayoutManager(this, 3) {
            @Override
            public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {
                return super.requestChildRectangleOnScreen(parent, child, rect, true, focusedChildVisible);
            }
        });
        mAdapter = new ItemAdapter();
        mViewList.setAdapter(mAdapter);
        mViewList.addItemDecoration(new GridPaddingDecoration(this, DensityUtils.dp2px(this, 20), DensityUtils.dp2px(this, 20), DensityUtils.dp2px(this, 20), DensityUtils.dp2px(this, 20)));
    }

    private void register() {
        if (TextUtils.isEmpty(Setting.uuid)) {
            Setting.uuid = AppUtils.getUUID();
            SettingManager.getInstance(this).save(Setting.class);
        }
    }

    private void initData() {
        mList = new ArrayList<>();
        mList.add(new MainItem("history", R.mipmap.main_history, Color.parseColor("#2FE487"), R.string.main_history));
        mList.add(new MainItem("local", R.mipmap.main_file, Color.parseColor("#2F9CFF"), R.string.main_local));
        mList.add(new MainItem("lan", R.mipmap.main_lan, Color.parseColor("#FD8723"), R.string.main_lan));
        mList.add(new MainItem("live", R.mipmap.live, Color.parseColor("#009688"), R.string.main_live));
        mList.add(new MainItem("setting", R.mipmap.main_setting, Color.parseColor("#FB4C2B"), R.string.main_setting));
        mList.add(new MainItem("setting", 0, Color.GRAY, R.string.main_future));
        mAdapter.notifyDataSetChanged();

//        Schedule.linear(new Task() {
//            @Override
//            public void run(TaskSchedule taskSchedule, Object result) {
//                RecommendInfo recommendInfo = HttpAdapter.getPlayerService().recommend();
//                taskSchedule.sendNext(recommendInfo);
//            }
//        }).next(new UITask<RecommendInfo>() {
//            @Override
//            public void run(TaskSchedule taskSchedule, RecommendInfo result) {
//                initRecommend(result);
//            }
//        });
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule taskSchedule, Object result) {
                UpdateInfo updateInfo = HttpAdapter.getPlayerService().update(AppUtils.getAppVersionCode(MainActivity.this));
                taskSchedule.sendNext(updateInfo);
            }
        }).next(new UITask<UpdateInfo>() {
            @Override
            public void run(TaskSchedule taskSchedule, UpdateInfo result) {
                if (result != null && result.update == 1) {
                    UpdateManager updateManager = new UpdateManager(MainActivity.this);
                    updateManager.checkUpdateInfo(result);
                }
                if (Build.VERSION.SDK_INT >= 23) {//6.0才用动态权限
                    initPermission();
                }
            }
        }).start();
    }

    private void initRecommend(RecommendInfo recommendInfo) {
        Glide.with(this).load(recommendInfo.getImages().get(0)).into(mImageView);
        mViewText.setText(recommendInfo.getText());
    }

    @Override
    public void onBackPressed() {
        if (mBackPressTime == -1) {
            mBackPressTime = System.currentTimeMillis();
            showToast(R.string.exit_msg);
        } else if ((System.currentTimeMillis() - mBackPressTime) > 1500) {
            mBackPressTime = -1;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        MainItem item = (MainItem) v.getTag();
        if (item.key.equals("history")) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            UserTrack.mark(UserTrack.MAIN_HISTORY);
        } else if (item.key.equals("local")) {
            Intent intent = new Intent(this, DiskActivity.class);
            startActivity(intent);
            UserTrack.mark(UserTrack.MAIN_LOCAL);
        } else if (item.key.equals("lan")) {
            Intent intent = new Intent(this, LanActivity.class);
            startActivity(intent);
            UserTrack.mark(UserTrack.MAIN_LAN);
        } else if (item.key.equals("setting")) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            UserTrack.mark(UserTrack.MAIN_SETTTING);
        } else if (item.key.equals("live")) {
            Intent intent = new Intent(this, LiveActivity.class);
            startActivity(intent);
            UserTrack.mark(UserTrack.MAIN_LIVE);
        }
    }

    public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_main, parent, false);
            view.setOnClickListener(MainActivity.this);
            ItemHolder itemHolder = new ItemHolder(view);
            view.setTag(itemHolder);
            return itemHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            holder.setData(mList.get(position));
            holder.index = position;
        }

        @Override
        public int getItemViewType(int position) {
            Object obj = mList.get(position);
            return (obj != null && obj instanceof String) ? 0 : 1;
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }
    }

    public static class MainItem {
        String key;
        int icon;
        int backgroundColor;
        int title;

        public MainItem(String key, int icon, int backgroundColor, int title) {
            this.key = key;
            this.icon = icon;
            this.backgroundColor = backgroundColor;
            this.title = title;
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        public  int index;
        private ImageView viewIcon;
        private TextView viewTitle;
        private ViewGroup root;

        public ItemHolder(View view) {
            super(view);
            root = (ViewGroup) view;
            viewIcon = root.findViewById(R.id.icon);
            viewTitle = root.findViewById(R.id.title);
        }

        public void setData(MainItem item) {
            root.setTag(item);
            if (item.icon > 0) {
                viewIcon.setImageResource(item.icon);
                root.setEnabled(true);
                root.setFocusable(true);
            } else {
                viewIcon.setImageDrawable(null);
                root.setEnabled(false);
                root.setFocusable(false);
            }
            viewTitle.setText(item.title);
            GradientDrawable drawable=new GradientDrawable();
            drawable.setCornerRadius(DensityUtils.dp2px(root.getContext(), 10f));
            drawable.setColor(item.backgroundColor);// 设置颜色
            root.setBackground(drawable);
        }
    }

    //权限判断和申请
    private void initPermission() {

        mPermissionList.clear();//清空没有通过的权限

        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }

        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        }else{
            //说明权限都已经通过，可以做你想做的事情去
        }
    }


    //请求权限后回调的方法
    //参数： requestCode  是我们自己定义的权限请求码
    //参数： permissions  是我们请求的权限名称数组
    //参数： grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                showPermissionDialog();//跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
            }else{
                //全部权限通过，可以进行下一步操作。。。

            }
        }

    }

    private void showPermissionDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("已禁用权限，请手动授予")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();

                            Uri packageURI = Uri.parse("package:" + getPackageName());
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            cancelPermissionDialog();

                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    //关闭对话框
    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }

}
