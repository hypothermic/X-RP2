/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.Matrix3;
import eloraam.core.Quat;

public class MathLib {

    private static Matrix3[] orientMatrixList;
    private static Quat[] orientQuatList;

    public static void orientMatrix(Matrix3 matrix3, int n, int n2) {
	matrix3.set(orientMatrixList[n * 4 + n2]);
    }

    public static Quat orientQuat(int n, int n2) {
	return new Quat(orientQuatList[n * 4 + n2]);
    }

    static {
	int n;
	orientMatrixList = new Matrix3[24];
	orientQuatList = new Quat[24];
	Quat quat = Quat.aroundAxis(1.0, 0.0, 0.0, 3.141592653589793);
	for (n = 0; n < 4; ++n) {
	    Quat quat2;
	    MathLib.orientQuatList[n] = quat2 = Quat.aroundAxis(0.0, 1.0, 0.0, -1.5707963267948966 * (double) n);
	    quat2 = new Quat(quat2);
	    quat2.multiply(quat);
	    MathLib.orientQuatList[n + 4] = quat2;
	}
	for (n = 0; n < 4; ++n) {
	    int n2 = (n >> 1 | n << 1) & 3;
	    Quat quat3 = Quat.aroundAxis(0.0, 0.0, 1.0, 1.5707963267948966);
	    quat3.multiply(Quat.aroundAxis(0.0, 1.0, 0.0, 1.5707963267948966 * (double) (n2 + 1)));
	    for (int i = 0; i < 4; ++i) {
		Quat quat4 = new Quat(orientQuatList[i]);
		quat4.multiply(quat3);
		MathLib.orientQuatList[8 + 4 * n + i] = quat4;
	    }
	}
	for (n = 0; n < 24; ++n) {
	    MathLib.orientMatrixList[n] = new Matrix3(orientQuatList[n]);
	}
    }
}
