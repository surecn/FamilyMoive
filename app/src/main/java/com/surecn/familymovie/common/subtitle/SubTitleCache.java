package com.surecn.familymovie.common.subtitle;

import android.content.Context;

import com.surecn.familymovie.utils.MD5Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 14:26
 */
public class SubTitleCache {

    private Context mContext;

    private File mFolder;

    private static SubTitleCache mSubTitleCache;

    public SubTitleCache(Context context) {
        mContext = context;
        mFolder = new File(context.getCacheDir(), "subtitle");
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
    }

    public File getCache(String url) {
        File file = getCacheFile(url);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public File getCacheFile(String url) {
        File file = new File(mFolder, MD5Util.md5ToString(url.getBytes()));
        return file;
    }

    public SubTitleWriter getWriter(File file) {
        return new SubTitleWriter(file);
    }

    public SubTitleWriter getWriter(String key) {
        return new SubTitleWriter(getCacheFile(key));
    }

    public static class SubTitleWriter {

        private BufferedWriter bufferedWriter;

        private SubTitleWriter(File file) {
            try {
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
