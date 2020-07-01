package com.surecn.familymovie.ui.browser;

import android.os.Bundle;
import com.surecn.familymovie.common.FileManager;
import com.surecn.familymovie.data.FavoriteModel;
import com.surecn.familymovie.domain.FileItem;
import com.surecn.familymovie.ui.browser.FileActivity;
import com.surecn.familymovie.ui.player.VideoActivity;

import java.io.File;
import androidx.annotation.Nullable;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-26
 * Time: 13:44
 */
public class LocalFileActivity extends FileActivity {

    private String path = null;

    private String root;

    private FavoriteModel mFavoriteModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFavoriteModel = new FavoriteModel(this);
        setTitle(getIntent().getStringExtra("title"));
        updateData(FileManager.listFile(getIntent().getStringExtra("file"), 0));
        root = getIntent().getStringExtra("file");
        path = root;
    }

    @Override
    public void onBackPressed() {
        if (path.equals(root)) {
            super.onBackPressed();
            return;
        }
        path = new File(path).getParent();
        updateData(FileManager.listFile(path, 0));
        updateList();
    }

    @Override
    protected void onFavorite(boolean isFavorite, FileItem selectedFileItem) {
        if (!isFavorite) {
            mFavoriteModel.addLocalFolder(selectedFileItem.path, selectedFileItem.server);
            showToast("收藏成功");
        } else {
            mFavoriteModel.deleteLocalFolder(selectedFileItem.path);
            showToast("取消收藏成功");
        }
    }

    public void onClick(FileItem fileItem) {
        if (fileItem.type == 2) {
            VideoActivity.startActivity(this, fileItem.path, 0, null);
        } else {
            setTitle(fileItem.name);
            path = fileItem.path;
            updateData(FileManager.listFile(fileItem.path, 0));
            updateList();
            scrollTop();
        }
    }

}
