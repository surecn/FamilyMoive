package com.surecn.familymovie.ui.browser;

import android.content.Intent;
import android.os.Bundle;
import com.surecn.familymovie.R;
import com.surecn.familymovie.common.SmbManager;
import com.surecn.familymovie.domain.FileItem;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.core.task.Task;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-13
 * Time: 12:41
 */
public class LanActivity extends FileActivity {

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
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule work, Object result) {
                updateData(SmbManager.listWorkGroup());
            }
        }).next(new UITask() {
            @Override
            public void run(TaskSchedule work, Object result) {
                updateList();
            }
        }).start();
    }

}
