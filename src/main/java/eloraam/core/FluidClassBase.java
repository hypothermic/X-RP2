/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.FluidClass;
import eloraam.core.WorldCoord;
import net.minecraft.server.World;

public class FluidClassBase extends FluidClass {

    int fluidID;
    int idStill;
    int idMoving;
    String texFile;
    int texture;

    public FluidClassBase(int n, int n2, int n3, String string, int n4) {
	this.idStill = n2;
	this.idMoving = n3;
	this.texture = n4;
	this.texFile = string;
	this.fluidID = n;
    }

    @Override
    public int getFluidId(World world, WorldCoord worldCoord) {
	int n = world.getTypeId(worldCoord.x, worldCoord.y, worldCoord.z);
	if (n == this.idStill || n == this.idMoving) {
	    return this.fluidID;
	}
	return 0;
    }

    @Override
    public int getFluidLevel(World world, WorldCoord worldCoord) {
	int n = world.getData(worldCoord.x, worldCoord.y, worldCoord.z);
	return n != 0 ? 0 : 16;
    }

    @Override
    public boolean setFluidLevel(World world, WorldCoord worldCoord, int n) {
	if (n == 16) {
	    world.setTypeId(worldCoord.x, worldCoord.y, worldCoord.z, this.idMoving);
	    return true;
	}
	return false;
    }

    @Override
    public int getFluidQuanta() {
	return 16;
    }

    @Override
    public String getTextureFile() {
	return this.texFile;
    }

    @Override
    public int getTexture() {
	return this.texture;
    }
}
