package com.surecn.familymoive.utils;

import android.content.Context;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.util.Log;

import com.surecn.familymoive.domain.StorageVolume;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-26
 * Time: 16:45
 */
public class StorageUtils {


    /*
    获取全部存储设备信息封装对象
     */
    public static ArrayList<StorageVolume> getVolume(Context context) {
        ArrayList<StorageVolume> list_storagevolume = new ArrayList<StorageVolume>();

        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        try {
            Method method_volumeList = StorageManager.class.getMethod("getVolumeList");

            method_volumeList.setAccessible(true);

            Object[] volumeList = (Object[]) method_volumeList.invoke(storageManager);
            if (volumeList != null) {
                StorageVolume volume;
                for (int i = 0; i < volumeList.length; i++) {
                    try {
                        String path = (String) volumeList[i].getClass().getMethod("getPath").invoke(volumeList[i]);
                        int descriptionId = (int) volumeList[i].getClass().getMethod("getDescriptionId").invoke(volumeList[i]);
                        boolean primary = (boolean) volumeList[i].getClass().getMethod("isPrimary").invoke(volumeList[i]);
                        boolean removable = (boolean) volumeList[i].getClass().getMethod("isRemovable").invoke(volumeList[i]);
                        boolean emulated = (boolean) volumeList[i].getClass().getMethod("isEmulated").invoke(volumeList[i]);
                        int mtpReserveSpace = (int) volumeList[i].getClass().getMethod("getMtpReserveSpace").invoke(volumeList[i]);
                        boolean allowMassStorage = (boolean) volumeList[i].getClass().getMethod("allowMassStorage").invoke(volumeList[i]);
                        long maxFileSize = (long) volumeList[i].getClass().getMethod("getMaxFileSize").invoke(volumeList[i]);
                        volume = new StorageVolume(path, descriptionId, primary, removable, emulated, mtpReserveSpace, allowMassStorage, maxFileSize);
                        volume.setState((String) volumeList[i].getClass().getMethod("getState").invoke(volumeList[i]));
                        list_storagevolume.add(volume);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.e("null", "null-------------------------------------");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return list_storagevolume;
    }


}