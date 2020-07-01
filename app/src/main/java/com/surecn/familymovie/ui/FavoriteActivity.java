package com.surecn.familymovie.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.surecn.familymovie.R;
import com.surecn.familymovie.common.FileManager;
import com.surecn.familymovie.common.SmbManager;
import com.surecn.familymovie.data.FavoriteModel;
import com.surecn.familymovie.data.ServerModel;
import com.surecn.familymovie.domain.Favorite;
import com.surecn.familymovie.domain.FileItem;
import com.surecn.familymovie.ui.browser.FileActivity;
import com.surecn.familymovie.ui.browser.LocalFileActivity;
import com.surecn.familymovie.ui.browser.SmbActivity;
import com.surecn.familymovie.ui.player.VideoActivity;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.Task;
import com.surecn.moat.core.task.UITask;
import java.util.ArrayList;
import java.util.List;
import jcifs.smb.NtlmPasswordAuthentication;

public class FavoriteActivity extends FileActivity {

    private FavoriteModel mFavoriteModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setFileShowStyle(1);
        super.onCreate(savedInstanceState);

        setTitle(R.string.favorite_list);
        setTipsText(R.string.favorite_tip_menu);

        mFavoriteModel = new FavoriteModel(this);

        load();
    }

    private void load() {
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule taskSchedule, Object result) {
                ServerModel serverModel = new ServerModel(FavoriteActivity.this);
                List<Favorite> list = mFavoriteModel.getFavoriteFolders();

                NtlmPasswordAuthentication auth = null;
                List<FileItem> folders = new ArrayList<>();
                for (Favorite favorite : list) {
                    if (favorite.getType() == FavoriteModel.TYPE_FOLDER_LAN && favorite.getValue().startsWith("smb://")) {
                        FileItem serverItem = serverModel.getByPath(favorite.getExtra());
                        if (serverItem != null && !TextUtils.isEmpty(serverItem.user)) {
                            auth = new NtlmPasswordAuthentication(serverItem.server, serverItem.user, serverItem.pass);
                        }
                        FileItem fileItem = SmbManager.getFileItem(favorite.getValue(), auth);
                        fileItem.favoriteType = favorite.getType();
                        folders.add(fileItem);
                    } else if (favorite.getType() == FavoriteModel.TYPE_FOLDER_LOCAL) {
                        FileItem fileItem = FileManager.getFileItem(favorite.getValue());
                        fileItem.favoriteType = favorite.getType();
                        folders.add(fileItem);
                    }
                }
                setData(folders);
            }
        }).next(new UITask() {
            @Override
            public void run(TaskSchedule taskSchedule, Object result) {
                updateList();
                hideTitlebarLoading();
            }
        }).start();
        showTitlebarLoading();
    }

    @Override
    protected int emptyTextResourceId() {
        return R.string.msg_favorite_empty;
    }

    public void onClick(FileItem fileItem) {
        if (fileItem.type == 2) {
            VideoActivity.startActivity(this, fileItem.path, 0, fileItem.path);
        } else if (fileItem.favoriteType == FavoriteModel.TYPE_FOLDER_LAN) {
            Intent intent = new Intent(this, SmbActivity.class);
            intent.putExtra("item", fileItem);
            startActivity(intent);
        } else if (fileItem.favoriteType == FavoriteModel.TYPE_FOLDER_LOCAL) {
            Intent intent = new Intent(this, LocalFileActivity.class);
            intent.putExtra("title", fileItem.getName());
            intent.putExtra("file", fileItem.path);
            startActivity(intent);
        }
    }

    @Override
    protected void onFavorite(boolean isFavorite, FileItem selectedFileItem) {
        super.onFavorite(isFavorite, selectedFileItem);
        if (isFavorite) {
            if (selectedFileItem.favoriteType == FavoriteModel.TYPE_FOLDER_LOCAL) {
                mFavoriteModel.deleteLocalFolder(selectedFileItem.path);
            } else if (selectedFileItem.favoriteType == FavoriteModel.TYPE_FOLDER_LAN) {
                mFavoriteModel.deleteLanFolder(selectedFileItem.path);
            }
            showToast("取消收藏成功");
            load();
        }
    }
}
