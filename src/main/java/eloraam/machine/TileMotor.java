/* X-RP - decompiled with CFR */
package eloraam.machine;

import eloraam.core.BluePowerConductor;
import eloraam.core.BluePowerEndpoint;
import eloraam.core.CoreLib;
import eloraam.core.CoreProxy;
import eloraam.core.FrameLib;
import eloraam.core.IBluePowerConnectable;
import eloraam.core.IFrameLink;
import eloraam.core.IFrameSupport;
import eloraam.core.IHandlePackets;
import eloraam.core.IRotatable;
import eloraam.core.Packet211TileDesc;
import eloraam.core.RedPowerLib;
import eloraam.core.TileExtended;
import eloraam.core.WorldCoord;
import eloraam.machine.BlockFrame;
import eloraam.machine.BlockMachine;
import eloraam.machine.TileFrameMoving;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.Packet;
import net.minecraft.server.RedPowerMachine;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public class TileMotor extends TileExtended implements IHandlePackets, IBluePowerConnectable, IRotatable, IFrameLink, IFrameSupport {

    BluePowerEndpoint cond;
    public int Rotation;
    public int MoveDir;
    public int MovePos;
    public boolean Powered;
    public boolean Active;
    public boolean Charged;
    public int LinkSize;
    public int ConMask;

    public TileMotor() {
	this.cond = new BluePowerEndpoint() {

	    @Override
	    public TileEntity getParent() {
		return TileMotor.this;
	    }
	};
	this.Rotation = 0;
	this.MoveDir = 4;
	this.MovePos = -1;
	this.Powered = false;
	this.Active = false;
	this.Charged = false;
	this.LinkSize = -1;
	this.ConMask = -1;
    }

    @Override
    public int getConnectableMask() {
	return 1073741823;
    }

    @Override
    public int getConnectClass(int n) {
	return 65;
    }

    @Override
    public int getCornerPowerMode() {
	return 0;
    }

    @Override
    public WorldCoord getFrameLinkset() {
	return null;
    }

    @Override
    public BluePowerConductor getBlueConductor() {
	return this.cond;
    }

    @Override
    public int getPartMaxRotation(int n, boolean bl) {
	if (this.MovePos >= 0) {
	    return 0;
	}
	return !bl ? 3 : 5;
    }

    @Override
    public int getPartRotation(int n, boolean bl) {
	if (bl) {
	    return this.Rotation >> 2;
	}
	return this.Rotation & 3;
    }

    @Override
    public void setPartRotation(int n, boolean bl, int n2) {
	if (this.MovePos >= 0) {
	    return;
	}
	this.Rotation = bl ? this.Rotation & 3 | n2 << 2 : this.Rotation & -4 | n2 & 3;
	this.updateBlockChange();
    }

    @Override
    public boolean isFrameMoving() {
	return false;
    }

    @Override
    public boolean canFrameConnectIn(int n) {
	return n != (this.Rotation >> 2 ^ 1);
    }

    @Override
    public boolean canFrameConnectOut(int n) {
	return n == (this.Rotation >> 2 ^ 1);
    }

    @Override
    public int getExtendedID() {
	return 7;
    }

    @Override
    public int getBlockID() {
	return RedPowerMachine.blockMachine.id;
    }

    @Override
    public void q_() {
	super.q_();
	if (this.MovePos >= 0 && this.MovePos < 16) {
	    ++this.MovePos;
	    this.dirtyBlock();
	}
	if (CoreProxy.isClient(this.world)) {
	    return;
	}
	if (this.MovePos >= 0) {
	    this.cond.drawPower(100 + 10 * this.LinkSize);
	}
	if (this.MovePos >= 16) {
	    this.dropFrame(true);
	    this.MovePos = -1;
	    this.Active = false;
	    this.updateBlock();
	}
	if (this.ConMask < 0) {
	    this.ConMask = RedPowerLib.getConnections((IBlockAccess) this.world, this, this.x, this.y, this.z);
	    this.cond.recache(this.ConMask, 0);
	}
	this.cond.iterate();
	this.dirtyBlock();
	if (this.MovePos >= 0) {
	    return;
	}
	if (this.cond.getVoltage() < 60.0) {
	    if (this.Charged && this.cond.Flow == 0) {
		this.Charged = false;
		this.updateBlock();
	    }
	    return;
	}
	if (!this.Charged) {
	    this.Charged = true;
	    this.updateBlock();
	}
    }

    private int getDriveSide() {
	int n;
        switch (this.Rotation >> 2) {
            case 0: {
                n = 13604;
                break;
            }
            case 1: {
                n = 13349;
                break;
            }
            case 2: {
                n = 20800;
                break;
            }
            case 3: {
                n = 16720;
                break;
            }
            case 4: {
                n = 8496;
                break;
            }
            default: {
                n = 12576;
            }
        }
        n >>= (this.Rotation & 3) << 2;
        return n &= 7;
    }

    void pickFrame() {
	this.MoveDir = this.getDriveSide();
	WorldCoord worldCoord = new WorldCoord(this);
	FrameLib.FrameSolver frameSolver = new FrameLib.FrameSolver(this.world, worldCoord.coordStep(this.Rotation >> 2 ^ 1), worldCoord, this.MoveDir);
	if (!frameSolver.solveLimit(RedPowerMachine.FrameLinkSize)) {
	    return;
	}
	if (!frameSolver.addMoved()) {
	    return;
	}
	this.LinkSize = frameSolver.getFrameSet().size();
	this.MovePos = 0;
	this.Active = true;
	this.updateBlock();
	for (Object worldCoord2 : frameSolver.getClearSet()) {
	    this.world.setRawTypeId(((WorldCoord) worldCoord2).x, ((WorldCoord) worldCoord2).y, ((WorldCoord) worldCoord2).z, 0);
	}
	for (WorldCoord worldCoord3 : frameSolver.getFrameSet()) {
	    int n = this.world.getTypeId(worldCoord3.x, worldCoord3.y, worldCoord3.z);
	    int n2 = this.world.getData(worldCoord3.x, worldCoord3.y, worldCoord3.z);
	    TileEntity tileEntity = this.world.getTileEntity(worldCoord3.x, worldCoord3.y, worldCoord3.z);
	    if (tileEntity != null) {
		this.world.q(worldCoord3.x, worldCoord3.y, worldCoord3.z);
	    }
	    boolean bl = this.world.isStatic;
	    this.world.isStatic = true;
	    this.world.setRawTypeIdAndData(worldCoord3.x, worldCoord3.y, worldCoord3.z, RedPowerMachine.blockFrame.id, 1);
	    this.world.isStatic = bl;
	    TileFrameMoving tileFrameMoving = (TileFrameMoving) CoreLib.getTileEntity((IBlockAccess) this.world, worldCoord3, TileFrameMoving.class);
	    if (tileFrameMoving == null)
		continue;
	    tileFrameMoving.setContents(n, n2, this.x, this.y, this.z, tileEntity);
	}
	for (WorldCoord worldCoord3 : frameSolver.getFrameSet()) {
	    this.world.notify(worldCoord3.x, worldCoord3.y, worldCoord3.z);
	    CoreLib.markBlockDirty(this.world, worldCoord3.x, worldCoord3.y, worldCoord3.z);
	    TileFrameMoving tileFrameMoving = (TileFrameMoving) CoreLib.getTileEntity((IBlockAccess) this.world, worldCoord3, TileFrameMoving.class);
	    if (tileFrameMoving == null || !(tileFrameMoving.movingTileEntity instanceof IFrameSupport))
		continue;
	    IFrameSupport iFrameSupport = (IFrameSupport) tileFrameMoving.movingTileEntity;
	    iFrameSupport.onFramePickup(tileFrameMoving.getFrameBlockAccess());
	}
    }

    void dropFrame(boolean bl) {
	WorldCoord object;
	WorldCoord worldCoord = new WorldCoord(this);
	FrameLib.FrameSolver frameSolver = new FrameLib.FrameSolver(this.world, worldCoord.coordStep(this.Rotation >> 2 ^ 1), worldCoord, -1);
	if (!frameSolver.solve()) {
	    return;
	}
	this.LinkSize = 0;
	frameSolver.sort(this.MoveDir);
	for (WorldCoord worldCoord2 : frameSolver.getFrameSet()) {
	    TileFrameMoving tileFrameMoving = (TileFrameMoving) CoreLib.getTileEntity((IBlockAccess) this.world, worldCoord2, TileFrameMoving.class);
	    if (tileFrameMoving == null)
		continue;
	    tileFrameMoving.pushEntities(this);
	    object = worldCoord2.copy();
	    if (bl) {
		((WorldCoord) object).step(this.MoveDir);
	    }
	    if (tileFrameMoving.movingBlockID != 0) {
		boolean bl2 = this.world.isStatic;
		this.world.isStatic = true;
		this.world.setRawTypeIdAndData(object.x, object.y, object.z, tileFrameMoving.movingBlockID, tileFrameMoving.movingBlockMeta);
		this.world.isStatic = bl2;
		if (tileFrameMoving.movingTileEntity != null) {
		    tileFrameMoving.movingTileEntity.x = object.x;
		    tileFrameMoving.movingTileEntity.y = object.y;
		    tileFrameMoving.movingTileEntity.z = object.z;
		    tileFrameMoving.movingTileEntity.m();
		    this.world.setTileEntity(object.x, object.y, object.z, tileFrameMoving.movingTileEntity);
		}
	    }
	    if (!bl)
		continue;
	    this.world.setRawTypeId(worldCoord2.x, worldCoord2.y, worldCoord2.z, 0);
	}
	for (WorldCoord worldCoord2 : frameSolver.getFrameSet()) {
	    // X-RP: "object" -> "xobject" + decl
	    Object xobject = (IFrameSupport) CoreLib.getTileEntity((IBlockAccess) this.world, worldCoord2, IFrameSupport.class);
	    if (xobject != null) {
		((IFrameSupport) xobject).onFrameDrop();
	    }
	    this.world.notify(worldCoord2.x, worldCoord2.y, worldCoord2.z);
	    CoreLib.markBlockDirty(this.world, worldCoord2.x, worldCoord2.y, worldCoord2.z);
	    RedPowerLib.updateIndirectNeighbors(this.world, worldCoord2.x, worldCoord2.y, worldCoord2.z, this.world.getTypeId(worldCoord2.x, worldCoord2.y, worldCoord2.z));
	}
    }

    float getMoveScaled() {
	return (float) this.MovePos / 16.0f;
    }

    @Override
    public void onBlockRemoval() {
	if (this.MovePos >= 0) {
	    this.Active = false;
	    this.dropFrame(false);
	}
	this.MovePos = -1;
    }

    @Override
    public void onBlockNeighborChange(int n) {
	this.ConMask = -1;
	if (RedPowerLib.isPowered((IBlockAccess) this.world, this.x, this.y, this.z, 16777215, 63)) {
	    if (!this.Charged) {
		return;
	    }
	    if (this.Powered) {
		return;
	    }
	    if (this.MovePos >= 0) {
		return;
	    }
	} else {
	    if (!this.Powered) {
		return;
	    }
	    this.Powered = false;
	    this.updateBlockChange();
	    return;
	}
	this.Powered = true;
	this.updateBlockChange();
	if (this.Powered) {
	    this.pickFrame();
	}
    }

    public int getFacing(EntityLiving entityLiving) {
	int n = (int) Math.floor((double) (entityLiving.yaw * 4.0f / 360.0f) + 0.5) & 3;
	if (Math.abs(entityLiving.locX - (double) this.x) < 2.0 && Math.abs(entityLiving.locZ - (double) this.z) < 2.0) {
	    double d = entityLiving.locY + 1.82 - (double) entityLiving.height - (double) this.y;
	    if (d > 2.0) {
		return 0;
	    }
	    if (d < 0.0) {
		return 1;
	    }
	}
	switch (n) {
	case 0: {
	    return 3;
	}
	case 1: {
	    return 4;
	}
	case 2: {
	    return 2;
	}
	}
	return 5;
    }

    @Override
    public void onBlockPlacedBy(EntityLiving entityLiving) {
	this.Rotation = this.getFacing(entityLiving) << 2;
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
	this.MoveDir = nBTTagCompound.getByte("mdir");
	this.MovePos = nBTTagCompound.getByte("mpos");
	this.LinkSize = nBTTagCompound.getInt("links");
	this.cond.readFromNBT(nBTTagCompound);
	byte by = nBTTagCompound.getByte("ps");
	this.Powered = (by & 1) > 0;
	this.Active = (by & 2) > 0;
	this.Charged = (by & 4) > 0;
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
	super.b(nBTTagCompound);
	nBTTagCompound.setByte("rot", (byte) this.Rotation);
	nBTTagCompound.setByte("mdir", (byte) this.MoveDir);
	nBTTagCompound.setByte("mpos", (byte) this.MovePos);
	nBTTagCompound.setInt("links", this.LinkSize);
	this.cond.writeToNBT(nBTTagCompound);
	int n = (this.Powered ? 1 : 0) | (this.Active ? 2 : 0) | (this.Charged ? 4 : 0);
	nBTTagCompound.setByte("ps", (byte) n);
    }

    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
	this.Rotation = packet211TileDesc.getByte();
	this.MoveDir = packet211TileDesc.getByte();
	this.MovePos = packet211TileDesc.getByte() - 1;
	int n = packet211TileDesc.getByte();
	this.Powered = (n & 1) > 0;
	this.Active = (n & 2) > 0;
	this.Charged = (n & 4) > 0;
    }

    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
	packet211TileDesc.addByte(this.Rotation);
	packet211TileDesc.addByte(this.MoveDir);
	packet211TileDesc.addByte(this.MovePos + 1);
	int n = (this.Powered ? 1 : 0) | (this.Active ? 2 : 0) | (this.Charged ? 4 : 0);
	packet211TileDesc.addByte(n);
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
