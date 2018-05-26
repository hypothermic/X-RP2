/* X-RP - decompiled with CFR */
package eloraam.core;

import java.util.Formatter;
import java.util.Locale;

public class Vector3 {

    public double x;
    public double y;
    public double z;

    public Vector3() {
    }

    public Vector3(double d, double d2, double d3) {
	this.x = d;
	this.y = d2;
	this.z = d3;
    }

    public Vector3(Vector3 vector3) {
	this.x = vector3.x;
	this.y = vector3.y;
	this.z = vector3.z;
    }

    public Object clone() {
	return new Vector3(this);
    }

    public void set(double d, double d2, double d3) {
	this.x = d;
	this.y = d2;
	this.z = d3;
    }

    public void set(Vector3 vector3) {
	this.x = vector3.x;
	this.y = vector3.y;
	this.z = vector3.z;
    }

    public double dotProduct(Vector3 vector3) {
	return vector3.x * this.x + vector3.y * this.y + vector3.z * this.z;
    }

    public double dotProduct(double d, double d2, double d3) {
	return d * this.x + d2 * this.y + d3 * this.z;
    }

    public void crossProduct(Vector3 vector3) {
	double d = this.y * vector3.z - this.z * vector3.y;
	double d2 = this.z * vector3.x - this.x * vector3.z;
	double d3 = this.x * vector3.y - this.y * vector3.x;
	this.x = d;
	this.y = d2;
	this.z = d3;
    }

    public void add(double d, double d2, double d3) {
	this.x += d;
	this.y += d2;
	this.z += d3;
    }

    public void add(Vector3 vector3) {
	this.x += vector3.x;
	this.y += vector3.y;
	this.z += vector3.z;
    }

    public void subtract(Vector3 vector3) {
	this.x -= vector3.x;
	this.y -= vector3.y;
	this.z -= vector3.z;
    }

    public void multiply(double d) {
	this.x *= d;
	this.y *= d;
	this.z *= d;
    }

    public double mag() {
	return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double magSquared() {
	return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public void normalize() {
	double d = this.mag();
	if (d == 0.0) {
	    return;
	}
	this.multiply(1.0 / d);
    }

    public String toString() {
	StringBuilder stringBuilder = new StringBuilder();
	Formatter formatter = new Formatter(stringBuilder, Locale.US);
	formatter.format("Vector:\n", new Object[0]);
	formatter.format("  < %f %f %f >\n", this.x, this.y, this.z);
	formatter.close(); // X-RP2: prevent rsc leak
	return stringBuilder.toString();
    }
}
