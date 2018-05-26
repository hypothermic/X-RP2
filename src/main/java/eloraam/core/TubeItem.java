/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.Packet211TileDesc;
import java.io.IOException;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;

public class TubeItem {

    public short progress = 0;
    public byte mode = 1;
    public byte side;
    public byte color = 0;
    public short power = 0;
    public boolean scheduled = false;
    public ItemStack item;

    public TubeItem() {
    }

    public TubeItem(int n, ItemStack itemStack) {
	this.item = itemStack;
	this.side = (byte) n;
    }

    public void readFromNBT(NBTTagCompound nBTTagCompound) {
	this.item = ItemStack.a((NBTTagCompound) nBTTagCompound);
	this.side = nBTTagCompound.getByte("side");
	this.progress = nBTTagCompound.getShort("pos");
	this.mode = nBTTagCompound.getByte("mode");
	this.color = nBTTagCompound.getByte("col");
	if (this.progress < 0) {
	    this.scheduled = true;
	    this.progress = (short) (-this.progress - 1);
	}
	this.power = (short) (nBTTagCompound.getByte("pow") & 255);
    }

    public void writeToNBT(NBTTagCompound nBTTagCompound) {
	this.item.save(nBTTagCompound);
	nBTTagCompound.setByte("side", this.side);
	nBTTagCompound.setShort("pos", (short) (this.scheduled ? -this.progress - 1 : this.progress));
	nBTTagCompound.setByte("mode", this.mode);
	nBTTagCompound.setByte("col", this.color);
	nBTTagCompound.setByte("pow", (byte) this.power);
    }

    public static TubeItem newFromNBT(NBTTagCompound nBTTagCompound) {
	TubeItem tubeItem = new TubeItem();
	tubeItem.readFromNBT(nBTTagCompound);
	return tubeItem;
    }

    public void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
	this.side = (byte) packet211TileDesc.getByte();
	this.progress = (short) packet211TileDesc.getVLC();
	if (this.progress < 0) {
	    this.scheduled = true;
	    this.progress = (short) (-this.progress - 1);
	}
	this.color = (byte) packet211TileDesc.getByte();
	this.power = (byte) packet211TileDesc.getByte();
	int n = packet211TileDesc.getByte();
	int n2 = (int) packet211TileDesc.getUVLC();
	int n3 = (int) packet211TileDesc.getUVLC();
	this.item = new ItemStack(Item.byId[n3], n, n2);
    }

    public void writeToPacket(Packet211TileDesc packet211TileDesc) {
	packet211TileDesc.addByte(this.side);
	int n = this.scheduled ? -this.progress - 1 : this.progress;
	packet211TileDesc.addVLC(this.scheduled ? (long) (-this.progress - 1) : (long) this.progress);
	packet211TileDesc.addByte(this.color);
	packet211TileDesc.addByte(this.power);
	packet211TileDesc.addByte(this.item.count);
	packet211TileDesc.addUVLC(this.item.getData());
	packet211TileDesc.addUVLC(this.item.id);
    }

    public static TubeItem newFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
	TubeItem tubeItem = new TubeItem();
	tubeItem.readFromPacket(packet211TileDesc);
	return tubeItem;
    }
}
