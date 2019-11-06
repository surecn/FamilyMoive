package com.surecn.familymoive.ui;

import android.content.Intent;
import android.os.Bundle;

import com.surecn.familymoive.DefaultConfig;
import com.surecn.familymoive.R;
import com.surecn.familymoive.common.SambaManager;
import com.surecn.familymoive.common.StreamService;
import com.surecn.familymoive.common.samba.IConfig;
import com.surecn.familymoive.domain.FileItem;
import com.surecn.familymoive.ui.player.VideoActivity;
import com.surecn.familymoive.utils.UriUtil;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.core.task.Task;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-13
 * Time: 12:41
 */
public class FamilyNetActivity extends FileActivity {

    private SambaManager mSambaManager;

    private IConfig mIconfig;

    private FileItem mCurrentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, StreamService.class));

        mIconfig = new DefaultConfig();
        mSambaManager = new SambaManager(mIconfig);

        listServer();
    }

    @Override
    public void onBackPressed() {
        String uri = mSambaManager.getCurrentRemoteFolder();
        if (uri == null) {
            super.onBackPressed();
            return;
        }
        uri = UriUtil.getUriParent(uri);
        if (uri == null) {
            listRoot(mSambaManager.getHost());
        } else if (uri.equals("smb://")) {
            listServer();
        } else {
            listFile(uri);
        }
    }

    @Override
    public void onClick(final FileItem fileItem) {
        switch (fileItem.type) {
            case 0:
                listRoot(fileItem.path);
                break;
            case 1:
                listFile(fileItem.path);
                break;
            case 2:
                play(fileItem.path);
                break;
        }
    }

    private void play(String path) {
        VideoActivity.startActivity(this, path, 0);
    }

    private void listServer() {
        setTitle(getString(R.string.family_net));
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule work, Object result) {
                updateData(mSambaManager.listWorkGroup());
            }
        }).next(new UITask() {
            @Override
            public void run(TaskSchedule work, Object result) {
                updateList();
            }
        }).start();
    }

    private void listFile(final String path) {
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule work, Object result) {
                updateData(mSambaManager.setCurrentFolder(path));
            }
        }).next(new UITask() {
            @Override
            public void run(TaskSchedule work, Object result) {
                setTitle(UriUtil.getUriName(path));
                updateList();
            }
        }).start();
    }

    private void listRoot(final String path) {
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule work, Object result) {
                mSambaManager.updateHost(path);
                updateData(mSambaManager.listAll());
            }
        }).next(new UITask() {
            @Override
            public void run(TaskSchedule work, Object result) {
                setTitle(UriUtil.getUriName(path));
                updateList();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, StreamService.class));
    }


}
