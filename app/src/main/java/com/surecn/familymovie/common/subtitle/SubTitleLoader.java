package com.surecn.familymovie.common.subtitle;

import com.surecn.familymovie.common.unicode.UnicodeReader;
import com.surecn.familymovie.utils.UriUtil;
import com.surecn.moat.tools.log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 10:48
 */
public class SubTitleLoader {

    private ExecutorService mExecutor = java.util.concurrent.Executors.newSingleThreadExecutor();

    public static SubTitleLoader sInstance;

    public static SubTitleLoader getInstance() {
        if (sInstance == null) {
            sInstance = new SubTitleLoader();
        }
        return sInstance;
    }

    public static interface OnDownloadListener {
        void onStart(String url);
        void onReadLine(String line);
        void onComplete();
    }

    private OnDownloadListener mOnDownloadListener;

    public void setOnReadLineListener(OnDownloadListener onDownloadListener) {
        this.mOnDownloadListener = onDownloadListener;
    }

    public void download(String url) {
        URLTask urlTask = new URLTask(url);
        urlTask.setSubTitleDownloader(this);
        mExecutor.execute(urlTask);
    }

    public void load(File file) {
        FileTask fileTask = new FileTask(file);
        fileTask.setSubTitleDownloader(this);
        mExecutor.execute(fileTask);
    }

    public void load(String url, InputStream inputStream) {
        InputStreamTask urlTask = new InputStreamTask(url, inputStream);
        urlTask.setSubTitleDownloader(this);
        mExecutor.execute(urlTask);
    }

    public static abstract class Task implements Runnable {

        private String mUrl;

        private SubTitleLoader mSubTitleLoader;

        public Task(String url) {
            this.mUrl = url;
        }

        public String getUrl() {
            return mUrl;
        }

        void setSubTitleDownloader(SubTitleLoader subTitleLoader) {
            mSubTitleLoader = subTitleLoader;
        }

        public abstract InputStream getInputStream();

        @Override
        public void run() {
            OnDownloadListener onDownloadListener = mSubTitleLoader.mOnDownloadListener;
            onDownloadListener.onStart(mUrl);
            InputStream inputStream = getInputStream();
            UnicodeReader unicodeReader = new UnicodeReader(inputStream, "gbk");
            BufferedReader br = new BufferedReader(unicodeReader);
            String line = null;
            try {
                while ((line = br.readLine()) != null && onDownloadListener != null) {
                    onDownloadListener.onReadLine(line);
                }
                onDownloadListener.onComplete();
            } catch (IOException e) {
                log.e(e);
            }
        }
    }

    public static class InputStreamTask extends Task {

        private InputStream mInputStream;

        public InputStreamTask(String url, InputStream inputStream) {
            super(url);
            this.mInputStream = inputStream;
        }

        @Override
        public InputStream getInputStream() {
            return mInputStream;
        }
    }

    public static class URLTask extends Task {

        private String mRealPath;

        public URLTask(String url) {
            super(url);
        }

        @Override
        public InputStream getInputStream() {
            try {
                URL url = new URL(getUrl());
                mRealPath = url.getPath();
                if ("zip".equals(UriUtil.getUriExtension(mRealPath))) {
                    ZipEntry next;
                    ZipInputStream zipInputStream = new ZipInputStream(url.openStream());
                    while ((next = zipInputStream.getNextEntry()) != null) {
                        if (next.isDirectory()) {
                            continue;
                        }
                        if ("ass".equals(next.getName()) || "srt".equals(next.getName())) {
                            return zipInputStream;
                        }
                    }
                }
                return url.openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class FileTask extends Task {

        private File mFile;

        public FileTask(File file) {
            super(file.getPath());
            this.mFile = file;
        }

        @Override
        public InputStream getInputStream() {
            try {
                return new FileInputStream(mFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
