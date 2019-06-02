/* X-RP - decompiled with CFR */
package eloraam.core;

import java.util.Formatter;
import java.util.Locale;

public class Quat {

    public double x;
    public double y;
    public double z;
    public double s;
    public static final double SQRT2 = Math.sqrt(2.0);

    public Quat() {
        this.s = 1.0;
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public Quat(Quat quat) {
        this.x = quat.x;
        this.y = quat.y;
        this.z = quat.z;
        this.s = quat.s;
    }

    public Quat(double d, double d2, double d3, double d4) {
        this.x = d2;
        this.y = d3;
        this.z = d4;
        this.s = d;
    }

    public void set(Quat quat) {
        this.x = quat.x;
        this.y = quat.y;
        this.z = quat.z;
        this.s = quat.s;
    }

    public static Quat aroundAxis(double d, double d2, double d3, double d4) {
        double d5 = Math.sin(d4 *= 0.5);
        return new Quat(Math.cos(d4), d * d5, d2 * d5, d3 * d5);
    }

    public void multiply(Quat quat) {
        double d = this.s * quat.s - this.x * quat.x - this.y * quat.y - this.z * quat.z;
        double d2 = this.s * quat.x + this.x * quat.s - this.y * quat.z + this.z * quat.y;
        double d3 = this.s * quat.y + this.x * quat.z + this.y * quat.s - this.z * quat.x;
        double d4 = this.s * quat.z - this.x * quat.y + this.y * quat.x + this.z * quat.s;
        this.s = d;
        this.x = d2;
        this.y = d3;
        this.z = d4;
    }

    public void rightMultiply(Quat quat) {
        double d = this.s * quat.s - this.x * quat.x - this.y * quat.y - this.z * quat.z;
        double d2 = this.s * quat.x + this.x * quat.s + this.y * quat.z - this.z * quat.y;
        double d3 = this.s * quat.y - this.x * quat.z + this.y * quat.s + this.z * quat.x;
        double d4 = this.s * quat.z + this.x * quat.y - this.y * quat.x + this.z * quat.s;
        this.s = d;
        this.x = d2;
        this.y = d3;
        this.z = d4;
    }

    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.s * this.s);
    }

    public void normalize() {
        double d = this.mag();
        if (d == 0.0) {
            return;
        }
        d = 1.0 / d;
        this.x *= d;
        this.y *= d;
        this.z *= d;
        this.s *= d;
    }

    public void rotate(Vector3 vector3) {
        double d = (-this.x) * vector3.x - this.y * vector3.y - this.z * vector3.z;
        double d2 = this.s * vector3.x + this.y * vector3.z - this.z * vector3.y;
        double d3 = this.s * vector3.y - this.x * vector3.z + this.z * vector3.x;
        double d4 = this.s * vector3.z + this.x * vector3.y - this.y * vector3.x;
        vector3.x = d2 * this.s - d * this.x - d3 * this.z + d4 * this.y;
        vector3.y = d3 * this.s - d * this.y + d2 * this.z - d4 * this.x;
        vector3.z = d4 * this.s - d * this.z - d2 * this.y + d3 * this.x;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder, Locale.US);
        formatter.format("Quaternion:\n", new Object[0]);
        formatter.format("  < %f %f %f %f >\n", this.s, this.x, this.y, this.z);
        formatter.close(); // X-RP2: prevent rsc leak
        return stringBuilder.toString();
    }
}
