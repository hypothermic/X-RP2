/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Block
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.Packet
 *  net.minecraft.server.World
 */
package eloraam.logic;

import eloraam.core.*;
import net.minecraft.server.*;

import java.io.IOException;
import java.util.ArrayList;

public class TileLogic
        extends TileCoverable
        implements IHandlePackets,
        IRedPowerConnectable,
        IRotatable,
        IFrameSupport {
    public int SubId = 0;
    public int Rotation = 0;
    public boolean Powered = false;
    public boolean Disabled = false;
    public boolean Active = false;
    public int PowerState = 0;
    public int Deadmap = 0;
    public int Cover = 255;

    @Override
    public int getPartMaxRotation(int n, boolean bl) {
        if (bl) {
            return 0;
        }
        return n == this.Rotation >> 2 ? 3 : 0;
    }

    @Override
    public int getPartRotation(int n, boolean bl) {
        if (bl) {
            return 0;
        }
        if (n != this.Rotation >> 2) {
            return 0;
        }
        return this.Rotation & 3;
    }

    @Override
    public void setPartRotation(int n, boolean bl, int n2) {
        if (bl) {
            return;
        }
        if (n != this.Rotation >> 2) {
            return;
        }
        this.Rotation = n2 & 3 | this.Rotation & -4;
        this.updateBlockChange();
    }

    @Override
    public int getConnectableMask() {
        return 15 << (this.Rotation & -4);
    }

    @Override
    public int getConnectClass(int n) {
        return 0;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public int getPoweringMask(int n) {
        if (n != 0) {
            return 0;
        }
        if (this.Powered) {
            return RedPowerLib.mapRotToCon(8, this.Rotation);
        }
        return 0;
    }

    @Override
    public boolean canAddCover(int n, int n2) {
        if (this.Cover != 255) {
            return false;
        }
        if ((n ^ 1) != this.Rotation >> 2) {
            return false;
        }
        return n2 <= 254;
    }

    @Override
    public boolean tryAddCover(int n, int n2) {
        if (!this.canAddCover(n, n2)) {
            return false;
        }
        this.Cover = n2;
        this.updateBlock();
        return true;
    }

    @Override
    public int tryRemoveCover(int n) {
        if (this.Cover == 255) {
            return -1;
        }
        if ((n ^ 1) != this.Rotation >> 2) {
            return -1;
        }
        int n2 = this.Cover;
        this.Cover = 255;
        this.updateBlock();
        return n2;
    }

    @Override
    public int getCover(int n) {
        if (this.Cover == 255) {
            return -1;
        }
        if ((n ^ 1) != this.Rotation >> 2) {
            return -1;
        }
        return this.Cover;
    }

    @Override
    public int getCoverMask() {
        if (this.Cover == 255) {
            return 0;
        }
        return 1 << (this.Rotation >> 2 ^ 1);
    }

    @Override
    public boolean blockEmpty() {
        return false;
    }

    @Override
    public void addHarvestContents(ArrayList arrayList) {
        super.addHarvestContents(arrayList);
        arrayList.add(new ItemStack(this.getBlockID(), 1, this.getExtendedID() * 256 + this.SubId));
    }

    private void replaceWithCovers() {
        if (this.Cover != 255) {
            short[] arrs = new short[26];
            arrs[this.Rotation >> 2 ^ 1] = (short) this.Cover;
            CoverLib.replaceWithCovers(this.world, this.x, this.y, this.z, 1 << (this.Rotation >> 2 ^ 1), arrs);
            CoreLib.dropItem(this.world, this.x, this.y, this.z, new ItemStack(this.getBlockID(), 1, this.getExtendedID() * 256 + this.SubId));
        } else {
            this.breakBlock();
            RedPowerLib.updateIndirectNeighbors(this.world, this.x, this.y, this.z, this.getBlockID());
        }
    }

    public boolean tryDropBlock() {
        if (RedPowerLib.canSupportWire((IBlockAccess) this.world, this.x, this.y, this.z, this.Rotation >> 2)) {
            return false;
        }
        this.replaceWithCovers();
        return true;
    }

    @Override
    public void onHarvestPart(EntityHuman entityHuman, int n) {
        if (n == this.Rotation >> 2) {
            this.replaceWithCovers();
            return;
        }
        super.onHarvestPart(entityHuman, n);
    }

    @Override
    public float getPartStrength(EntityHuman entityHuman, int n) {
        Block block = Block.byId[this.getBlockID()];
        if (n == this.Rotation >> 2) {
            return entityHuman.getCurrentPlayerStrVsBlock(block, 0) / (block.m() * 30.0f);
        }
        return super.getPartStrength(entityHuman, n);
    }

    @Override
    public void setPartBounds(BlockMultipart blockMultipart, int n) {
        if (n != this.Rotation >> 2) {
            super.setPartBounds(blockMultipart, n);
            return;
        }
        switch (n) {
            case 0: {
                blockMultipart.a(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
                break;
            }
            case 1: {
                blockMultipart.a(0.0f, 0.875f, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 2: {
                blockMultipart.a(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.125f);
                break;
            }
            case 3: {
                blockMultipart.a(0.0f, 0.0f, 0.875f, 1.0f, 1.0f, 1.0f);
                break;
            }
            case 4: {
                blockMultipart.a(0.0f, 0.0f, 0.0f, 0.125f, 1.0f, 1.0f);
                break;
            }
            case 5: {
                blockMultipart.a(0.875f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    @Override
    public int getPartsMask() {
        int n = 1 << (this.Rotation >> 2);
        if (this.Cover != 255) {
            n |= 1 << (this.Rotation >> 2 ^ 1);
        }
        return n;
    }

    @Override
    public int getSolidPartsMask() {
        return this.getPartsMask();
    }

    @Override
    public boolean isBlockStrongPoweringTo(int n) {
        return (this.getPoweringMask(0) & RedPowerLib.getConDirMask(n ^ 1)) > 0;
    }

    @Override
    public boolean isBlockWeakPoweringTo(int n) {
        return (this.getPoweringMask(0) & RedPowerLib.getConDirMask(n ^ 1)) > 0;
    }

    @Override
    public int getBlockID() {
        return RedPowerLogic.blockLogic.id;
    }

    @Override
    public int getExtendedMetadata() {
        return this.SubId;
    }

    @Override
    public void setExtendedMetadata(int n) {
        this.SubId = n;
    }

    public void playSound(String string, float f, float f2, boolean bl) {
        if (!bl && !RedPowerLogic.EnableSounds) {
            return;
        }
        this.world.makeSound((double) ((float) this.x + 0.5f), (double) ((float) this.y + 0.5f), (double) ((float) this.z + 0.5f), string, f, f2);
    }

    public void initSubType(int n) {
        this.SubId = n;
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (this.getLightValue() != 9) {
            this.world.v(this.x, this.y, this.z);
        }
    }

    public int getLightValue() {
        return 9;
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
        this.SubId = nBTTagCompound.getByte("sid") & 255;
        this.Rotation = nBTTagCompound.getByte("rot") & 255;
        int n = nBTTagCompound.getByte("ps") & 255;
        this.Deadmap = nBTTagCompound.getByte("dm") & 255;
        this.Cover = nBTTagCompound.getByte("cov") & 255;
        this.PowerState = n & 15;
        this.Powered = (n & 16) > 0;
        this.Disabled = (n & 32) > 0;
        this.Active = (n & 64) > 0;
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        nBTTagCompound.setByte("sid", (byte) this.SubId);
        nBTTagCompound.setByte("rot", (byte) this.Rotation);
        int n = this.PowerState | (this.Powered ? 16 : 0) | (this.Disabled ? 32 : 0) | (this.Active ? 64 : 0);
        nBTTagCompound.setByte("ps", (byte) n);
        nBTTagCompound.setByte("dm", (byte) this.Deadmap);
        nBTTagCompound.setByte("cov", (byte) this.Cover);
    }

    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        this.SubId = packet211TileDesc.getByte();
        this.Rotation = packet211TileDesc.getByte();
        int n = packet211TileDesc.getByte();
        if (!CoreProxy.isServer()) {
            this.PowerState = n & 15;
            this.Powered = (n & 16) > 0;
            this.Disabled = (n & 32) > 0;
            this.Active = (n & 64) > 0;
        }
        this.Deadmap = (n & 128) > 0 ? packet211TileDesc.getByte() : 0;
        this.Cover = packet211TileDesc.getByte();
    }

    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        packet211TileDesc.addByte(this.SubId);
        packet211TileDesc.addByte(this.Rotation);
        int n = this.PowerState | (this.Powered ? 16 : 0) | (this.Disabled ? 32 : 0) | (this.Active ? 64 : 0) | (this.Deadmap <= 0 ? 0 : 128);
        packet211TileDesc.addByte(n);
        if (this.Deadmap > 0) {
            packet211TileDesc.addByte(this.Deadmap);
        }
        packet211TileDesc.addByte(this.Cover);
    }

    public Packet d() {
        Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
        packet211TileDesc.subId = 1;
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

