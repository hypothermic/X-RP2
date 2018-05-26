/* X-RP - decompiled with CFR */
package eloraam.control;

import eloraam.control.TileBackplane;
import eloraam.core.BlockExtended;
import eloraam.core.BlockMultipart;
import eloraam.core.CoreLib;
import java.util.ArrayList;
import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.RedPowerControl;
import net.minecraft.server.World;

public class TileRAM extends TileBackplane {

    public byte[] memory = new byte[8192];

    @Override
    public int readBackplane(int n) {
	return this.memory[n] & 255;
    }

    @Override
    public void writeBackplane(int n, int n2) {
	this.memory[n] = (byte) n2;
    }

    @Override
    public int getBlockID() {
	return RedPowerControl.blockBackplane.id;
    }

    @Override
    public int getExtendedID() {
	return 1;
    }

    @Override
    public void addHarvestContents(ArrayList arrayList) {
	super.addHarvestContents(arrayList);
	arrayList.add(new ItemStack((Block) RedPowerControl.blockBackplane, 1, 1));
    }

    @Override
    public void onHarvestPart(EntityHuman entityHuman, int n) {
	CoreLib.dropItem(this.world, this.x, this.y, this.z, new ItemStack((Block) RedPowerControl.blockBackplane, 1, 1));
	BlockMultipart.removeMultipart(this.world, this.x, this.y, this.z);
	this.world.setRawTypeId(this.x, this.y, this.z, RedPowerControl.blockBackplane.id);
	TileBackplane tileBackplane = (TileBackplane) CoreLib.getTileEntity((IBlockAccess) this.world, this.x, this.y, this.z, TileBackplane.class);
	if (tileBackplane != null) {
	    tileBackplane.Rotation = this.Rotation;
	}
	this.updateBlockChange();
    }

    @Override
    public void setPartBounds(BlockMultipart blockMultipart, int n) {
	if (n == 0) {
	    super.setPartBounds(blockMultipart, n);
	} else {
	    blockMultipart.a(0.0f, 0.125f, 0.0f, 1.0f, 1.0f, 1.0f);
	}
    }

    @Override
    public int getSolidPartsMask() {
	return 3;
    }

    @Override
    public int getPartsMask() {
	return 3;
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
	super.a(nBTTagCompound);
	this.memory = nBTTagCompound.getByteArray("ram");
	if (this.memory.length != 8192) {
	    this.memory = new byte[8192];
	}
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
	super.b(nBTTagCompound);
	nBTTagCompound.setByteArray("ram", this.memory);
    }
}
