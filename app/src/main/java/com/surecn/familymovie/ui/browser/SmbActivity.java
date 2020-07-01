package com.surecn.familymovie.ui.browser;

import android.os.Bundle;
import android.text.TextUtils;
import com.surecn.familymovie.common.SmbManager;
import com.surecn.familymovie.data.FavoriteModel;
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
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-12
 * Time: 18:12
 */
public class SmbActivity extends FileActivity {

    private String mPath;

    private FileItem mFileItem;

    private int mRepeatCount;

    private NtlmPasswordAuthentication mAuth;

    private FavoriteModel mFavoriteModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFavoriteModel = new FavoriteModel(this);
        mFileItem = (FileItem) getIntent().getParcelableExtra("item");
        mPath = mFileItem.path;
        if (!TextUtils.isEmpty(mFileItem.user)) {
            mAuth = new NtlmPasswordAuthentication(mFileItem.server, mFileItem.user, mFileItem.pass);
        }

        listFile(mPath);
    }

    @Override
    public void onBackPressed() {
        if (mPath.equals(mFileItem.path)) {
            super.onBackPressed();
            return;
        }
        try {
            mPath = new SmbFile(mPath, mAuth).getParent();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        listFile(mPath);
    }

    @Override
    protected void onFavorite(boolean isFavorite, FileItem selectedFileItem) {
        if (!isFavorite) {
            mFavoriteModel.addLanFolder(selectedFileItem.path, selectedFileItem.server);
            showToast("收藏成功");
        } else {
            mFavoriteModel.deleteLanFolder(selectedFileItem.path);
            showToast("取消收藏成功");
        }
    }

    public void onClick(FileItem fileItem) {
        if (fileItem.type == 2) {
            VideoActivity.startActivity(this, fileItem.path, 0, mFileItem.path);
        } else {
            listFile(fileItem.path);
        }
    }

    private void listFile(final String url) {
        showTitlebarLoading();
        mRepeatCount = 3;
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule work, Object result) {
                try {
                    work.sendNext(SmbManager.listFile(url, 0, mAuth));
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
                    scrollTop();
                    hideTitlebarLoading();
                }
            }
        }).start();
    }

}
