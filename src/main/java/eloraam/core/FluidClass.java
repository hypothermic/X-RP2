/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.WorldCoord;
import net.minecraft.server.World;

public abstract class FluidClass {

    public abstract int getFluidId(World var1, WorldCoord var2);

    public abstract int getFluidLevel(World var1, WorldCoord var2);

    public abstract boolean setFluidLevel(World var1, WorldCoord var2, int var3);

    public abstract int getFluidQuanta();

    public abstract String getTextureFile();

    public abstract int getTexture();
}
