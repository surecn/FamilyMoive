package com.surecn.familymovie.ui.browser;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.surecn.familymovie.R;
import com.surecn.familymovie.domain.StorageVolume;
import com.surecn.familymovie.ui.base.TitleActivity;
import com.surecn.familymovie.utils.SizeUtils;
import com.surecn.familymovie.utils.StorageUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import androidx.annotation.Nullable;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-26
 * Time: 13:44
 */
public class DiskActivity extends TitleActivity implements View.OnClickListener {

    private List<HashMap<String, Object>> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);
        setTitle("存储卡");
        //updateData(FileManager.listFile(null, 0));
        initView();
    }

    public void initView() {
        mList = new ArrayList<>();
//        HashMap<String, Object> inner = getInnerDirectory();
//        mList.add(inner);
        List<HashMap<String, Object>> outer = getAllDirectory();
        if (outer != null) {
            mList.addAll(outer);
        }

        LinearLayout linearLayout = findViewById(R.id.root);
        for (HashMap<String, Object> map : mList) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_local_disk, linearLayout, false);
            TextView nameView = view.findViewById(R.id.name);
            nameView.setText(map.get("name").toString());
            ImageView iconView = view.findViewById(R.id.icon);
            iconView.setImageResource((int)map.get("icon"));
            File file = (File) map.get("file");
            TextView sizeView = view.findViewById(R.id.size);
            sizeView.setText("已用:" + SizeUtils.toSimple(file.getUsableSpace()) + "\n" + "总共:" + SizeUtils.toSimple(file.getTotalSpace()));
            linearLayout.addView(view);
            view.setOnClickListener(this);
            view.setTag(map);
        }
    }

    public List<HashMap<String, Object>> getAllDirectory() {
        List<HashMap<String, Object>> list = new ArrayList<>();
        ArrayList<StorageVolume> volumeArrayList = StorageUtils.getVolume(this);
        if (volumeArrayList != null) {
            for (StorageVolume volume : volumeArrayList) {
                if (!"mounted".equals(volume.getState())) {
                    continue;
                }
                File file = new File(volume.getPath());
                if (file.getTotalSpace() <= 0) {
                    continue;
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put("icon", volume.isPrimary() ? R.mipmap.local : R.mipmap.usb);
                String name = volume.getDescription(this);
                map.put("name", name == null ? getString(R.string.unknow) : name);
                map.put("file", file);
                list.add(map);
            }
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        HashMap<String, Object> map = (HashMap<String, Object>) v.getTag();
        Intent intent = new Intent(this, LocalFileActivity.class);
        intent.putExtra("title", map.get("name").toString());
        File file = (File) map.get("file");
        intent.putExtra("file", file.getPath());
        startActivity(intent);
    }

}
