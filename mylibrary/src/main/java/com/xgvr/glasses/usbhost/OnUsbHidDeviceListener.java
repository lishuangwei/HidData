package com.xgvr.glasses.usbhost;

import com.xgvr.glasses.HidDevice;

public interface OnUsbHidDeviceListener {
    void onUsbHidDeviceConnected(HidDevice var1);

    void onUsbHidDeviceConnectFailed(HidDevice var1);
}