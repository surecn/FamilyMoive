package com.surecn.familymovie.utils;

import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;

import com.surecn.familymovie.domain.StorageVolume;

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
                        String description = null;
                        String path = (String) volumeList[i].getClass().getMethod("getPath").invoke(volumeList[i]);
                        try {
                            description = context.getResources().getString((int)volumeList[i].getClass().getMethod("getDescriptionId").invoke(volumeList[i]));
                        } catch (Exception e) {
                            description = (String) volumeList[i].getClass().getMethod("getDescription", Context.class).invoke(volumeList[i], context);
                        }
                        boolean primary = (boolean) volumeList[i].getClass().getMethod("isPrimary").invoke(volumeList[i]);
                        boolean removable = (boolean) volumeList[i].getClass().getMethod("isRemovable").invoke(volumeList[i]);
                        boolean emulated = (boolean) volumeList[i].getClass().getMethod("isEmulated").invoke(volumeList[i]);
                        int mtpReserveSpace = 0;
                        boolean allowMassStorage = (boolean) volumeList[i].getClass().getMethod("allowMassStorage").invoke(volumeList[i]);
                        long maxFileSize = (long) volumeList[i].getClass().getMethod("getMaxFileSize").invoke(volumeList[i]);
                        volume = new StorageVolume(path, description, primary, removable, emulated, mtpReserveSpace, allowMassStorage, maxFileSize);
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