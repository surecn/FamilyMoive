package com.surecn.familymovie.ui.browser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.surecn.familymovie.R;
import com.surecn.familymovie.common.LanManager;
import com.surecn.familymovie.common.SmbManager;
import com.surecn.familymovie.data.AppProvider;
import com.surecn.familymovie.data.FileCache;
import com.surecn.familymovie.data.ServerModel;
import com.surecn.familymovie.domain.FileItem;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.core.task.Task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-13
 * Time: 12:41
 */
public class LanActivity extends FileActivity {

    private HashSet<String> mGetName = new HashSet<>();

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private ServerModel mServerModel;

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

    private ContentObserver contentObserver = new ContentObserver(handler) {
        @Override
        public void onChange(boolean selfChange) {
            loadList();
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFileShowStyle(1);
        super.onCreate(savedInstanceState);

        mServerModel = new ServerModel(this);
        getContentResolver().registerContentObserver(AppProvider.getContentUri("SERVER"), true, contentObserver);
        loadList();

        listServer();

        setTips(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(contentObserver);
    }

    private void loadList() {
        List<FileItem> list = mServerModel.all();
        if (list == null) {
            list = new ArrayList<>();
        }
        FileItem fileItem = new FileItem();
        fileItem.name = "手动添加";
        fileItem.type = -1;
        list.add(fileItem);
        setData(list);
        updateList();
    }

    @Override
    public void onClick(final FileItem fileItem) {
        switch (fileItem.type) {
            case 0:
                Schedule.linear(new Task() {
                    @Override
                    public void run(TaskSchedule taskSchedule, Object result) {
                        if (!fileItem.canAccess || !tryEnterServer(fileItem)) {
                            showInputAuth(fileItem);
                            taskSchedule.sendNext(false);
                            return;
                        }
                        taskSchedule.sendNext(true);
                    }
                }).next(new UITask<Boolean>() {
                    @Override
                    public void run(TaskSchedule taskSchedule, Boolean result) {
                        if (result) {
                            Intent intent = new Intent(LanActivity.this, SmbActivity.class);
                            intent.putExtra("item", fileItem);
                            startActivity(intent);
                        }
                    }
                }).start();
                break;
            case -1:
                showAddServer();
                break;
        }
    }


    private boolean tryEnterServer(FileItem fileItem) {
        try {
            SmbFile smbFile = null;
            if (TextUtils.isEmpty(fileItem.user)) {
                smbFile = new SmbFile(fileItem.path);
            } else {
                smbFile = new SmbFile(fileItem.path, new NtlmPasswordAuthentication(fileItem.server, fileItem.user, fileItem.pass));
            }
            smbFile.connect();
            fileItem.canAccess = true;
            mServerModel.setAuth(fileItem, fileItem.user, fileItem.pass);
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showInputAuth(final FileItem fileItem) {
        Schedule.linear(new UITask() {
            @Override
            public void run(TaskSchedule taskSchedule, Object result) {
                final AlertDialog alertDialog = new AlertDialog.Builder(LanActivity.this)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlertDialog dia = (AlertDialog) dialog;
                                EditText user = dia.findViewById(R.id.edit_user);
                                EditText pass = dia.findViewById(R.id.edit_pass);
                                if (user.length() <= 0) {
                                    Toast.makeText(LanActivity.this, R.string.msg_empty_user, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                fileItem.user = user.getText().toString();
                                fileItem.pass = pass.getText().toString();
                                Schedule.linear(new Task() {
                                    @Override
                                    public void run(TaskSchedule taskSchedule, Object result) {
                                        tryEnterServer(fileItem);
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setTitle(R.string.auth_title)
                        .setView(R.layout.activity_smbauth).create();
                alertDialog.show();
            }
        }).start();
    }

    @Override
    protected void showMenu(View v) {
        final SettingDialog settingDialog = new SettingDialog(this);
        settingDialog.setData(new String[]{"重新扫描"});
        settingDialog.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if (textView.getText().equals("重新扫描")) {
                    //mServerModel.clear();
                    listServer();
                }
                settingDialog.dismiss();
            }
        });
        settingDialog.show();
    }

    private void showAddServer() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.lan_title_add_server)
                .setView(R.layout.layout_add_server)
                .setNegativeButton(R.string.lan_button_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog dialog1 = (AlertDialog) dialog;
                        EditText editText = dialog1.findViewById(R.id.input);
                        FileItem fileItem = new FileItem();
                        fileItem.type = 0;
                        fileItem.name = editText.getText().toString();
                        fileItem.path = "smb://" + fileItem.name;
                        mServerModel.add(fileItem);
                    }
                })
                .create();
        alertDialog.show();
    }

    private void listServer() {
        setTitle(getString(R.string.main_lan));
        showTitlebarLoading();
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule work, Object result) {
                ArrayList<String> items = SmbManager.listWorkGroupIp();
                for (String ip : items) {
                    getServerName(ip);
                }
                getShareServer();

            }
        }).next(new UITask() {
            @Override
            public void run(TaskSchedule work, Object result) {
                updateList();
                List<FileItem> fileItems = getData();
                hideTitlebarLoading();
            }
        }).start();
    }

    public void getShareServer() {
        long [] range = LanManager.discover(LanActivity.this);
        CountDownLatch latch = new CountDownLatch((int) (range[1] - range[0] + 1));
        for (long i = range[0]; i <= range[1]; i++) {
            final String ip = LanManager.LongToIp(i);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(LanManager.pingHost(ip, 445)) {
                            getServerName(ip);
                        }
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getServerName(String ip) {
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule taskSchedule, Object result) {
                FileItem fileItem = SmbManager.getServer(ip);
                if (fileItem != null) {
                    mServerModel.add(fileItem);
                }
            }
        }).start();

    }

}
