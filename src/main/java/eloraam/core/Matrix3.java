/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.Quat;
import eloraam.core.Vector3;
import java.util.Formatter;
import java.util.Locale;

public class Matrix3 {

    public double xx;
    public double xy;
    public double xz;
    public double yx;
    public double yy;
    public double yz;
    public double zx;
    public double zy;
    public double zz;

    public Matrix3() {
    }

    public Matrix3(Quat quat) {
	this.set(quat);
    }

    public void set(Quat quat) {
	this.xx = quat.s * quat.s + quat.x * quat.x - quat.z * quat.z - quat.y * quat.y;
	this.xy = 2.0 * (quat.s * quat.z + quat.x * quat.y);
	this.xz = 2.0 * (quat.x * quat.z - quat.s * quat.y);
	this.yx = 2.0 * (quat.x * quat.y - quat.s * quat.z);
	this.yy = quat.s * quat.s + quat.y * quat.y - quat.z * quat.z - quat.x * quat.x;
	this.yz = 2.0 * (quat.s * quat.x + quat.y * quat.z);
	this.zx = 2.0 * (quat.s * quat.y + quat.x * quat.z);
	this.zy = 2.0 * (quat.y * quat.z - quat.s * quat.x);
	this.zz = quat.s * quat.s + quat.z * quat.z - quat.y * quat.y - quat.x * quat.x;
    }

    public void set(Matrix3 matrix3) {
	this.xx = matrix3.xx;
	this.xy = matrix3.xy;
	this.xz = matrix3.xz;
	this.yx = matrix3.yx;
	this.yy = matrix3.yy;
	this.yz = matrix3.yz;
	this.zx = matrix3.zx;
	this.zy = matrix3.zy;
	this.zz = matrix3.zz;
    }

    public Matrix3 multiply(Matrix3 matrix3) {
	Matrix3 matrix32 = new Matrix3();
	matrix32.xx = this.xx * matrix3.xx + this.xy * matrix3.yx + this.xz * matrix3.zx;
	matrix32.xy = this.xx * matrix3.xy + this.xy * matrix3.yy + this.xz * matrix3.zy;
	matrix32.xz = this.xx * matrix3.xz + this.xy * matrix3.yz + this.xz * matrix3.zz;
	matrix32.yx = this.yx * matrix3.xx + this.yy * matrix3.yx + this.yz * matrix3.zx;
	matrix32.yy = this.yx * matrix3.xy + this.yy * matrix3.yy + this.yz * matrix3.zy;
	matrix32.yz = this.yx * matrix3.xz + this.yy * matrix3.yz + this.yz * matrix3.zz;
	matrix32.zx = this.zx * matrix3.xx + this.zy * matrix3.yx + this.zz * matrix3.zx;
	matrix32.zy = this.zx * matrix3.xy + this.zy * matrix3.yy + this.zz * matrix3.zy;
	matrix32.zz = this.zx * matrix3.xz + this.zy * matrix3.yz + this.zz * matrix3.zz;
	return matrix32;
    }

    public Vector3 getBasisVector(int n) {
	if (n == 0) {
	    return new Vector3(this.xx, this.xy, this.xz);
	}
	if (n == 1) {
	    return new Vector3(this.yx, this.yy, this.yz);
	}
	return new Vector3(this.zx, this.zy, this.zz);
    }

    public double det() {
	return this.xx * (this.yy * this.zz - this.yz * this.zy) - this.xy * (this.yx * this.zz - this.yz * this.zx) + this.xz * (this.yx * this.zy - this.yy * this.zx);
    }

    public void rotate(Vector3 vector3) {
	double d = this.xx * vector3.x + this.yx * vector3.y + this.zx * vector3.z;
	double d2 = this.xy * vector3.x + this.yy * vector3.y + this.zy * vector3.z;
	double d3 = this.xz * vector3.x + this.yz * vector3.y + this.zz * vector3.z;
	vector3.x = d;
	vector3.y = d2;
	vector3.z = d3;
    }

    public String toString() {
	StringBuilder stringBuilder = new StringBuilder();
	Formatter formatter = new Formatter(stringBuilder, Locale.US);
	formatter.format("Matrix:\n", new Object[0]);
	formatter.format("  < %f %f %f >\n", this.xx, this.xy, this.xz);
	formatter.format("  < %f %f %f >\n", this.yx, this.yy, this.yz);
	formatter.format("  < %f %f %f >\n", this.zx, this.zy, this.zz);
	formatter.close(); // X-RP2: prevent rsc leak
	return stringBuilder.toString();
    }
}
