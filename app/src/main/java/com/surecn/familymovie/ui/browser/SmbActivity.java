package com.surecn.familymovie.ui.browser;

import android.os.Bundle;
import com.surecn.familymovie.common.SmbManager;
import com.surecn.familymovie.domain.FileItem;
import com.surecn.familymovie.ui.player.VideoActivity;
import com.surecn.familymovie.utils.UriUtil;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.Task;
import com.surecn.moat.core.task.UITask;
import java.net.MalformedURLException;
import java.util.List;
import androidx.annotation.Nullable;
import jcifs.smb.SmbFile;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-12
 * Time: 18:12
 */
public class SmbActivity extends FileActivity {

    private String mPath;

    private String mRoot;

    private int mRepeatCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPath = getIntent().getStringExtra("path");
        mRoot = mPath;

        listFile(mPath);
    }

    @Override
    public void onBackPressed() {
        if (mPath.equals(mRoot)) {
            super.onBackPressed();
            return;
        }
        try {
            mPath = new SmbFile(mPath).getParent();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        listFile(mPath);
    }

    public void onClick(FileItem fileItem) {
        if (fileItem.type == 2) {
            VideoActivity.startActivity(this, fileItem.path, 0);
        } else {
            listFile(fileItem.path);
        }
    }

    private void listFile(final String url) {
        mRepeatCount = 3;
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule work, Object result) {
                try {
                    work.sendNext(SmbManager.listFile(url, 0));
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mRepeatCount > 0) {
                        mRepeatCount--;
                        work.repeat();
                    }
                }
            }
        }).next(new UITask<List<FileItem>>() {
            @Override
            public void run(TaskSchedule work, List<FileItem> result) {
                if (result != null) {
                    mPath = url;
                    setTitle(UriUtil.getUriName(mPath));
                    updateData(result);
                    updateList();
                }
            }
        }).start();
    }

}
