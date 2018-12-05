//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xgvr.glasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class USBBroadcastReceiver extends BroadcastReceiver {
    public USBBroadcastReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.hardware.usb.action.USB_STATE")) {
            if(intent.getExtras().getBoolean("connected")) {
                Toast.makeText(context, "插入", 1).show();
            } else {
                Toast.makeText(context, "拔出", 1).show();
            }
        }

    }
}
