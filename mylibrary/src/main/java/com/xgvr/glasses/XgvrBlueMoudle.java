//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xgvr.glasses;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Build.VERSION;
//import com.unity3d.player.UnityPlayer;
//import cwm.android_plugin_lib.CwmController;
//import cwm.android_plugin_lib.CwmManager;
//import cwm.android_plugin_lib.DeviceHandler;
//import cwm.android_plugin_lib.CwmManager.RemoteServiceListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XgvrBlueMoudle {
    private final String TAG = "XgvrBlueMoudle";
    private static XgvrBlueMoudle mInstance;
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private final long SCAN_PERIOD = 20000L;
    private Handler mHandler = new Handler();
    private boolean mScanning;
    private List<BluetoothDevice> mScanDayDreamDeviceList = new ArrayList();
    private boolean isScanBluetoothController = true;
    private int mBindState;
    private boolean debug = true;
    private float[] Pos = new float[2];
    private LeScanCallback mLeScanCallback = new LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            XgvrBlueMoudle.this.mHandler.post(new Runnable() {
                public void run() {
                    XgvrBlueMoudle.this.TryConnect(device, rssi);
                }
            });
        }
    };
    private BroadcastReceiver bluetoothStatusReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            Object[] lstName = b.keySet().toArray();

            for (int i = 0; i < lstName.length; ++i) {
                String keyName = lstName[i].toString();
                glog.i("XgvrBlueMoudle", keyName + ">>>" + b.get(keyName));
            }

            String var8 = intent.getAction();
            byte var9 = -1;
            switch (var8.hashCode()) {
                case -1530327060:
                    if (var8.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                        var9 = 0;
                    }
                default:
                    switch (var9) {
                        case 0:
                            int blueState = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 0);
                            switch (blueState) {
                                case 10:
                                    if (!XgvrBlueMoudle.this.debug) {
                                    }
                                case 11:
                                case 13:
                                default:
                                    break;
                                case 12:
                                    if (!XgvrBlueMoudle.this.debug) {
                                    }
                            }
                        default:
                    }
            }
        }
    };

    private XgvrBlueMoudle() {
    }

    public static XgvrBlueMoudle getInstance() {
        if (mInstance == null) {
            mInstance = new XgvrBlueMoudle();
        }

        return mInstance;
    }

    public int init(Context context) {
        this.mContext = context;
        if (!context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            glog.e("XgvrBlueMoudle", "no support lebluetooth---------------------------");
            return -50;
        } else {
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            this.mBluetoothAdapter = bluetoothManager.getAdapter();
            if (this.mBluetoothAdapter != null && this.mBluetoothAdapter.isEnabled()) {
                IntentFilter bluetoothfilter = new IntentFilter();
                bluetoothfilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
                bluetoothfilter.addAction("android.bluetooth.device.action.FOUND");
                context.registerReceiver(this.bluetoothStatusReceiver, bluetoothfilter);
                glog.i("life", "--------------------------------------------init");
                return 0;
            } else {
                glog.e("XgvrBlueMoudle", "bluetooth is closed---------------------------");
                return -51;
            }
        }
    }

    public void deinit() {
        this.mContext.unregisterReceiver(this.bluetoothStatusReceiver);
    }

    private void scanLeDevice(boolean enable) {
        if (this.isScanBluetoothController) {
            if (VERSION.SDK_INT >= 23 && this.mContext.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION")
                    != PackageManager.PERMISSION_GRANTED) {
                ((Activity) this.mContext).requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, 1);
            }

            if (enable) {
                this.mHandler.postDelayed(new Runnable() {
                    public void run() {
                        XgvrBlueMoudle.this.mScanning = false;
                        XgvrBlueMoudle.this.mBindState = 3;
                        XgvrBlueMoudle.this.mBluetoothAdapter.stopLeScan(XgvrBlueMoudle.this.mLeScanCallback);
                    }
                }, 20000L);
                this.mScanning = true;
                boolean scanstatus = this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
                if (!scanstatus) {
                    glog.i("XgvrBlueMoudle", "mBluetoothAdapter.startLeScan return false--------------------------");
                }

                glog.i("XgvrBlueMoudle", "scan --------------------------- true");
            } else {
                this.mScanning = false;
                this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
            }

        }
    }

    public void TryConnect(BluetoothDevice device, int rssi) {
        String address = device.getAddress();
        boolean isMyDevice = address.contains("ED:50:5A") || device.getName() != null && device.getName().equals("Daydream controller");
        if (address.contains("ED:50:5A") || device.getName() != null && device.getName().equals("Daydream controller")) {
            glog.i("XgvrBlueMoudle", "scaned bluetooth：" + device.getName() + "    mac=" + device.getAddress() + " mydevice = " + isMyDevice + ", rssi = " + rssi);
            if (this.mScanDayDreamDeviceList.indexOf(device) != -1) {
                glog.i("XgvrBlueMoudle", "the device already existed  listsize=" + this.mScanDayDreamDeviceList.size());
                return;
            }

            if (rssi < -45) {
                glog.i("XgvrBlueMoudle", " the rssi is too weak  ");
                return;
            }

            this.mScanDayDreamDeviceList.add(device);
            glog.i("XgvrBlueMoudle", "try connect the device");
        }

    }

    public float[] getRotation() {
        float[] rotation = new float[]{0.0F, 0.0F, 0.0F, 1.0F};
        return rotation;
    }

    public boolean[] getButtons() {
        boolean[] buttons = new boolean[]{false, false, false, false, false};
        glog.i("XgvrBlueMoudle", "b0=" + buttons[0] + "   b1=" + buttons[1] + "   b2=" + buttons[2] + "   b3=" + buttons[3] + "   b4=" + buttons[4]);
        return buttons;
    }

    private boolean findSystemConnectedDevice() {
        Class bluetoothAdapterClass = BluetoothAdapter.class;

        try {
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            method.setAccessible(true);
            int state = ((Integer) method.invoke(this.mBluetoothAdapter, (Object[]) null)).intValue();
            if (state == 2) {
                glog.i("XgvrBlueMoudle", "BluetoothAdapter.STATE_CONNECTED");
                Set<BluetoothDevice> devices = this.mBluetoothAdapter.getBondedDevices();
                glog.i("XgvrBlueMoudle", "devices:" + devices.size());
                Iterator var5 = devices.iterator();

                while (var5.hasNext()) {
                    BluetoothDevice device = (BluetoothDevice) var5.next();
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = ((Boolean) isConnectedMethod.invoke(device, (Object[]) null)).booleanValue();
                    if (isConnected) {
                        glog.i("XgvrBlueMoudle", "connected:" + device.getAddress());
                        return true;
                    }
                }
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return false;
    }

    public int getBindState() {
        return this.mBindState;
    }
}
