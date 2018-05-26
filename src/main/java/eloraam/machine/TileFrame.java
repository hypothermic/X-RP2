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
package eloraam.machine;

import eloraam.core.BlockMultipart;
import eloraam.core.CoreLib;
import eloraam.core.CoverLib;
import eloraam.core.IFrameLink;
import eloraam.core.IFrameSupport;
import eloraam.core.IHandlePackets;
import eloraam.core.Packet211TileDesc;
import eloraam.core.TileCoverable;
import eloraam.core.WorldCoord;
import eloraam.machine.BlockFrame;
import eloraam.machine.BlockMachine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.Packet;
import net.minecraft.server.RedPowerMachine;
import net.minecraft.server.World;

public class TileFrame
extends TileCoverable
implements IHandlePackets,
IFrameLink,
IFrameSupport {
    public int CoverSides = 0;
    public int StickySides = 63;
    public short[] Covers = new short[6];

    @Override
    public boolean isFrameMoving() {
        return false;
    }

    @Override
    public boolean canFrameConnectIn(int n) {
        return (this.StickySides & 1 << n) > 0;
    }

    @Override
    public boolean canFrameConnectOut(int n) {
        return (this.StickySides & 1 << n) > 0;
    }

    @Override
    public WorldCoord getFrameLinkset() {
        return null;
    }

    @Override
    public int getExtendedID() {
        return 0;
    }

    @Override
    public void onBlockNeighborChange(int n) {
    }

    @Override
    public int getBlockID() {
        return RedPowerMachine.blockFrame.id;
    }

    @Override
    public int getPartsMask() {
        return this.CoverSides | 536870912;
    }

    @Override
    public int getSolidPartsMask() {
        return this.CoverSides | 536870912;
    }

    @Override
    public boolean blockEmpty() {
        return false;
    }

    @Override
    public void onHarvestPart(EntityHuman entityHuman, int n) {
        boolean bl = false;
        if (n == 29) {
            CoreLib.dropItem(this.world, this.x, this.y, this.z, new ItemStack((Block)RedPowerMachine.blockFrame, 1));
            if (this.CoverSides > 0) {
                this.replaceWithCovers();
                this.updateBlockChange();
            } else {
                this.deleteBlock();
            }
        } else {
            super.onHarvestPart(entityHuman, n);
            return;
        }
    }

    @Override
    public void addHarvestContents(ArrayList arrayList) {
        super.addHarvestContents(arrayList);
        arrayList.add(new ItemStack((Block)RedPowerMachine.blockFrame, 1));
    }

    @Override
    public float getPartStrength(EntityHuman entityHuman, int n) {
        BlockMachine blockMachine = RedPowerMachine.blockMachine;
        if (n == 29) {
            return entityHuman.getCurrentPlayerStrVsBlock((Block)blockMachine, 0) / (blockMachine.m() * 30.0f);
        }
        return super.getPartStrength(entityHuman, n);
    }

    @Override
    public void setPartBounds(BlockMultipart blockMultipart, int n) {
        if (n == 29) {
            blockMultipart.a(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        } else {
            super.setPartBounds(blockMultipart, n);
        }
    }

    @Override
    public boolean canAddCover(int n, int n2) {
        if (n > 5) {
            return false;
        }
        int n3 = n2 >> 8;
        if (n3 != 0 && n3 != 1 && n3 != 3 && n3 != 4) {
            return false;
        }
        return (this.CoverSides & 1 << n) <= 0;
    }

    void rebuildSticky() {
        int n = 0;
        for (int i = 0; i < 6; ++i) {
            int n2 = 1 << i;
            if ((this.CoverSides & n2) == 0) {
                n |= n2;
                continue;
            }
            int n3 = this.Covers[i] >> 8;
            if (n3 != 1 && n3 != 4) continue;
            n |= n2;
        }
        this.StickySides = n;
    }

    @Override
    public boolean tryAddCover(int n, int n2) {
        if (!this.canAddCover(n, n2)) {
            return false;
        }
        this.CoverSides |= 1 << n;
        this.Covers[n] = (short)n2;
        this.rebuildSticky();
        this.updateBlockChange();
        return true;
    }

    @Override
    public int tryRemoveCover(int n) {
        if ((this.CoverSides & 1 << n) == 0) {
            return -1;
        }
        this.CoverSides &= ~ (1 << n);
        short s = this.Covers[n];
        this.Covers[n] = 0;
        this.rebuildSticky();
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

    public void replaceWithCovers() {
        short[] arrs = Arrays.copyOf(this.Covers, 29);
        CoverLib.replaceWithCovers(this.world, this.x, this.y, this.z, this.CoverSides, arrs);
    }

    @Override
    public byte[] getFramePacket() {
        Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
        packet211TileDesc.subId = 9;
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
    public void onFramePickup(IBlockAccess iBlockAccess) {
    }

    @Override
    public void onFrameRefresh(IBlockAccess iBlockAccess) {
    }

    @Override
    public void onFrameDrop() {
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        int n = nBTTagCompound.getInt("cvm") & 63;
        this.CoverSides |= n;
        byte[] arrby = nBTTagCompound.getByteArray("cvs");
        if (arrby != null && n > 0) {
            int n2 = 0;
            for (int i = 0; i < 6; ++i) {
                if ((n & 1 << i) == 0) continue;
                this.Covers[i] = (short)((arrby[n2] & 255) + ((arrby[n2 + 1] & 255) << 8));
                n2 += 2;
            }
        }
        this.rebuildSticky();
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        nBTTagCompound.setInt("cvm", this.CoverSides);
        byte[] arrby = new byte[Integer.bitCount(this.CoverSides) * 2];
        int n = 0;
        for (int i = 0; i < 6; ++i) {
            if ((this.CoverSides & 1 << i) == 0) continue;
            arrby[n] = (byte)(this.Covers[i] & 255);
            arrby[n + 1] = (byte)(this.Covers[i] >> 8);
            n += 2;
        }
        nBTTagCompound.setByteArray("cvs", arrby);
    }

    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        if (packet211TileDesc.subId != 9) {
            return;
        }
        this.CoverSides = (int)packet211TileDesc.getUVLC();
        for (int i = 0; i < 6; ++i) {
            if ((this.CoverSides & 1 << i) <= 0) continue;
            this.Covers[i] = (short)packet211TileDesc.getUVLC();
        }
        this.rebuildSticky();
    }

    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        packet211TileDesc.addUVLC(this.CoverSides);
        for (int i = 0; i < 6; ++i) {
            if ((this.CoverSides & 1 << i) <= 0) continue;
            packet211TileDesc.addUVLC(this.Covers[i]);
        }
    }

    public Packet d() {
        Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
        packet211TileDesc.subId = 9;
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
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.world.notify(this.x, this.y, this.z);
    }
}

