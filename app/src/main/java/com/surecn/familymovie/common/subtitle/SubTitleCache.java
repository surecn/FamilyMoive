package com.surecn.familymovie.common.subtitle;

import android.content.Context;
import android.text.TextUtils;

import com.surecn.familymovie.utils.MD5Util;
import com.surecn.familymovie.utils.UriUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 14:26
 */
public class SubTitleCache {

    private Context mContext;

    private File mFolder;

    public SubTitleCache(Context context) {
        mContext = context;
        mFolder = new File(context.getCacheDir(), "subtitle");
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }

    }

    public File getCache(String url, String extension) {
        File file = getCacheFile(url, extension);
        if (file != null && file.exists()) {
            return file;
        }
        return null;
    }

    private File getCacheFile(String url, String extension) {
        if (TextUtils.isEmpty(extension)) {
            String name = MD5Util.MD5(url) + ".";
            File [] files = mFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return s.equalsIgnoreCase(name + "srt") || s.equalsIgnoreCase(name + "ass") || s.equalsIgnoreCase(name + "ssa");
                }
            });
            if (files != null && files.length > 0) {
                return files[0];
            } else {
                return null;
            }
        } else {
            File file = new File(mFolder, MD5Util.MD5(url) + "." + extension);
            return file;
        }
    }

    public SubTitleWriter getWriter(File file) {
        return new SubTitleWriter(file);
    }

    public SubTitleWriter getWriter(String key, String extension) {
        return new SubTitleWriter(getCacheFile(key, extension));
    }

    public static class SubTitleWriter {

        private BufferedWriter bufferedWriter;

        private SubTitleWriter(File file) {
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                bufferedWriter = new BufferedWriter(new FileWriter(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void writeLine(String line) {
            try {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
