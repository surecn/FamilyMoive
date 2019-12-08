package com.surecn.familymovie.data;

import android.content.Context;
import android.os.Parcel;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-09
 * Time: 15:58
 */
public class FileCache {

    public final static String KEY_CHANNELDATA = "channel_parcelable";

    public final static String KEY_LANSERVER = "lanserver_parcelable";

    private static FileCache mInstance;

    public static FileCache getInstance() {
        if (mInstance == null) {
            mInstance = new FileCache();
        }
        return mInstance;
    }

    public void save(Context context, String key, List obj) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            File file = new File(context.getCacheDir(), key);
            if (file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file, false);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            Parcel parcel = Parcel.obtain();
            parcel.writeList(obj);
            bos.write(parcel.marshall());
            bos.flush();
            bos.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List read(Context context, String key) {
        FileInputStream fileInputStream = null;
        try {
            File file = new File(context.getCacheDir(), key);
            fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(bytes, 0, bytes.length);
            parcel.setDataPosition(0);
            return parcel.readArrayList(Thread.currentThread().getContextClassLoader());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
