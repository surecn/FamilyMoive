package com.surecn.moat.tools.utils;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * Created by surecn on 15/8/13.
 */
public class DeviceUtils {

    public static String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public static String getPhoneModel() {
        return Build.MODEL;
    }

    public static int getPhoneVerison() {
        return Build.VERSION.SDK_INT;
    }

    private static boolean checkCameraFacing(final int facing) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            return false;
        }
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }
    public static boolean hasBackFacingCamera() {
        final int CAMERA_FACING_BACK = 0;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }
    public static boolean hasFrontFacingCamera() {
        final int CAMERA_FACING_BACK = 1;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }
}
