//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xgvr.glasses;

import android.content.Context;

public abstract class BaseDeviceManager {
    public BaseDeviceManager() {
    }

    public abstract int init(Context var1);

    public abstract int startScan();

    public abstract int setCurDevice(int var1, int var2);

    public abstract int open();

    public abstract int read(byte[] var1, int var2);

    public abstract int write(byte[] var1, int var2);

    public abstract int stop();

    public abstract int getPid();

    public abstract boolean isConnect();

    public abstract int getDataType();
}
