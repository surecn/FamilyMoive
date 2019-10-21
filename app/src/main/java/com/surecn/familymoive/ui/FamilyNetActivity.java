package com.surecn.familymoive.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.surecn.familymoive.DefaultConfig;
import com.surecn.familymoive.R;
import com.surecn.familymoive.common.SambaManager;
import com.surecn.familymoive.common.StreamService;
import com.surecn.familymoive.common.samba.IConfig;
import com.surecn.familymoive.utils.UriUtil;
import com.surecn.moat.core.Moat;
import com.surecn.moat.core.TaskPool;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, StreamService.class));

        mIconfig = new DefaultConfig();
        mSambaManager = new SambaManager(this, mIconfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Moat.with(this).next(new Task() {
            @Override
            public void run(Context context, TaskPool work, Object result) {
                getData().addAll(mSambaManager.listWorkGroup());
            }
        }).next(new UITask() {
            @Override
            public void run(Context context, TaskPool work, Object result) {
                FileAdapter fileAdapter = getAdater();
                fileAdapter.notifyDataSetChanged();
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        String uri = UriUtil.getUriParent(mSambaManager.getCurrentRemoteFolder());
        if (uri.equals("smb")) {
            listRoot(mSambaManager.getHost());
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
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("url", path);
        startActivity(intent);
    }

    private void listFile(final String path) {
        Moat.with(this).next(new Task() {
            @Override
            public void run(Context context, TaskPool work, Object result) {
                getData().clear();
                getData().addAll(mSambaManager.setCurrentFolder(path));
            }
        }).next(new UITask() {
            @Override
            public void run(Context context, TaskPool work, Object result) {
                setTitle(getString(R.string.family_net));
                FileAdapter fileAdapter = getAdater();
                fileAdapter.notifyDataSetChanged();
            }
        }).start();
    }

    private void listRoot(final String path) {
        mIconfig.updateHost(path);
        Moat.with(this).next(new Task() {
            @Override
            public void run(Context context, TaskPool work, Object result) {
                mSambaManager.listRoot();
                getData().clear();
                getData().addAll(mSambaManager.listFile());
            }
        }).next(new UITask() {
            @Override
            public void run(Context context, TaskPool work, Object result) {
                setTitle(UriUtil.getUriName(path));
                FileAdapter fileAdapter = getAdater();
                fileAdapter.notifyDataSetChanged();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, StreamService.class));
    }


}
