//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xgvr.glasses;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.xgvr.glasses.usbhost.OnUsbHidDeviceListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DeviceManager extends BaseDeviceManager {
    private final String TAG = "DeviceManager";
    private UsbManager mUsbManager;
    private Context mContext;
    private List<HidDevice> mDeviceList = new ArrayList();
    private HidDevice mCurDevice;
    private boolean mOpenSuccess = false;
    private int mNoDataCount = 0;

    public DeviceManager() {
    }

    public int init(Context context) {
        this.mContext = context;
        this.mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (this.mUsbManager == null) {
            glog.i("DeviceManager", "no support usb device");
            return -101;
        } else {
            return 0;
        }
    }

    public int startScan() {
        if (this.mUsbManager == null) {
            glog.e("DeviceManager", " mUsbManager is null");
            return -1;
        } else {
            glog.i("tst", "startScan 1");
            this.mDeviceList.clear();
            glog.i("tst", "startScan 2");
            Map<String, UsbDevice> devices = this.mUsbManager.getDeviceList();
            glog.i("tst", "startScan size = " + devices.size());
            Iterator var2 = devices.values().iterator();

            while (var2.hasNext()) {
                UsbDevice device = (UsbDevice) var2.next();
                if (device.getVendorId() == 11036 && this.comparePid(device.getProductId())) {
                    HidDevice hidDevice = new HidDevice(this.mUsbManager, device);
                    this.mDeviceList.add(hidDevice);
                    glog.i("DeviceManager", "device vid =  " + device.getVendorId() + " pid = " + device.getProductId());
                }
            }

            glog.i("DeviceManager", "get device " + this.mDeviceList.size());
            if (this.mDeviceList.size() <= 0) {
                return -1;
            } else {
                this.mCurDevice = (HidDevice) this.mDeviceList.get(0);
                return this.mDeviceList.size();
            }
        }
    }

    private boolean comparePid(int pid) {
        return pid == 1 || pid == 512 || pid == 513 || pid == 514 || pid == 515;
    }

    public int setCurDevice(int vid, int pid) {
        glog.i("DeviceManager", "set device " + vid + ", " + pid);

        for (int i = 0; i < this.mDeviceList.size(); ++i) {
            if (((HidDevice) this.mDeviceList.get(i)).getVid() == vid && ((HidDevice) this.mDeviceList.get(i)).getPid() == pid) {
                this.mCurDevice = (HidDevice) this.mDeviceList.get(i);
                return 0;
            }
        }

        return -103;
    }

    public int open() {
        if (this.mCurDevice == null) {
            glog.e("DeviceManager", "no hid device now");
            return -1;
        } else {
            this.mOpenSuccess = false;
            this.mCurDevice.open(this.mContext, new OnUsbHidDeviceListener() {
                public void onUsbHidDeviceConnected(HidDevice device) {
                    DeviceManager.this.mOpenSuccess = true;
                    glog.i("DeviceManager", "connect success");
                }

                public void onUsbHidDeviceConnectFailed(HidDevice device) {
                    DeviceManager.this.mOpenSuccess = false;
                    glog.i("DeviceManager", "connect fail");
                }
            });
            return 0;
        }
    }

    public int read(byte[] data, int length) {
        if (this.mCurDevice == null) {
            return 0;
        } else {
            int ret = this.mCurDevice.read(data, length);
            ++this.mNoDataCount;
            if (ret < 0 && this.isConnect() && this.mNoDataCount > 10) {
                this.mNoDataCount = 0;
                this.mOpenSuccess = false;
            }

            return ret;
        }
    }

    public int write(byte[] data, int length) {
        return this.mCurDevice == null ? 0 : this.mCurDevice.write(data, length);
    }

    public int stop() {
        return this.mCurDevice != null ? this.mCurDevice.close() : 0;
    }

    public int getPid() {
        return this.mCurDevice != null ? this.mCurDevice.getPid() : 0;
    }

    public boolean isConnect() {
        if (this.mCurDevice != null ) {
            if(mCurDevice.getUsbDevice() != null && mUsbManager.getDeviceList().size() > 0 && mCurDevice.isConnection() && mOpenSuccess){
                return true;
            }
            return false;
        } else {
            this.mOpenSuccess = false;
            this.mCurDevice = null;
            return false;
        }
    }

    public int getDataType() {
        int pid = 0;
        if (mCurDevice != null) {
            pid = mCurDevice.getPid();
        }
        return pid == 1 ? 1002 : (pid != 515 && pid != 514 && pid != 512 && pid != 513 ? 1000 : 1003);
    }
}
