/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.FluidBuffer;

public interface IPipeConnectable {

    public int getPipeConnectableSides();

    public int getPipeFlangeSides();

    public int getPipePressure(int var1);

    public FluidBuffer getPipeBuffer(int var1);
}
