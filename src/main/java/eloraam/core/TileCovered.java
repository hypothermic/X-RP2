/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.CoverLib;
import eloraam.core.IFrameSupport;
import eloraam.core.IHandlePackets;
import eloraam.core.Packet211TileDesc;
import eloraam.core.TileCoverable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import net.minecraft.server.Block;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.Packet;
import net.minecraft.server.World;

public class TileCovered extends TileCoverable implements IHandlePackets, IFrameSupport {

    public int CoverSides = 0;
    public short[] Covers = new short[29];

    public void replaceWithCovers() {
	CoverLib.replaceWithCovers(this.world, this.x, this.y, this.z, this.CoverSides, this.Covers);
    }

    public boolean canUpdate() {
	return false;
    }

    @Override
    public int getExtendedID() {
	return 0;
    }

    @Override
    public void onBlockNeighborChange(int n) {
	if (this.CoverSides == 0) {
	    this.deleteBlock();
	}
    }

    @Override
    public int getBlockID() {
	return CoverLib.blockCoverPlate.id;
    }

    @Override
    public boolean canAddCover(int n, int n2) {
	if ((this.CoverSides & 1 << n) > 0) {
	    return false;
	}
	short[] arrs = Arrays.copyOf(this.Covers, 29);
	arrs[n] = (short) n2;
	return CoverLib.checkPlacement(this.CoverSides | 1 << n, arrs, 0, false);
    }

    @Override
    public boolean tryAddCover(int n, int n2) {
	if (!this.canAddCover(n, n2)) {
	    return false;
	}
	this.CoverSides |= 1 << n;
	this.Covers[n] = (short) n2;
	this.updateBlockChange();
	return true;
    }

    @Override
    public int tryRemoveCover(int n) {
	if ((this.CoverSides & 1 << n) == 0) {
	    return -1;
	}
	this.CoverSides &= ~(1 << n);
	short s = this.Covers[n];
	this.Covers[n] = 0;
	this.updateBlockChange();
	return s;
    }

    @Override
    public int getCover(int n) {
	if ((this.CoverSides & 1 << n) == 0) {
	    return -1;
	}
	return this.Covers[n];
    }

    @Override
    public int getCoverMask() {
	return this.CoverSides;
    }

    @Override
    public boolean blockEmpty() {
	return this.CoverSides == 0;
    }

    @Override
    public byte[] getFramePacket() {
	Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
	packet211TileDesc.subId = 5;
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
	int n = nBTTagCompound.getInt("cvm") & 536870911;
	this.CoverSides |= n;
	byte[] arrby = nBTTagCompound.getByteArray("cvs");
	if (arrby != null && n > 0) {
	    int n2 = 0;
	    for (int i = 0; i < 29; ++i) {
		if ((n & 1 << i) == 0)
		    continue;
		this.Covers[i] = (short) ((arrby[n2] & 255) + ((arrby[n2 + 1] & 255) << 8));
		n2 += 2;
	    }
	}
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
	super.b(nBTTagCompound);
	nBTTagCompound.setInt("cvm", this.CoverSides);
	byte[] arrby = new byte[Integer.bitCount(this.CoverSides) * 2];
	int n = 0;
	for (int i = 0; i < 29; ++i) {
	    if ((this.CoverSides & 1 << i) == 0)
		continue;
	    arrby[n] = (byte) (this.Covers[i] & 255);
	    arrby[n + 1] = (byte) (this.Covers[i] >> 8);
	    n += 2;
	}
	nBTTagCompound.setByteArray("cvs", arrby);
    }

    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
	if (packet211TileDesc.subId != 5) {
	    return;
	}
	this.CoverSides = (int) packet211TileDesc.getUVLC();
	for (int i = 0; i < 29; ++i) {
	    if ((this.CoverSides & 1 << i) <= 0)
		continue;
	    this.Covers[i] = (short) packet211TileDesc.getUVLC();
	}
    }

    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
	packet211TileDesc.addUVLC(this.CoverSides);
	for (int i = 0; i < 29; ++i) {
	    if ((this.CoverSides & 1 << i) <= 0)
		continue;
	    packet211TileDesc.addUVLC(this.Covers[i]);
	}
    }

    public Packet d() {
	Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
	packet211TileDesc.subId = 5;
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
	    this.readFromPacket(packet211TileDesc);
	} catch (IOException iOException) {
	    // empty catch block
	}
	this.world.notify(this.x, this.y, this.z);
    }
}
