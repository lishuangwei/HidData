//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xgvr.glasses;

public class utilQuat {
    public utilQuat() {
    }

    static utilQuat.Quat quatInit(float w, float x, float y, float z) {
        utilQuat.Quat quat = new utilQuat.Quat(w, x, y, z);
        return quat;
    }

    static utilQuat.Quat quatNormalize(utilQuat.Quat q1) {
        float m = (float)Math.sqrt((double)(q1.w * q1.w + q1.x * q1.x + q1.y * q1.y + q1.z * q1.z));
        return quatInit(q1.w / m, q1.x / m, q1.y / m, q1.z / m);
    }

    static utilQuat.Quat quatMultiply(utilQuat.Quat q1, utilQuat.Quat q2) {
        utilQuat.Quat result = quatInit(1.0F, 0.0F, 0.0F, 0.0F);
        result.x = q1.x * q2.w + q1.y * q2.z - q1.z * q2.y + q1.w * q2.x;
        result.y = -q1.x * q2.z + q1.y * q2.w + q1.z * q2.x + q1.w * q2.y;
        result.z = q1.x * q2.y - q1.y * q2.x + q1.z * q2.w + q1.w * q2.z;
        result.w = -q1.x * q2.x - q1.y * q2.y - q1.z * q2.z + q1.w * q2.w;
        return quatNormalize(result);
    }

    static utilQuat.Quat quatInverse(utilQuat.Quat q1) {
        utilQuat.Quat result = quatNormalize(q1);
        return quatInit(result.w, -result.x, -result.y, -result.z);
    }

    static utilQuat.Quat quatYawOffset(utilQuat.Quat q1) {
        float w = q1.w;
        float x = 0.0F;
        float y = q1.y;
        float z = 0.0F;
        return quatNormalize(quatInit(w, x, y, z));
    }

    static float quatToYaw(utilQuat.Quat q1) {
        return (float)(2.0D * Math.acos((double)q1.y / Math.sqrt((double)(q1.w * q1.w + q1.y * q1.y))));
    }

    static utilQuat.Quat quatYawToQuat(float yaw) {
        double t0 = Math.cos(0.0D);
        double t1 = Math.sin(0.0D);
        double t2 = Math.cos(0.0D);
        double t3 = Math.sin(0.0D);
        double t4 = Math.cos((double)yaw * 0.5D);
        double t5 = Math.sin((double)yaw * 0.5D);
        double x = t0 * t3 * t4 - t1 * t2 * t5;
        double y = t0 * t2 * t5 + t1 * t3 * t4;
        double z = t1 * t2 * t4 - t0 * t3 * t5;
        double w = t0 * t2 * t4 + t1 * t3 * t5;
        return quatNormalize(quatInit((float)w, (float)x, (float)y, (float)z));
    }

    static void quatTranslatePoints(double angle, float input_y, float input_z, Float output_y, Float output_z) {
        double real_angle = Math.atan((double)(input_y / input_z));
        double angle_rad = angle / 180.0D * 3.14152D;
        double virtual_angle = real_angle - angle;
        double dist_from_origin = Math.sqrt((double)(input_y * input_y + input_z * input_z));
        new Float((double)((float)dist_from_origin) * Math.sin(virtual_angle));
        new Float((double)((float)dist_from_origin) * Math.cos(virtual_angle));
    }

    static double quatDot(utilQuat.Quat q1, utilQuat.Quat q2) {
        return (double)(q1.x * q2.x + q1.y * q2.y + q1.z * q2.z + q1.w * q2.w);
    }

    static void quatSlerp(utilQuat.Quat old_quat, utilQuat.Quat new_quat, utilQuat.Quat output_quat) {
        double alpha = 0.8D;
        double spin = 0.0D;
        double M_PI = 3.1415D;
        double cos_t = quatDot(old_quat, new_quat);
        boolean bflip;
        if(cos_t < 0.0D) {
            cos_t = -cos_t;
            bflip = true;
        } else {
            bflip = false;
        }

        double beta;
        if(1.0D - cos_t < 1.0E-7D) {
            beta = 1.0D - alpha;
        } else {
            double theta = Math.acos(cos_t);
            double phi = theta + spin * M_PI;
            double sin_t = Math.sin(theta);
            beta = Math.sin(theta - alpha * phi) / sin_t;
            alpha = Math.sin(alpha * phi) / sin_t;
        }

        if(bflip) {
            alpha = -alpha;
        }

        output_quat.x = (float)(beta * (double)old_quat.x + alpha * (double)new_quat.x);
        output_quat.y = (float)(beta * (double)old_quat.y + alpha * (double)new_quat.y);
        output_quat.z = (float)(beta * (double)old_quat.z + alpha * (double)new_quat.z);
        output_quat.w = (float)(beta * (double)old_quat.w + alpha * (double)new_quat.w);
    }

    public static class Quat {
        public float w;
        public float x;
        public float y;
        public float z;

        public Quat(float w, float x, float y, float z) {
            this.w = w;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
