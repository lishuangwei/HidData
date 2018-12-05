//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xgvr.glasses;

import android.util.Log;

public class glog {
    public static final boolean debug = false;

    public glog() {
    }

    public static void i(String tag, String msg) {
        if (debug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (debug)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (debug)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (debug)
            Log.v(tag, msg);
    }
}
