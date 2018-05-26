/* X-RP - decompiled with CFR */
package eloraam.control;

import eloraam.base.ItemScrewdriver;
import eloraam.core.BlockExtended;
import eloraam.core.CoreProxy;
import eloraam.core.IFrameSupport;
import eloraam.core.IHandlePackets;
import eloraam.core.IRedbusConnectable;
import eloraam.core.Packet211TileDesc;
import eloraam.core.TileExtended;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import net.minecraft.server.BaseMod;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.Packet;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.RedPowerControl;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import net.minecraft.server.mod_RedPowerControl;

public class TileDisplay extends TileExtended implements IRedbusConnectable, IHandlePackets, IFrameSupport {

    public byte[] screen = new byte[4000];
    public int Rotation = 0;
    public int memRow = 0;
    public int cursX = 0;
    public int cursY = 0;
    public int cursMode = 2;
    public int kbstart = 0;
    public int kbpos = 0;
    public int blitXS = 0;
    public int blitYS = 0;
    public int blitXD = 0;
    public int blitYD = 0;
    public int blitW = 0;
    public int blitH = 0;
    public int blitMode = 0;
    public byte[] kbbuf = new byte[16];
    int rbaddr = 1;

    public TileDisplay() {
	Arrays.fill(this.screen, (byte) 32);
    }

    @Override
    public int rbGetAddr() {
	return this.rbaddr;
    }

    @Override
    public void rbSetAddr(int n) {
	this.rbaddr = n;
    }

    @Override
    public int rbRead(int n) {
	if (n >= 16 && n < 96) {
	    return this.screen[this.memRow * 80 + n - 16];
	}
	switch (n) {
	case 0: {
	    return this.memRow;
	}
	case 1: {
	    return this.cursX;
	}
	case 2: {
	    return this.cursY;
	}
	case 3: {
	    return this.cursMode;
	}
	case 4: {
	    return this.kbstart;
	}
	case 5: {
	    return this.kbpos;
	}
	case 6: {
	    return this.kbbuf[this.kbstart] & 255;
	}
	case 7: {
	    return this.blitMode;
	}
	case 8: {
	    return this.blitXS;
	}
	case 9: {
	    return this.blitYS;
	}
	case 10: {
	    return this.blitXD;
	}
	case 11: {
	    return this.blitYD;
	}
	case 12: {
	    return this.blitW;
	}
	case 13: {
	    return this.blitH;
	}
	}
	return 0;
    }

    @Override
    public void rbWrite(int n, int n2) {
	this.dirtyBlock();
	if (n >= 16 && n < 96) {
	    this.screen[this.memRow * 80 + n - 16] = (byte) n2;
	    return;
	}
	switch (n) {
	case 0: {
	    this.memRow = n2;
	    if (this.memRow > 49) {
		this.memRow = 49;
	    }
	    return;
	}
	case 1: {
	    this.cursX = n2;
	    return;
	}
	case 2: {
	    this.cursY = n2;
	    return;
	}
	case 3: {
	    this.cursMode = n2;
	    return;
	}
	case 4: {
	    this.kbstart = n2 & 15;
	    return;
	}
	case 5: {
	    this.kbpos = n2 & 15;
	    return;
	}
	case 6: {
	    this.kbbuf[this.kbstart] = (byte) n2;
	    return;
	}
	case 7: {
	    this.blitMode = n2;
	    return;
	}
	case 8: {
	    this.blitXS = n2;
	    return;
	}
	case 9: {
	    this.blitYS = n2;
	    return;
	}
	case 10: {
	    this.blitXD = n2;
	    return;
	}
	case 11: {
	    this.blitYD = n2;
	    return;
	}
	case 12: {
	    this.blitW = n2;
	    return;
	}
	case 13: {
	    this.blitH = n2;
	    return;
	}
	}
    }

    @Override
    public int getConnectableMask() {
	return 16777215;
    }

    @Override
    public int getConnectClass(int n) {
	return 66;
    }

    @Override
    public int getCornerPowerMode() {
	return 0;
    }

    @Override
    public void onBlockPlacedBy(EntityLiving entityLiving) {
	this.Rotation = (int) Math.floor((double) (entityLiving.yaw * 4.0f / 360.0f) + 0.5) + 1 & 3;
    }

    @Override
    public boolean onBlockActivated(EntityHuman entityHuman) {
	if (entityHuman.isSneaking()) {
	    if (CoreProxy.isClient(this.world)) {
		return false;
	    }
	    ItemStack itemStack = entityHuman.inventory.getItemInHand();
	    if (itemStack == null) {
		return false;
	    }
	    if (!(itemStack.getItem() instanceof ItemScrewdriver)) {
		return false;
	    }
	    entityHuman.openGui((BaseMod) mod_RedPowerControl.instance, 2, this.world, this.x, this.y, this.z);
	    return false;
	}
	if (CoreProxy.isClient(this.world)) {
	    return true;
	}
	entityHuman.openGui((BaseMod) mod_RedPowerControl.instance, 1, this.world, this.x, this.y, this.z);
	return true;
    }

    @Override
    public int getBlockID() {
	return RedPowerControl.blockPeripheral.id;
    }

    @Override
    public int getExtendedID() {
	return 0;
    }

    public boolean isUseableByPlayer(EntityHuman entityHuman) {
	if (this.world.getTileEntity(this.x, this.y, this.z) != this) {
	    return false;
	}
	return entityHuman.e((double) this.x + 0.5, (double) this.y + 0.5, (double) this.z + 0.5) <= 64.0;
    }

    public void pushKey(byte by) {
	int n = this.kbpos + 1 & 15;
	if (n == this.kbstart) {
	    return;
	}
	this.kbbuf[this.kbpos] = by;
	this.kbpos = n;
    }

    @Override
    public void q_() {
	this.runblitter();
    }

    private void runblitter() {
	if (this.blitMode == 0) {
	    return;
	}
	this.dirtyBlock();
	int n = this.blitW;
	int n2 = this.blitH;
	n = Math.min(n, 80 - this.blitXD);
	n2 = Math.min(n2, 50 - this.blitYD);
	if (n < 0 || n2 < 0) {
	    this.blitMode = 0;
	    return;
	}
	int n3 = this.blitYD * 80 + this.blitXD;
	switch (this.blitMode) {
	case 1: {
	    for (int i = 0; i < n2; ++i) {
		for (int j = 0; j < n; ++j) {
		    this.screen[n3 + 80 * i + j] = (byte) this.blitXS;
		}
	    }
	    this.blitMode = 0;
	    return;
	}
	case 2: {
	    for (int i = 0; i < n2; ++i) {
		for (int j = 0; j < n; ++j) {
		    byte[] arrby = this.screen;
		    int n4 = n3 + 80 * i + j;
		    arrby[n4] = (byte) (arrby[n4] ^ 128);
		}
	    }
	    this.blitMode = 0;
	    return;
	}
	}
	n = Math.min(n, 80 - this.blitXS);
	n2 = Math.min(n2, 50 - this.blitYS);
	if (n < 0 || n2 < 0) {
	    this.blitMode = 0;
	    return;
	}
	int n5 = this.blitYS * 80 + this.blitXS;
	switch (this.blitMode) {
	case 3: {
	    for (int i = 0; i < n2; ++i) {
		for (int j = 0; j < n; ++j) {
		    this.screen[n3 + 80 * i + j] = this.screen[n5 + 80 * i + j];
		}
	    }
	    this.blitMode = 0;
	    return;
	}
	}
    }

    @Override
    public byte[] getFramePacket() {
	Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
	packet211TileDesc.subId = 7;
	this.writeToPacket(packet211TileDesc);
	packet211TileDesc.headout.write(packet211TileDesc.subId);
	return packet211TileDesc.toByteArray();
    }

    @Override
    public void handleFramePacket(byte[] arrby) throws IOException {
	Packet211TileDesc packet211TileDesc = new Packet211TileDesc(arrby);
	packet211TileDesc.subId = packet211TileDesc.getByte();
	this.readFromPacket(packet211TileDesc);
    }

    @Override
    public void onFrameRefresh(IBlockAccess iBlockAccess) {
    }

    @Override
    public void onFramePickup(IBlockAccess iBlockAccess) {
    }

    @Override
    public void onFrameDrop() {
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
	super.a(nBTTagCompound);
	this.Rotation = nBTTagCompound.getByte("rot");
	this.screen = nBTTagCompound.getByteArray("fb");
	if (this.screen.length != 4000) {
	    this.screen = new byte[4000];
	}
	this.memRow = nBTTagCompound.getByte("row") & 255;
	this.cursX = nBTTagCompound.getByte("cx") & 255;
	this.cursY = nBTTagCompound.getByte("cy") & 255;
	this.cursMode = nBTTagCompound.getByte("cm") & 255;
	this.kbstart = nBTTagCompound.getByte("kbs");
	this.kbpos = nBTTagCompound.getByte("kbp");
	this.kbbuf = nBTTagCompound.getByteArray("kbb");
	if (this.kbbuf.length != 16) {
	    this.kbbuf = new byte[16];
	}
	this.blitXS = nBTTagCompound.getByte("blxs") & 255;
	this.blitYS = nBTTagCompound.getByte("blys") & 255;
	this.blitXD = nBTTagCompound.getByte("blxd") & 255;
	this.blitYD = nBTTagCompound.getByte("blyd") & 255;
	this.blitW = nBTTagCompound.getByte("blw") & 255;
	this.blitH = nBTTagCompound.getByte("blh") & 255;
	this.blitMode = nBTTagCompound.getByte("blmd");
	this.rbaddr = nBTTagCompound.getByte("rbaddr") & 255;
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
	super.b(nBTTagCompound);
	nBTTagCompound.setByte("rot", (byte) this.Rotation);
	nBTTagCompound.setByteArray("fb", this.screen);
	nBTTagCompound.setByte("row", (byte) this.memRow);
	nBTTagCompound.setByte("cx", (byte) this.cursX);
	nBTTagCompound.setByte("cy", (byte) this.cursY);
	nBTTagCompound.setByte("cm", (byte) this.cursMode);
	nBTTagCompound.setByte("kbs", (byte) this.kbstart);
	nBTTagCompound.setByte("kbp", (byte) this.kbpos);
	nBTTagCompound.setByteArray("kbb", this.kbbuf);
	nBTTagCompound.setByte("blxs", (byte) this.blitXS);
	nBTTagCompound.setByte("blys", (byte) this.blitYS);
	nBTTagCompound.setByte("blxd", (byte) this.blitXD);
	nBTTagCompound.setByte("blyd", (byte) this.blitYD);
	nBTTagCompound.setByte("blw", (byte) this.blitW);
	nBTTagCompound.setByte("blh", (byte) this.blitH);
	nBTTagCompound.setByte("blmd", (byte) this.blitMode);
	nBTTagCompound.setByte("rbaddr", (byte) this.rbaddr);
    }

    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
	this.Rotation = packet211TileDesc.getByte();
    }

    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
	packet211TileDesc.addByte(this.Rotation);
    }

    public Packet d() {
	Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
	packet211TileDesc.subId = 7;
	packet211TileDesc.xCoord = this.x;
	packet211TileDesc.yCoord = this.y;
	packet211TileDesc.zCoord = this.z;
	this.writeToPacket(packet211TileDesc);
	packet211TileDesc.encode();
	return packet211TileDesc;
    }

    @Override
    public void handlePacket(Packet211TileDesc packet211TileDesc) {
	try {
	    if (packet211TileDesc.subId != 7) {
		return;
	    }
	    this.readFromPacket(packet211TileDesc);
	} catch (IOException iOException) {
	    // empty catch block
	}
	this.world.notify(this.x, this.y, this.z);
    }
}
