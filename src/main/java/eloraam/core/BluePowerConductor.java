/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.*;

public abstract class BluePowerConductor {

    private static int[] dirmap = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 11, 14, 18, 23};
    private static double indscale = 0.07;
    private static final double gparallel = 0.5;
    int imask = 0;
    double[] currents;
    public double Vcap = 0.0;
    public double Icap = 0.0;
    public double Veff = 0.0;
    int lastTick = 0;
    public double It1 = 0.0;
    public double Itot = 0.0;

    public abstract TileEntity getParent();

    public abstract double getInvCap();

    public int getChargeScaled(int n) {
        return 0;
    }

    public int getFlowScaled(int n) {
        return 0;
    }

    public double getResistance() {
        return 0.01;
    }

    public void recache(int n, int n2) {
        int n3;
        int n4 = 0;
        for (n3 = 0; n3 < 3; ++n3) {
            if ((n & RedPowerLib.getConDirMask(n3 * 2)) <= 0)
                continue;
            n4 |= 1 << n3;
        }
        for (n3 = 0; n3 < 12; ++n3) {
            if ((n2 & 1 << dirmap[n3]) <= 0)
                continue;
            n4 |= 8 << n3;
        }
        if (this.imask == n4) {
            return;
        }
        double[] arrd = new double[Integer.bitCount(n4)];
        int n5 = 0;
        int n6 = 0;
        for (int i = 0; i < 15; ++i) {
            int n7 = 1 << i;
            double d = 0.0;
            if ((this.imask & n7) > 0) {
                d = this.currents[n5++];
            }
            if ((n4 & n7) <= 0)
                continue;
            arrd[n6++] = d;
        }
        this.currents = arrd;
        this.imask = n4;
    }

    public double getVoltage() {
        long l = this.getParent().world.getTime();
        if ((l & 65535) == (long) this.lastTick) {
            return this.Vcap;
        }
        this.lastTick = (int) (l & 65535);
        this.Itot = 0.5 * this.It1;
        this.It1 = 0.0;
        this.Vcap += 0.05 * this.Icap * this.getInvCap();
        this.Icap = 0.0;
        return this.Vcap;
    }

    public void applyCurrent(double d) {
        this.getVoltage();
        this.Icap += d;
        this.It1 += Math.abs(d);
    }

    public void drawPower(double d) {
        double d2 = this.Vcap * this.Vcap - 0.1 * d * this.getInvCap();
        double d3 = d2 >= 0.0 ? Math.sqrt(d2) - this.Vcap : 0.0;
        this.applyDirect(20.0 * d3 / this.getInvCap());
    }

    public void applyPower(double d) {
        double d2 = Math.sqrt(this.Vcap * this.Vcap + 0.1 * d * this.getInvCap()) - this.Vcap;
        this.applyDirect(20.0 * d2 / this.getInvCap());
    }

    public void applyDirect(double d) {
        this.applyCurrent(d);
    }

    public void iterate() {
        TileEntity tileEntity = this.getParent();
        World world = tileEntity.world;
        this.getVoltage();
        int n = this.imask;
        int n2 = 0;
        while (n > 0) {
            int n3 = Integer.numberOfTrailingZeros(n);
            n &= ~(1 << n3);
            WorldCoord worldCoord = new WorldCoord(tileEntity);
            if (n3 < 3) {
                worldCoord.step(n3 * 2);
            } else {
                int n4 = dirmap[n3 - 3];
                worldCoord.indStep(n4 >> 2, n4 & 3);
            }
            IBluePowerConnectable iBluePowerConnectable = (IBluePowerConnectable) CoreLib.getTileEntity((IBlockAccess) world, worldCoord, IBluePowerConnectable.class);
            if (iBluePowerConnectable == null) {
                ++n2;
                continue;
            }
            BluePowerConductor bluePowerConductor = iBluePowerConnectable.getBlueConductor();
            double d = this.getResistance() + bluePowerConductor.getResistance();
            double d2 = this.currents[n2];
            double d3 = this.Vcap - bluePowerConductor.getVoltage();
            double[] arrd = this.currents;
            int n5 = n2++;
            arrd[n5] = arrd[n5] + (d3 - d2 * d) * indscale;
            this.applyCurrent(-(d2 += d3 * 0.5));
            bluePowerConductor.applyCurrent(d2);
        }
    }

    public void readFromNBT(NBTTagCompound nBTTagCompound) {
        this.imask = nBTTagCompound.getInt("bpim");
        int n = Integer.bitCount(this.imask);
        this.currents = new double[n];
        NBTTagList nBTTagList = nBTTagCompound.getList("bpil");
        if (nBTTagList.size() != n) {
            return;
        }
        for (int i = 0; i < n; ++i) {
            NBTTagDouble nBTTagDouble = (NBTTagDouble) nBTTagList.get(i);
            this.currents[i] = nBTTagDouble.data;
        }
        this.Vcap = nBTTagCompound.getDouble("vcap");
        this.Icap = nBTTagCompound.getDouble("icap");
        this.Veff = nBTTagCompound.getDouble("veff");
        this.It1 = nBTTagCompound.getDouble("it1");
        this.Itot = nBTTagCompound.getDouble("itot");
        this.lastTick = nBTTagCompound.getInt("ltk");
    }

    public void writeToNBT(NBTTagCompound nBTTagCompound) {
        nBTTagCompound.setInt("bpim", this.imask);
        int n = Integer.bitCount(this.imask);
        NBTTagList nBTTagList = new NBTTagList();
        for (int i = 0; i < n; ++i) {
            NBTTagDouble nBTTagDouble = new NBTTagDouble(null, this.currents[i]);
            nBTTagList.add((NBTBase) nBTTagDouble);
        }
        nBTTagCompound.set("bpil", (NBTBase) nBTTagList);
        nBTTagCompound.setDouble("vcap", this.Vcap);
        nBTTagCompound.setDouble("icap", this.Icap);
        nBTTagCompound.setDouble("veff", this.Veff);
        nBTTagCompound.setDouble("it1", this.It1);
        nBTTagCompound.setDouble("itot", this.Itot);
        nBTTagCompound.setInt("ltk", this.lastTick);
    }
}
