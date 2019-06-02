/* X-RP - decompiled with CFR */
package eloraam.base;

import eloraam.core.IFrameSupport;
import eloraam.core.IHandlePackets;
import eloraam.core.Packet211TileDesc;
import eloraam.core.TileExtended;
import net.minecraft.server.*;

import java.io.IOException;

public class TileAppliance extends TileExtended implements IHandlePackets, IFrameSupport {

    public int Rotation = 0;
    public boolean Active = false;

    @Override
    public void onBlockPlacedBy(EntityLiving entityLiving) {
        this.Rotation = (int) Math.floor((double) (entityLiving.yaw * 4.0f / 360.0f) + 0.5) & 3;
    }

    @Override
    public int getBlockID() {
        return RedPowerBase.blockAppliance.id;
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
        byte by = nBTTagCompound.getByte("ps");
        this.Rotation = nBTTagCompound.getByte("rot");
        this.Active = by > 0;
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        nBTTagCompound.setByte("ps", (byte) (this.Active ? 1 : 0)); // X-RP: point of failure
        nBTTagCompound.setByte("rot", (byte) this.Rotation);
    }

    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        this.Rotation = packet211TileDesc.getByte();
        int n = packet211TileDesc.getByte();
        this.Active = n > 0;
    }

    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        packet211TileDesc.addByte(this.Rotation);
        int n = this.Active ? 1 : 0;
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
