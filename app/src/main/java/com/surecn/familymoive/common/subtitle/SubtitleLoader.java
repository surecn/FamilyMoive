/*
 *                       Copyright (C) of Avery
 *
 *                              _ooOoo_
 *                             o8888888o
 *                             88" . "88
 *                             (| -_- |)
 *                             O\  =  /O
 *                          ____/`- -'\____
 *                        .'  \\|     |//  `.
 *                       /  \\|||  :  |||//  \
 *                      /  _||||| -:- |||||-  \
 *                      |   | \\\  -  /// |   |
 *                      | \_|  ''\- -/''  |   |
 *                      \  .-\__  `-`  ___/-. /
 *                    ___`. .' /- -.- -\  `. . __
 *                 ."" '<  `.___\_<|>_/___.'  >'"".
 *                | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *                \  \ `-.   \_ __\ /__ _/   .-` /  /
 *           ======`-.____`-.___\_____/___.-`____.-'======
 *                              `=- -='
 *           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *              Buddha bless, there will never be bug!!!
 */

package com.surecn.familymoive.common.subtitle;

import android.text.TextUtils;
import android.util.Log;

import com.surecn.familymoive.common.subtitle.exception.FatalParsingException;
import com.surecn.familymoive.common.subtitle.format.FormatASS;
import com.surecn.familymoive.common.subtitle.format.FormatSRT;
import com.surecn.familymoive.common.subtitle.format.FormatSTL;
import com.surecn.familymoive.common.subtitle.model.TimedTextObject;
import com.surecn.familymoive.common.subtitle.runtime.AppTaskExecutor;
import com.surecn.moat.tools.log;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import net.lingala.zip4j.model.ZipParameters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;

import jcifs.smb.SmbFile;

/**
 * @author AveryZhong.
 */

public class SubtitleLoader {
    private static final String TAG = SubtitleLoader.class.getSimpleName();

    private SubtitleLoader() {
        throw new AssertionError("No instance for you.");
    }

    public static void loadSubtitle(final String path, final Callback callback) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (path.startsWith("http://")
                || path.startsWith("https://")
                || path.startsWith("smb://")) {
            loadFromRemoteAsync(path, callback);
        } else {
            loadFromLocalAsync(path, callback);
        }
    }

    private static void loadFromRemoteAsync(final String remoteSubtitlePath,
                                           final Callback callback) {
        AppTaskExecutor.deskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final TimedTextObject timedTextObject = loadFromRemote(remoteSubtitlePath);
                    if (callback != null) {
                        AppTaskExecutor.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(timedTextObject);
                            }
                        });
                    }

                } catch (final Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        AppTaskExecutor.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }

                }
            }
        });
    }

    private static void loadFromLocalAsync(final String localSubtitlePath,
                                          final Callback callback) {
        AppTaskExecutor.deskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final TimedTextObject timedTextObject = loadFromLocal(localSubtitlePath);
                    if (callback != null) {
                        AppTaskExecutor.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(timedTextObject);
                            }
                        });
                    }

                } catch (final Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        AppTaskExecutor.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }

                }
            }
        });
    }

    public TimedTextObject loadSubtitle(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        try {
            if (path.startsWith("http://")
                    || path.startsWith("https://")) {
               return loadFromRemote(path);
            } else {
                return loadFromLocal(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static TimedTextObject loadFromRemote(final String remoteSubtitlePath)
            throws IOException, FatalParsingException {
        Log.d(TAG, "parseRemote: remoteSubtitlePath = " + remoteSubtitlePath);
        if (remoteSubtitlePath.startsWith("smb://")) {
            SmbFile smbFile = new SmbFile(remoteSubtitlePath);
            return loadAndParse(smbFile.getInputStream(), remoteSubtitlePath);
        } else {
            URL url = new URL(remoteSubtitlePath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            for (String key : urlConnection.getHeaderFields().keySet()) {
                log.e(key + "===" + urlConnection.getHeaderFields().get(key));
            }
            log.e("====loadFromRemote======" + url.getPath());
            return loadAndParse(url.openStream(), url.getPath());
        }

    }

    private static TimedTextObject loadFromLocal(final String localSubtitlePath)
            throws IOException, FatalParsingException {
        Log.d(TAG, "parseLocal: localSubtitlePath = " + localSubtitlePath);
        File file = new File(localSubtitlePath);
        return loadAndParse(new FileInputStream(file), file.getPath());
    }

    private static TimedTextObject loadAndParse(final InputStream is, final String filePath)
            throws IOException, FatalParsingException {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String ext = fileName.substring(fileName.lastIndexOf("."));
        Log.d(TAG, "parse: name = " + fileName + ", ext = " + ext);
        if (".srt".equalsIgnoreCase(ext)) {
            return new FormatSRT().parseFile(fileName, is);
        } else if (".ass".equalsIgnoreCase(ext)) {
            return new FormatASS().parseFile(fileName, is);
        } else if (".stl".equalsIgnoreCase(ext)) {
            return new FormatSTL().parseFile(fileName, is);
        } else if (".ttml".equalsIgnoreCase(ext)) {
            return new FormatSTL().parseFile(fileName, is);
        } else if (".zip".equalsIgnoreCase(ext)) {
            ZipInputStream zipInputStream = new ZipInputStream(is);
            LocalFileHeader next = null;
            while ((next = zipInputStream.getNextEntry()) != null) {
                if (next.isDirectory()) {
                    continue;
                }
                TimedTextObject obj = loadAndParse(zipInputStream, next.getFileName());
                if(obj != null) {
                    return obj;
                }
            }
            zipInputStream.close();
        }
        return null;
    }

    public interface Callback {
        void onSuccess(TimedTextObject timedTextObject);

        void onError(Exception exception);
    }
}
