/* X-RP - decompiled with CFR */
package eloraam.control;

import eloraam.core.*;
import net.minecraft.server.*;

import java.io.IOException;
import java.util.ArrayList;

public class TileBackplane extends TileMultipart implements IHandlePackets, IFrameSupport {

    public int Rotation = 0;

    public int readBackplane(int n) {
        return 255;
    }

    public void writeBackplane(int n, int n2) {
    }

    @Override
    public int getBlockID() {
        return RedPowerControl.blockBackplane.id;
    }

    @Override
    public int getExtendedID() {
        return 0;
    }

    @Override
    public void onBlockNeighborChange(int n) {
        if (!this.world.isBlockSolidOnSide(this.x, this.y - 1, this.z, 0)) {
            this.breakBlock();
            return;
        }
        WorldCoord worldCoord = new WorldCoord(this);
        worldCoord.step(CoreLib.rotToSide(this.Rotation) ^ 1);
        int n2 = this.world.getTypeId(worldCoord.x, worldCoord.y, worldCoord.z);
        int n3 = this.world.getData(worldCoord.x, worldCoord.y, worldCoord.z);
        if (n2 == RedPowerControl.blockBackplane.id) {
            return;
        }
        if (n2 == RedPowerControl.blockPeripheral.id && n3 == 1) {
            return;
        }
        this.breakBlock();
    }

    @Override
    public void addHarvestContents(ArrayList arrayList) {
        super.addHarvestContents(arrayList);
        arrayList.add(new ItemStack((Block) RedPowerControl.blockBackplane, 1, 0));
    }

    @Override
    public void onHarvestPart(EntityHuman entityHuman, int n) {
        this.breakBlock();
    }

    @Override
    public float getPartStrength(EntityHuman entityHuman, int n) {
        return 0.1f;
    }

    @Override
    public boolean blockEmpty() {
        return false;
    }

    @Override
    public void setPartBounds(BlockMultipart blockMultipart, int n) {
        blockMultipart.a(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
    }

    @Override
    public int getSolidPartsMask() {
        return 1;
    }

    @Override
    public int getPartsMask() {
        return 1;
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
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        nBTTagCompound.setByte("rot", (byte) this.Rotation);
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
