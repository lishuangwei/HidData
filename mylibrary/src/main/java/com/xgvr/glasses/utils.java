//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xgvr.glasses;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class utils {
    private static String oldMsg;
    private static Toast toast = null;
    private static long oneTime = 0L;
    private static long twoTime = 0L;

    public utils() {
    }

    public static short getShort(byte data) {
        return (short)(255 & data);
    }

    public static int getShort(byte[] arr, int index) {
        return arr[index + 1] << 8 & '\uff00' | arr[index] & 255;
    }

    public static byte[] getByteArray(float f) {
        int intbits = Float.floatToIntBits(f);
        return getByteArray((float)intbits);
    }

    public static float getFloat(byte[] arr, int index) {
        int data = arr[index + 3] & 255;
        data = data << 8 | arr[index + 2] & 255;
        data = data << 8 | arr[index + 1] & 255;
        data = data << 8 | arr[index] & 255;
        return Float.intBitsToFloat(data);
    }

    public static void showToast(final Context context, final String message) {
        ((Activity)context).runOnUiThread(new Runnable() {
            public void run() {
                if(utils.toast == null) {
                    utils.toast = Toast.makeText(context, message, 0);
                    utils.toast.show();
                    utils.oneTime = System.currentTimeMillis();
                } else {
                    utils.twoTime = System.currentTimeMillis();
                    if(message.equals(utils.oldMsg)) {
                        if(utils.twoTime - utils.oneTime > 0L) {
                            utils.toast.show();
                        }
                    } else {
                        utils.oldMsg = message;
                        utils.toast.setText(message);
                        utils.toast.show();
                    }
                }

                utils.oneTime = utils.twoTime;
            }
        });
    }

    public static boolean getStoragePermission(Activity activity) {
        boolean hasPermission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
        if(!hasPermission && !shouldShowRationale) {
            ActivityCompat.requestPermissions(activity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 0);
        }

        return hasPermission;
    }
}
