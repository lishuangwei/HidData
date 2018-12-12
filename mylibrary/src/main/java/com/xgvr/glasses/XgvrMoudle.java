//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xgvr.glasses;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.xgvr.glasses.BaseDeviceManager;
import com.xgvr.glasses.utilQuat.Quat;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class XgvrMoudle {
    private final String TAG = XgvrMoudle.class.getName();
    private static XgvrMoudle mInstance;
    private BaseDeviceManager mDeviceManager = new DeviceManager();
    private XgvrMoudle.ReceiveHidThread mDataReceiveThread;
    private boolean mIsRun;
    private Handler mHandler;
    private boolean mReCenter = false;
    private Quat mCenterQuat = new Quat(1.0F, 0.0F, 0.0F, 0.0F);
    private Lock lock = new ReentrantLock();
    private float[] date;
    boolean stop;
    byte[] buf = new byte[64];
    float[] data = new float[9];
    Quat rawQuat = new Quat(0, 0, 0, 0);
    HidDataInfoD3 dataInfoD3 = new HidDataInfoD3();
    HidDataInfoS1 dataInfoS1 = new HidDataInfoS1();

    private XgvrMoudle() {
    }

    public static XgvrMoudle getInstance() {
        if (mInstance == null) {
            mInstance = new XgvrMoudle();
        }

        return mInstance;
    }

    public int init(Context context) {
        //DJILogUtil.init(context);
        this.mHandler = new Handler(context.getMainLooper());
        return this.mDeviceManager.init(context);
    }

    public int startScan() {
        return this.mDeviceManager.startScan() > 0 ? 0 : -102;
    }

    public int isConnect() {
        return this.mDeviceManager.isConnect() ? 0 : -102;
    }

    public int open() {
        int ret = this.mDeviceManager.open();
        return ret;
    }

    public void stop() {
        if (this.mDataReceiveThread != null) {
            this.mDataReceiveThread.cancel(true);
        }

        this.mIsRun = false;
        this.mDeviceManager.stop();
        if (this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages(Boolean.valueOf(true));
            this.mHandler = null;
        }

    }

    public float[] readData(int type) {
        int ret = this.mDeviceManager.read(buf, buf.length);
        if (ret >= 0) {
            if (type == 1002) {
                HidDataInfoS1 dataInfo = (HidDataInfoS1) this.parse(buf, 1002);
                glog.i(this.TAG, "data = " + Arrays.toString(buf));
                data[0] = dataInfo.quat[0];
                data[1] = dataInfo.quat[1];
                data[2] = dataInfo.quat[2];
                data[3] = dataInfo.quat[3];
                data[4] = 1.0F;
                data[5] = 0.0F;
                data[6] = 0.0F;
                data[7] = 0.0F;
                data[8] = 0.0F;
            } else if (type == 1003) {
                HidDataInfoD3 dataInfo = (HidDataInfoD3) this.parse(buf, 1003);
                glog.i(this.TAG, "data = " + Arrays.toString(buf));
                data[0] = dataInfo.quat[0];
                data[1] = dataInfo.quat[1];
                data[2] = dataInfo.quat[2];
                data[3] = dataInfo.quat[3];
                data[4] = (float) dataInfo.als;
                data[5] = (float) dataInfo.button;
                data[6] = dataInfo.mag[0];
                data[7] = dataInfo.mag[1];
                data[8] = dataInfo.mag[2];
                glog.i(this.TAG, "data 6 = " + data[6]);
                glog.i(this.TAG, "data 7= " + data[7]);
                glog.i(this.TAG, "data 8= " + data[8]);
            }

            rawQuat.w = data[3];
            rawQuat.x = data[0];
            rawQuat.y = data[1];
            rawQuat.z = data[2];
            if (this.mReCenter) {
                this.mReCenter = false;
                this.mCenterQuat = utilQuat.quatInverse(utilQuat.quatYawOffset(rawQuat));
            }

            Quat qRotation = utilQuat.quatMultiply(this.mCenterQuat, rawQuat);
            String str = data[0] + " " + data[1] + " " + data[2] + " " + data[3] + " \n" + this.mCenterQuat.x + " " + this.mCenterQuat.y + " " + this.mCenterQuat.z + " " + this.mCenterQuat.w + "\n" + qRotation.x + " " + qRotation.y + " " + qRotation.z + " " + qRotation.w;
            glog.i(this.TAG, str);
            data[0] = qRotation.x;
            data[1] = qRotation.y;
            data[2] = qRotation.z;
            data[3] = qRotation.w;
            glog.i(this.TAG, "read data end ");
            return data;
        } else {
            //DJILogUtil.i(this.TAG, "no data read ret = " + ret);
            return null;
        }
    }

    public Object parse(byte[] data, int type) {
        if (type == 1003) {
            dataInfoD3.type = utils.getShort(data[0]);
            dataInfoD3.panel_status = utils.getShort(data[1]);
            dataInfoD3.timestamp = utils.getShort(data, 2);
            dataInfoD3.tempetature = utils.getShort(data[4]);
            dataInfoD3.ipd = utils.getShort(data[5]);
            dataInfoD3.quat[0] = utils.getFloat(data, 8);
            dataInfoD3.quat[1] = utils.getFloat(data, 12);
            dataInfoD3.quat[2] = utils.getFloat(data, 16);
            dataInfoD3.quat[3] = utils.getFloat(data, 20);
            dataInfoD3.acc[0] = utils.getFloat(data, 24);
            dataInfoD3.acc[1] = utils.getFloat(data, 28);
            dataInfoD3.acc[2] = utils.getFloat(data, 32);
            dataInfoD3.gyr[0] = utils.getFloat(data, 36);
            dataInfoD3.gyr[1] = utils.getFloat(data, 40);
            dataInfoD3.gyr[2] = utils.getFloat(data, 44);
            dataInfoD3.mag[0] = utils.getFloat(data, 48);
            dataInfoD3.mag[1] = utils.getFloat(data, 52);
            dataInfoD3.mag[2] = utils.getFloat(data, 56);
            dataInfoD3.touch[0] = utils.getShort(data[60]);
            dataInfoD3.touch[1] = utils.getShort(data[61]);
            dataInfoD3.als = utils.getShort(data[62]);
            dataInfoD3.button = utils.getShort(data[63]);
            String str = dataInfoD3.type + ", " + dataInfoD3.panel_status + ", " + dataInfoD3.timestamp + ", " + dataInfoD3.tempetature + ", " + dataInfoD3.ipd + ", " + dataInfoD3.quat[0] + ", " + dataInfoD3.quat[1] + ", " + dataInfoD3.quat[2] + ", " + dataInfoD3.quat[3] + ", " + dataInfoD3.acc[0] + ", " + dataInfoD3.acc[1] + ", " + dataInfoD3.acc[2] + ", " + dataInfoD3.gyr[0] + ", " + dataInfoD3.gyr[1] + ", " + dataInfoD3.gyr[2] + ", " + dataInfoD3.mag[0] + ", " + dataInfoD3.mag[1] + ", " + dataInfoD3.mag[2] + ", " + dataInfoD3.touch[0] + ", " + dataInfoD3.touch[1] + ", " + dataInfoD3.als + ", " + dataInfoD3.button;
            glog.i(this.TAG, str);
            return dataInfoD3;
        } else if (type != 1002) {
            return null;
        } else {

            dataInfoS1.type = utils.getShort(data[0]);
            dataInfoS1.state = utils.getShort(data[1]);
            dataInfoS1.timestamp = utils.getShort(data, 2);
            dataInfoS1.times_count = utils.getShort(data, 4);
            dataInfoS1.tempetature = utils.getShort(data[6]);
            dataInfoS1.ipd = utils.getShort(data[7]);
            dataInfoS1.quat[0] = utils.getFloat(data, 8);
            dataInfoS1.quat[1] = utils.getFloat(data, 12);
            dataInfoS1.quat[2] = utils.getFloat(data, 16);
            dataInfoS1.quat[3] = utils.getFloat(data, 20);

            int i;
            for (i = 0; i < 32; ++i) {
                dataInfoS1.samples[i] = utils.getShort(data[24 + i]);
            }

            dataInfoS1.msg_x = utils.getShort(data, 56);
            dataInfoS1.msg_y = utils.getShort(data, 58);
            dataInfoS1.msg_z = utils.getShort(data, 60);
            dataInfoS1.touch_x = utils.getShort(data[62]);
            dataInfoS1.touch_y = utils.getShort(data[63]);
            String str = dataInfoS1.type + ", " + dataInfoS1.state + ", " + dataInfoS1.timestamp + ", " + dataInfoS1.times_count + ", " + dataInfoS1.tempetature + ", " + dataInfoS1.ipd + ", " + dataInfoS1.quat[0] + ", " + dataInfoS1.quat[1] + ", " + dataInfoS1.quat[2] + ", " + dataInfoS1.quat[3];

            for (i = 0; i < 32; ++i) {
                str = str + ", " + dataInfoS1.samples[i];
            }

            str = str + ", " + dataInfoS1.msg_x + ", " + dataInfoS1.msg_y + ", " + dataInfoS1.msg_z + ", " + dataInfoS1.msg_x + ", " + dataInfoS1.msg_y;
            glog.i(this.TAG, str);
            return dataInfoS1;
        }
    }

    public void startGetDate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stop) {
                    if (XgvrMoudle.getInstance().xgvr_hmd_is_present() == 0) {
                        long s = System.currentTimeMillis();
                        lock.lock();
                        date = xgvr_hmd_data_get();
                        lock.unlock();
                        long e = System.currentTimeMillis() - s;
                        if (e > 5) {
                            Log.d("shuang", "startGetDate COST =" + e);
                        }
                    }
                }
            }
        }).start();
    }

    public float[] getDates() {
        return date;
    }

    public int xgvr_hdm_init(Context context) {
        int ret;
        if ((ret = this.init(context)) != 0) {
            return ret;
        } else if ((ret = this.startScan()) != 0) {
            return ret;
        } else {
            this.open();
            return ret;
        }
    }

    public int xgvr_hmd_deinit() {
        this.stop();
        stop = true;
        return 0;
    }

    public int xgvr_hmd_is_present() {
        glog.i("tst", "xgvr_hmd_is_present1");
        int ret;
        if ((ret = this.isConnect()) == 0) {
            return ret;
        } else if ((ret = this.startScan()) != 0) {
            return ret;
        } else {
            this.open();
            return ret;
        }
    }

    public float[] xgvr_hmd_data_get() {
        int dataType = this.mDeviceManager.getDataType();
        glog.i(this.TAG, "data type = " + dataType);
        long time1 = System.currentTimeMillis();
        float[] datas = this.readData(dataType);
        glog.i(this.TAG, "cost time = " + (System.currentTimeMillis() - time1));
        return datas;
    }

    public void xgvr_hmd_ReCenter() {
        this.mReCenter = true;
    }

    public class ReceiveHidThread extends AsyncTask {
        public ReceiveHidThread() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Object doInBackground(Object[] objects) {
            return null;
        }

        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }
}
