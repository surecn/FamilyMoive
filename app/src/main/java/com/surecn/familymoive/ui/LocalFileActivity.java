package com.surecn.familymoive.ui;

import android.os.Bundle;
import com.surecn.familymoive.common.FileManager;
import com.surecn.familymoive.domain.FileItem;
import java.io.File;
import androidx.annotation.Nullable;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-26
 * Time: 13:44
 */
public class LocalFileActivity extends FileActivity {

    String path = null;

    private String root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void onClick(FileItem fileItem) {
        setTitle(fileItem.name);
        path = fileItem.path;
        updateData(FileManager.listFile(fileItem.path, 0));
        updateList();
    }

}
