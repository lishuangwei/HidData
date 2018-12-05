//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xgvr.glasses;

import android.content.Context;

public class XgvrActivity {
    public XgvrActivity() {
    }

    public int xgvr_hdm_init(Context context) {
        return XgvrMoudle.getInstance().xgvr_hdm_init(context);
    }

    public int xgvr_hmd_deinit() {
        XgvrMoudle.getInstance().xgvr_hmd_deinit();
        return 0;
    }

    public int xgvr_hmd_is_present() {
        return XgvrMoudle.getInstance().xgvr_hmd_is_present();
    }

    public int xgvr_hmd_info_get() {
        return 0;
    }

    public float[] xgvr_hmd_data_get() {
        return XgvrMoudle.getInstance().xgvr_hmd_data_get();
    }

    public void xgvr_hmd_reCenter() {
        XgvrMoudle.getInstance().xgvr_hmd_ReCenter();
    }

    public boolean[] getButtons() {
        return XgvrBlueMoudle.getInstance().getButtons();
    }

    public float[] getRotation() {
        return XgvrBlueMoudle.getInstance().getRotation();
    }

    public int getBindState() {
        return XgvrBlueMoudle.getInstance().getBindState();
    }

    public void startGetDate(Context context) {
        XgvrMoudle.getInstance().startGetDate();
    }

    public float[] getDates() {
        return XgvrMoudle.getInstance().getDates();
    }
}
