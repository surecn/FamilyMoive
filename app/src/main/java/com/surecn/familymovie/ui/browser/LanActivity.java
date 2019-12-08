package com.surecn.familymovie.ui.browser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.surecn.familymovie.R;
import com.surecn.familymovie.common.LanManager;
import com.surecn.familymovie.common.SmbManager;
import com.surecn.familymovie.data.FileCache;
import com.surecn.familymovie.domain.FileItem;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.core.task.Task;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jcifs.netbios.NbtAddress;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-13
 * Time: 12:41
 */
public class LanActivity extends FileActivity {

    private ArrayList<FileItem> mList = new ArrayList<>();

    private HashSet<FileItem> mGetName = new HashSet<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    updateList();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listServer();
    }

    @Override
    public void onClick(final FileItem fileItem) {
        switch (fileItem.type) {
            case 0:
                Intent intent = new Intent(this, SmbActivity.class);
                intent.putExtra("path", fileItem.path);
                startActivity(intent);
                break;
        }
    }

    private void listServer() {
        setTitle(getString(R.string.main_lan));
        showTitlebarLoading();
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule work, Object result) {
                setData(mList);
                List<FileItem> cacheList = FileCache.getInstance().read(LanActivity.this, FileCache.KEY_LANSERVER);
                if (cacheList != null) {
                    mList.addAll(cacheList);
                    handler.sendEmptyMessage(1000);
                }
                LanManager.discover(LanActivity.this);
                List<String> ips = LanManager.readArp();
                getShareServer(ips);
                getServerName();
            }
        }).next(new UITask() {
            @Override
            public void run(TaskSchedule work, Object result) {
                updateList();
                FileCache.getInstance().save(LanActivity.this, FileCache.KEY_LANSERVER, getData());
                hideTitlebarLoading();
            }
        }).start();
    }

    public void getShareServer(List<String> ips) {
        for (String ip : ips) {
            FileItem fileItem = SmbManager.getServer(ip);
            if (fileItem != null && !mList.contains(fileItem)) {
                mList.add(fileItem);
                mGetName.add(fileItem);
                handler.sendEmptyMessage(1000);
            }
        }
    }

    public void getServerName() {
        for (FileItem fileItem : mGetName) {
            try {
                NbtAddress nbtAddress = NbtAddress.getByName(fileItem.name);
                String firstName = nbtAddress.firstCalledName();
                String calledName = nbtAddress.nextCalledName();
                fileItem.name = calledName;
                handler.sendEmptyMessage(1000);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

}
