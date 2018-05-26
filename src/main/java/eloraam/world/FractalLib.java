/* X-RP - decompiled with CFR */
package eloraam.world;

import eloraam.core.Vector3;

public class FractalLib {

    public static long hash64shift(long l) {
	l = (l ^ -1) + (l << 21);
	l ^= l >>> 24;
	l = l + (l << 3) + (l << 8);
	l ^= l >>> 14;
	l = l + (l << 2) + (l << 4);
	l ^= l >>> 28;
	l += l << 31;
	return l;
    }

    public static double perturb(long l, float f, float f2, int n) {
	double d = 0.0;
	double d2 = 1.0;
	double d3 = 1.0;
	for (int i = 0; i < n; ++i) {
	    long l2 = (long) Math.floor((double) f * d3);
	    long l3 = FractalLib.hash64shift(l + l2);
	    double d4 = Double.longBitsToDouble(4607182418800017408L | l3 & 0xFFFFFFFFFFFFFL) - 1.0;
	    d += d2 * d4 * Math.sin(6.283185307179586 * (double) f * d3);
	    d3 *= 2.0;
	    d2 *= (double) f2;
	}
	return d;
    }

    public static void fillVector(Vector3 vector3, Vector3 vector32, Vector3 vector33, float f, long l) {
	double d = 4.0 * Math.sin(3.141592653589793 * (double) f);
	vector3.x = vector32.x + (double) (f * f) * vector33.x + d * FractalLib.perturb(l, f, 0.7f, 5);
	vector3.y = vector32.y + (double) f * vector33.y + d * FractalLib.perturb(l + 1, f, 0.7f, 5);
	vector3.z = vector32.z + (double) (f * f) * vector33.z + d * FractalLib.perturb(l + 2, f, 0.7f, 5);
    }

    public static int mdist(Vector3 vector3, Vector3 vector32) {
	return (int) (Math.abs(Math.floor(vector3.x) - Math.floor(vector32.x)) + Math.abs(Math.floor(vector3.y) - Math.floor(vector32.y)) + Math.abs(Math.floor(vector3.z) - Math.floor(vector32.z)));
    }

    public static class BlockRay {

	private Vector3 p1;
	private Vector3 p2;
	private Vector3 dv;
	public Vector3 enter;
	public Vector3 exit;
	public int xp;
	public int yp;
	public int zp;
	public int dir;
	public int face;

	public void set(Vector3 vector3, Vector3 vector32) {
	    this.p1.set(vector3);
	    this.p2.set(vector32);
	    this.dv.set(vector32);
	    this.dv.subtract(vector3);
	    this.exit.set(vector3);
	    this.xp = (int) Math.floor(vector3.x);
	    this.yp = (int) Math.floor(vector3.y);
	    this.zp = (int) Math.floor(vector3.z);
	    this.dir = 0;
	    this.dir |= vector32.x <= vector3.x ? 0 : 4;
	    this.dir |= vector32.y <= vector3.y ? 0 : 1;
	    this.dir |= vector32.z <= vector3.z ? 0 : 2;
	}

	boolean step() {
	    double d;
	    int n;
	    double d2 = 1.0;
	    int n2 = -1;
	    if (this.dv.x != 0.0) {
		n = this.xp;
		if ((this.dir & 4) > 0) {
		    ++n;
		}
		if ((d = ((double) n - this.p1.x) / this.dv.x) >= 0.0 && d <= d2) {
		    d2 = d;
		    int n3 = n2 = (this.dir & 4) <= 0 ? 5 : 4;
		}
	    }
	    if (this.dv.y != 0.0) {
		n = this.yp;
		if ((this.dir & 1) > 0) {
		    ++n;
		}
		if ((d = ((double) n - this.p1.y) / this.dv.y) >= 0.0 && d <= d2) {
		    d2 = d;
		    int n4 = n2 = (this.dir & 1) <= 0 ? 1 : 0;
		}
	    }
	    if (this.dv.z != 0.0) {
		n = this.zp;
		if ((this.dir & 2) > 0) {
		    ++n;
		}
		if ((d = ((double) n - this.p1.z) / this.dv.z) >= 0.0 && d <= d2) {
		    d2 = d;
		    n2 = (this.dir & 2) <= 0 ? 3 : 2;
		}
	    }
	    this.face = n2;
	    switch (n2) {
	    case 0: {
		++this.yp;
		break;
	    }
	    case 1: {
		--this.yp;
		break;
	    }
	    case 2: {
		++this.zp;
		break;
	    }
	    case 3: {
		--this.zp;
		break;
	    }
	    case 4: {
		++this.xp;
		break;
	    }
	    case 5: {
		--this.xp;
	    }
	    }
	    this.enter.set(this.exit);
	    this.exit.set(this.dv);
	    this.exit.multiply(d2);
	    this.exit.add(this.p1);
	    return d2 >= 1.0;
	}

	public BlockRay(Vector3 vector3, Vector3 vector32) {
	    this.p1 = new Vector3(vector3);
	    this.p2 = new Vector3(vector32);
	    this.dv = new Vector3(vector32);
	    this.dv.subtract(vector3);
	    this.exit = new Vector3(vector3);
	    this.enter = new Vector3();
	    this.xp = (int) Math.floor(vector3.x);
	    this.yp = (int) Math.floor(vector3.y);
	    this.zp = (int) Math.floor(vector3.z);
	    this.dir = 0;
	    this.dir |= vector32.x <= vector3.x ? 0 : 4;
	    this.dir |= vector32.y <= vector3.y ? 0 : 1;
	    this.dir |= vector32.z <= vector3.z ? 0 : 2;
	}
    }

    public static class BlockSnake {

	int fep = -1;
	BlockRay ray;
	Vector3 org;
	Vector3 dest;
	Vector3 fracs;
	Vector3 frace;
	long seed;

	public boolean iterate() {
	    if (this.fep == -1) {
		++this.fep;
		return true;
	    }
	    if (!this.ray.step()) {
		return true;
	    }
	    if (this.fep == 8) {
		return false;
	    }
	    this.fracs.set(this.frace);
	    FractalLib.fillVector(this.frace, this.org, this.dest, (float) this.fep / 8.0f, this.seed);
	    this.ray.set(this.fracs, this.frace);
	    ++this.fep;
	    return true;
	}

	Vector3 get() {
	    return new Vector3(this.ray.xp, this.ray.yp, this.ray.zp);
	}

	public BlockSnake(Vector3 vector3, Vector3 vector32, long l) {
	    this.org = new Vector3(vector3);
	    this.dest = new Vector3(vector32);
	    this.fracs = new Vector3(vector3);
	    this.frace = new Vector3();
	    this.seed = l;
	    FractalLib.fillVector(this.frace, this.org, this.dest, 0.125f, this.seed);
	    this.ray = new BlockRay(this.fracs, this.frace);
	}
    }

}
