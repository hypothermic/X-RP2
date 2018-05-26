/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.Packet211TileDesc;
import java.io.IOException;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public abstract class FluidBuffer {

    public int Type = 0;
    public int Level = 0;
    public int Delta = 0;
    private int lastTick = 0;

    public abstract TileEntity getParent();

    public abstract void onChange();

    public int getMaxLevel() {
	return 16;
    }

    public int getLevel() {
	long l = this.getParent().world.getTime();
	if ((l & 65535) == (long) this.lastTick) {
	    return this.Level;
	}
	this.lastTick = (int) (l & 65535);
	this.Level += this.Delta;
	this.Delta = 0;
	if (this.Level == 0) {
	    this.Type = 0;
	}
	return this.Level;
    }

    public void addLevel(int n, int n2) {
	this.Type = n;
	this.Delta += n2;
	this.onChange();
    }

    public void readFromNBT(NBTTagCompound nBTTagCompound, String string) {
	NBTTagCompound nBTTagCompound2 = nBTTagCompound.getCompound(string);
	this.Type = nBTTagCompound2.getShort("type");
	this.Level = nBTTagCompound2.getShort("lvl");
	this.Delta = nBTTagCompound2.getShort("del");
	this.lastTick = nBTTagCompound2.getInt("ltk");
    }

    public void writeToNBT(NBTTagCompound nBTTagCompound, String string) {
	NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
	nBTTagCompound2.setShort("type", (short) this.Type);
	nBTTagCompound2.setShort("lvl", (short) this.Level);
	nBTTagCompound2.setShort("del", (short) this.Delta);
	nBTTagCompound2.setShort("lck", (short) this.lastTick);
	nBTTagCompound.setCompound(string, nBTTagCompound2);
    }

    public void writeToPacket(Packet211TileDesc packet211TileDesc) {
	packet211TileDesc.addUVLC(this.Type);
	packet211TileDesc.addUVLC(this.Level);
    }

    public void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
	this.Type = (int) packet211TileDesc.getUVLC();
	this.Level = (int) packet211TileDesc.getUVLC();
    }
}
