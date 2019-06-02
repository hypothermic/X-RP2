/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.NBTTagCompound;

public abstract class BluePowerEndpoint extends BluePowerConductor {

    public int Charge = 0;
    public int Flow = 0;

    @Override
    public double getInvCap() {
        return 0.25;
    }

    @Override
    public int getChargeScaled(int n) {
        return Math.min(n, n * this.Charge / 1000);
    }

    @Override
    public int getFlowScaled(int n) {
        return Integer.bitCount(this.Flow) * n / 32;
    }

    @Override
    public void iterate() {
        super.iterate();
        this.Charge = (int) (this.getVoltage() * 10.0);
        this.Flow = this.Flow << 1 | (this.Charge < 600 ? 0 : 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound nBTTagCompound) {
        super.readFromNBT(nBTTagCompound);
        this.Charge = nBTTagCompound.getShort("chg");
        this.Flow = nBTTagCompound.getInt("flw");
    }

    @Override
    public void writeToNBT(NBTTagCompound nBTTagCompound) {
        super.writeToNBT(nBTTagCompound);
        nBTTagCompound.setShort("chg", (short) this.Charge);
        nBTTagCompound.setInt("flw", this.Flow);
    }
}
