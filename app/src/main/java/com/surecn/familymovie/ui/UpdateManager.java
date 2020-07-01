
/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-05
 * Time: 19:00
 */
package com.surecn.familymovie.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.surecn.familymovie.R;
import com.surecn.familymovie.domain.UpdateInfo;
import com.surecn.moat.core.LinearSchedule;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.Task;
import com.surecn.moat.tools.log;

import androidx.core.content.FileProvider;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UpdateManager {

    private Context mContext;

    private Dialog noticeDialog;

    private Dialog downloadDialog;

    private UpdateInfo updateInfo;

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;

    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private boolean interceptFlag = false;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:

                    installApk();
                    break;
                default:
                    break;
            }
        };
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    //外部接口让主Activity调用
    public void checkUpdateInfo(UpdateInfo updateInfo){
        this.updateInfo = updateInfo;
        showNoticeDialog();
    }

    private File getSavePath() {
        File file = mContext.getExternalCacheDir();
        if (file == null) {
            file = mContext.getCacheDir();
        }
        return new File(file, "update.apk");
    }


    private void showNoticeDialog(){
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("软件版本更新");
        builder.setMessage(updateInfo.msg);
        builder.setPositiveButton("下载", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        builder.setNegativeButton("以后再说", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.show();
    }

    private void showDownloadDialog(){
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("软件版本更新");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.layout_progress, null);
        mProgress = (ProgressBar)v.findViewById(R.id.progress);
        mProgress.setMax(100);

        builder.setView(v);
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();

        downloadApk();
    }

    private void downloadApk(){
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule taskSchedule, Object result) {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(updateInfo.url)
                            .get()//默认就是GET请求，可以不写
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    ResponseBody responseBody = response.body();
                    long length = responseBody.contentLength();
                    InputStream is = responseBody.byteStream();

                    File file = getSavePath();
                    FileOutputStream fos = new FileOutputStream(file);

                    long count = 0;
                    byte buf[] = new byte[50 * 1024];
                    int readSize = 0;
                    while (((readSize = is.read(buf)) != -1) && !interceptFlag) {
                        count += readSize;
                        progress = (int) (count * 100 / length);
                        //更新进度
                        mHandler.sendEmptyMessage(DOWN_UPDATE);
                        fos.write(buf,0, readSize);
                        if (count >= length) {
                            break;
                        }
                    }
                    if (count >= length) {
                        mHandler.sendEmptyMessage(DOWN_OVER);
                    }
                    fos.close();
                    is.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void installApk(){
        File apkFile = getSavePath();
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", apkFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);

    }
}