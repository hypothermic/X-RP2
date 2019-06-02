/* X-RP - decompiled with CFR */
package eloraam.core;

public interface IRedPowerWiring extends IRedPowerConnectable, IWiring {

    public int scanPoweringStrength(int var1, int var2);

    public int getCurrentStrength(int var1, int var2);

    public void updateCurrentStrength();
}
