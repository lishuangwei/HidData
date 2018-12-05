//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xgvr.glasses.usbhost;

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
import android.support.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UsbHidDevice {
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
                    if(intent.getBooleanExtra("permission", false) && device != null) {
                        UsbHidDevice.this.openDevice();
                    } else {
                        UsbHidDevice.this.onConnectFailed();
                    }
                }
            }

        }
    };

    public static UsbHidDevice[] enumerate(Context context, int vid, int pid) throws Exception {
        UsbManager usbManager = (UsbManager)context.getApplicationContext().getSystemService("usb");
        if(usbManager == null) {
            throw new Exception("no usb service");
        } else {
            Map<String, UsbDevice> devices = usbManager.getDeviceList();
            List<UsbHidDevice> usbHidDevices = new ArrayList();
            Iterator var6 = devices.values().iterator();

            while(true) {
                UsbDevice device;
                do {
                    do {
                        if(!var6.hasNext()) {
                            return (UsbHidDevice[])usbHidDevices.toArray(new UsbHidDevice[usbHidDevices.size()]);
                        }

                        device = (UsbDevice)var6.next();
                    } while(vid != 0 && device.getVendorId() != vid);
                } while(pid != 0 && device.getProductId() != pid);

                UsbHidDevice hidDevice = new UsbHidDevice(device, usbManager);
                usbHidDevices.add(hidDevice);
            }
        }
    }

    private UsbInterface getInterface(UsbDevice device) {
        UsbInterface usbInterface = null;
        int count = device.getInterfaceCount();

        for(int i = 0; i < count; ++i) {
            usbInterface = device.getInterface(i);
            if(usbInterface.getInterfaceClass() == 3) {
                return usbInterface;
            }
        }

        return usbInterface;
    }

    public static UsbHidDevice factory(Context context, int vid, int pid) {
        try {
            UsbHidDevice[] devices = enumerate(context, vid, pid);
            return devices.length == 0?null:devices[0];
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    @RequiresApi(
        api = 21
    )
    public static UsbHidDevice factory(Context context, int vid, int pid, String serialNumber) {
        try {
            UsbHidDevice[] devices = enumerate(context, vid, pid);
            UsbHidDevice[] var5 = devices;
            int var6 = devices.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                UsbHidDevice device = var5[var7];
                if(device.getSerialNumber().equals(device.getSerialNumber())) {
                    return device;
                }
            }

            return null;
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    public static UsbHidDevice factory(Context context, int vid, int pid, int deviceId) {
        try {
            UsbHidDevice[] devices = enumerate(context, vid, pid);
            UsbHidDevice[] var5 = devices;
            int var6 = devices.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                UsbHidDevice device = var5[var7];
                if(device.getDeviceId() == deviceId) {
                    return device;
                }
            }

            return null;
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    private UsbHidDevice(UsbDevice usbDevice, UsbManager usbManager) {
        this.mUsbDevice = usbDevice;
        this.mUsbManager = usbManager;
    }

    private UsbHidDevice(UsbDevice usbDevice, UsbInterface usbInterface, UsbManager usbManager) {
        this.mUsbDevice = usbDevice;
        this.mUsbInterface = usbInterface;
        this.mUsbManager = usbManager;

        for(int i = 0; i < this.mUsbInterface.getEndpointCount(); ++i) {
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

    }

    public UsbDevice getUsbDevice() {
        return this.mUsbDevice;
    }

    @RequiresApi(
        api = 21
    )
    public String getSerialNumber() {
        return this.mUsbDevice.getSerialNumber();
    }

    public int getDeviceId() {
        return this.mUsbDevice.getDeviceId();
    }

    public void open(Context context, OnUsbHidDeviceListener listener) {
        this.mListener = listener;
        this.mHandler = new Handler(context.getMainLooper());
        if(!this.mUsbManager.hasPermission(this.mUsbDevice)) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            context.registerReceiver(this.mUsbReceiver, filter);
            this.mUsbManager.requestPermission(this.mUsbDevice, permissionIntent);
        } else {
            this.openDevice();
        }

    }

    private void openDevice() {
        this.mUsbInterface = this.getInterface(this.mUsbDevice);
        this.mConnection = this.mUsbManager.openDevice(this.mUsbDevice);
        if(this.mUsbInterface == null) {
            this.onConnectFailed();
        } else if(this.mConnection == null) {
            this.onConnectFailed();
        } else if(!this.mConnection.claimInterface(this.mUsbInterface, true)) {
            this.onConnectFailed();
        } else {
            if(VERSION.SDK_INT >= 21) {
                this.mConnection.setInterface(this.mUsbInterface);
            }

            for(int i = 0; i < this.mUsbInterface.getEndpointCount(); ++i) {
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

        }
    }

    private void onConnectFailed() {
    }

    public void close() {
        this.mConnection.close();
    }

    public void write(byte[] data) {
        this.write(data, 0, data.length);
    }

    public void write(byte[] data, int size) {
        this.write(data, 0, size);
    }

    public void write(byte[] data, int offset, int size) {
        if(offset != 0) {
            data = Arrays.copyOfRange(data, offset, size);
        }

        if(this.mOutUsbEndpoint != null) {
            this.mConnection.bulkTransfer(this.mOutUsbEndpoint, data, size, 1000);
        }

    }

    public byte[] read(int size, int timeout) {
        byte[] buffer = new byte[size];
        int bytesRead = this.mConnection.bulkTransfer(this.mInUsbEndpoint, buffer, size, timeout);
        if(bytesRead > 0 && bytesRead <= size) {
            buffer = Arrays.copyOfRange(buffer, 0, bytesRead);
            return buffer;
        } else {
            return null;
        }
    }

    public byte[] read(int size) {
        return this.read(size, -1);
    }
}
