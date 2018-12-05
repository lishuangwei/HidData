//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xgvr.glasses;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Build.VERSION;
import com.xgvr.glasses.usbhost.OnUsbHidDeviceListener;
import java.util.Arrays;

public class HidDevice {
    private final String TAG = this.getClass().getName();
    private static final int INTERFACE_CLASS_HID = 3;
    private static final String ACTION_USB_PERMISSION = "com.xgvr.glasses.usbhost.USB_PERMISSION";
    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private UsbInterface mUsbInterface;
    private UsbDeviceConnection mConnection;
    private OnUsbHidDeviceListener mListener;
    private UsbEndpoint mInUsbEndpoint;
    private UsbEndpoint mOutUsbEndpoint;
    private Handler mHandler;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_USB_PERMISSION.equals(action)) {
                context.unregisterReceiver(this);
                synchronized(this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra("device");
                    if(device != null && device.getProductId() != HidDevice.this.mUsbDevice.getProductId()) {
                        return;
                    }

                    if(intent.getBooleanExtra("permission", false)) {
                        HidDevice.this.openImpl();
                    } else {
                        HidDevice.this.connectFailed();
                    }
                }
            }

        }
    };

    public HidDevice(UsbManager usbManager, UsbDevice device) {
        this.mUsbManager = usbManager;
        this.mUsbDevice = device;
        this.init();
    }

    private int init() {
        int count = this.mUsbDevice.getInterfaceCount();

        int i;
        for(i = 0; i < count; ++i) {
            UsbInterface usbInterface = this.mUsbDevice.getInterface(i);
            if(usbInterface.getInterfaceClass() == 3) {
                this.mUsbInterface = usbInterface;
                break;
            }
        }

        if(this.mUsbInterface == null) {
            //glog.e(this.TAG, "mUsbInterface is null");
            return -1;
        } else {
            for(i = 0; i < this.mUsbInterface.getEndpointCount(); ++i) {
                UsbEndpoint endpoint = this.mUsbInterface.getEndpoint(i);
                int dir = endpoint.getDirection();
                int type = endpoint.getType();
                if(this.mInUsbEndpoint == null && dir == 128 && type == 3) {
                    this.mInUsbEndpoint = endpoint;
                }

                if(this.mOutUsbEndpoint == null && dir == 0 && type == 3) {
                    this.mOutUsbEndpoint = endpoint;
                }
            }

            return 0;
        }
    }

    public int getPid() {
        return this.mUsbDevice.getProductId();
    }

    public int getVid() {
        return this.mUsbDevice.getVendorId();
    }

    public UsbDevice getUsbDevice() {
        return this.mUsbDevice;
    }

    public int open(Context context, OnUsbHidDeviceListener listener) {
        this.mListener = listener;
        if(this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages(this);
            this.mHandler = null;
        }

        this.mHandler = new Handler(context.getMainLooper());
        byte ret;
        if(!this.mUsbManager.hasPermission(this.mUsbDevice)) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            context.registerReceiver(this.mUsbReceiver, filter);
            this.mUsbManager.requestPermission(this.mUsbDevice, permissionIntent);
            ret = 1;
        } else {
            this.openImpl();
            ret = 2;
        }

        return ret;
    }

    private int openImpl() {
        this.mConnection = this.mUsbManager.openDevice(this.mUsbDevice);
        if(this.mUsbInterface == null) {
            this.connectFailed();
            return -1;
        } else if(this.mConnection == null) {
            this.connectFailed();
            return -1;
        } else if(!this.mConnection.claimInterface(this.mUsbInterface, true)) {
            this.connectFailed();
            return -1;
        } else {
            if(VERSION.SDK_INT >= 21) {
                this.mConnection.setInterface(this.mUsbInterface);
            }

            this.mHandler.post(new Runnable() {
                public void run() {
                    if(HidDevice.this.mListener != null) {
                        HidDevice.this.mListener.onUsbHidDeviceConnected(HidDevice.this);
                    }

                }
            });
            return 0;
        }
    }

    private void connectFailed() {
        this.mHandler.post(new Runnable() {
            public void run() {
                if(HidDevice.this.mListener != null) {
                    HidDevice.this.mListener.onUsbHidDeviceConnectFailed(HidDevice.this);
                }

            }
        });
    }

    public int write(byte[] data, int size) {
        return this.write(data, 0, size);
    }

    public int write(byte[] data, int offset, int size) {
        int ret = 0;
        if(offset != 0) {
            data = Arrays.copyOfRange(data, offset, size);
        }

        if(this.mOutUsbEndpoint == null) {
            //DJILogUtil.i(this.TAG, "OutUsbEndPoint is null");
        } else {
            ret = this.mConnection.bulkTransfer(this.mOutUsbEndpoint, data, size, 1000);
        }

        return ret;
    }

    public boolean isConnection() {
        return this.mConnection != null;
    }

    public int read(byte[] buf, int length, int timeout) {
        //glog.d(this.TAG, "connection = " + this.mConnection);
        if(this.mConnection == null) {
            return -1;
        } else {
            int bytesRead = this.mConnection.bulkTransfer(this.mInUsbEndpoint, buf, length, timeout);
            return bytesRead;
        }
    }

    public int read(byte[] buf, int length) {
        return this.mUsbDevice == null?-1:this.read(buf, length, 1000);
    }

    public int close() {
        this.mHandler = null;
        this.mConnection.close();
        return 0;
    }
}
