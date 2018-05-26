/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.Block
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.EntityLiving
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.Packet
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.BlockMultipart;
import eloraam.core.IFrameSupport;
import eloraam.core.IHandlePackets;
import eloraam.core.IRotatable;
import eloraam.core.Packet211TileDesc;
import eloraam.core.TileMultipart;
import eloraam.machine.BlockMachinePanel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.Packet;
import net.minecraft.server.RedPowerMachine;
import net.minecraft.server.World;

public class TileMachinePanel
extends TileMultipart
implements IHandlePackets,
IRotatable,
IFrameSupport {
    public int Rotation = 0;
    public boolean Active = false;
    public boolean Powered = false;
    public boolean Delay = false;
    public boolean Charged = false;

    public int getLightValue() {
        return 0;
    }

    void updateLight() {
        this.world.v(this.x, this.y, this.z);
    }

    public int getFacing(EntityLiving entityLiving) {
        int n = (int)Math.floor((double)(entityLiving.yaw * 4.0f / 360.0f) + 0.5) & 3;
        if (Math.abs(entityLiving.locX - (double)this.x) < 2.0 && Math.abs(entityLiving.locZ - (double)this.z) < 2.0) {
            double d = entityLiving.locY + 1.82 - (double)entityLiving.height - (double)this.y;
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
        this.Rotation = (int)Math.floor((double)(entityLiving.yaw * 4.0f / 360.0f) + 0.5) & 3;
    }

    @Override
    public int getBlockID() {
        return RedPowerMachine.blockMachinePanel.id;
    }

    @Override
    public void addHarvestContents(ArrayList arrayList) {
        arrayList.add(new ItemStack(this.getBlockID(), 1, this.getExtendedID()));
    }

    @Override
    public void onHarvestPart(EntityHuman entityHuman, int n) {
        this.breakBlock();
    }

    @Override
    public float getPartStrength(EntityHuman entityHuman, int n) {
        BlockMachinePanel blockMachinePanel = RedPowerMachine.blockMachinePanel;
        return entityHuman.getCurrentPlayerStrVsBlock((Block)blockMachinePanel, 0) / (blockMachinePanel.m() * 30.0f);
    }

    @Override
    public boolean blockEmpty() {
        return false;
    }

    @Override
    public void setPartBounds(BlockMultipart blockMultipart, int n) {
        blockMultipart.a(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
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
    public int getPartMaxRotation(int n, boolean bl) {
        return !bl ? 3 : 0;
    }

    @Override
    public int getPartRotation(int n, boolean bl) {
        if (bl) {
            return 0;
        }
        return this.Rotation;
    }

    @Override
    public void setPartRotation(int n, boolean bl, int n2) {
        if (bl) {
            return;
        }
        this.Rotation = n2;
        this.updateBlockChange();
    }

    @Override
    public byte[] getFramePacket() {
        Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
        packet211TileDesc.subId = 8;
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
        this.Active = (by & 1) > 0;
        this.Powered = (by & 2) > 0;
        this.Delay = (by & 4) > 0;
        this.Charged = (by & 8) > 0;
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        int n = (this.Active ? 1 : 0) | (this.Powered ? 2 : 0) | (this.Delay ? 4 : 0) | (this.Charged ? 8 : 0);
        nBTTagCompound.setByte("ps", (byte)n);
        nBTTagCompound.setByte("rot", (byte)this.Rotation);
    }

    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        this.Rotation = packet211TileDesc.getByte();
        int n = packet211TileDesc.getByte();
        this.Active = (n & 1) > 0;
        this.Powered = (n & 2) > 0;
        this.Delay = (n & 4) > 0;
        this.Charged = (n & 8) > 0;
    }

    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        packet211TileDesc.addByte(this.Rotation);
        int n = (this.Active ? 1 : 0) | (this.Powered ? 2 : 0) | (this.Delay ? 4 : 0) | (this.Charged ? 8 : 0);
        packet211TileDesc.addByte(n);
    }

    public Packet d() {
        Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
        packet211TileDesc.subId = 8;
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
            if (packet211TileDesc.subId != 8) {
                return;
            }
            this.readFromPacket(packet211TileDesc);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.world.notify(this.x, this.y, this.z);
    }
}

