/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Block
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.*;
import net.minecraft.server.*;

import java.io.IOException;
import java.util.ArrayList;

public class TilePipe
        extends TileCovered
        implements IPipeConnectable {
    public FluidBuffer pipebuf;
    public int Pressure;
    public int ConCache;
    public int Flanges;
    private boolean hasChanged;

    public TilePipe() {
        this.pipebuf = new FluidBuffer() {

            @Override
            public TileEntity getParent() {
                return TilePipe.this;
            }

            @Override
            public void onChange() {
                TilePipe.this.dirtyBlock();
            }
        };
        this.Pressure = 0;
        this.ConCache = -1;
        this.Flanges = -1;
        this.hasChanged = false;
    }

    @Override
    public int getPipeConnectableSides() {
        int n = 63;
        for (int i = 0; i < 6; ++i) {
            if ((this.CoverSides & 1 << i) <= 0 || this.Covers[i] >> 8 >= 3) continue;
            n &= ~(1 << i);
        }
        return n;
    }

    @Override
    public int getPipeFlangeSides() {
        this.cacheCon();
        if (this.ConCache == 3 || this.ConCache == 12 || this.ConCache == 48) {
            return 0;
        }
        if (Integer.bitCount(this.ConCache) == 1) {
            return 0;
        }
        return this.ConCache;
    }

    @Override
    public int getPipePressure(int n) {
        return this.Pressure;
    }

    @Override
    public FluidBuffer getPipeBuffer(int n) {
        return this.pipebuf;
    }

    @Override
    public boolean tryAddCover(int n, int n2) {
        if (!super.tryAddCover(n, n2)) {
            return false;
        }
        this.uncache();
        this.updateBlockChange();
        return true;
    }

    @Override
    public int tryRemoveCover(int n) {
        int n2 = super.tryRemoveCover(n);
        if (n2 < 0) {
            return -1;
        }
        this.uncache();
        this.updateBlockChange();
        return n2;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void q_() {
        super.q_();
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        this.cacheCon();
        for (int i = 0; i < 6; ++i) {
            if ((this.ConCache & 1 << i) == 0) continue;
            WorldCoord worldCoord = new WorldCoord(this);
            worldCoord.step(i);
            IPipeConnectable iPipeConnectable = (IPipeConnectable) CoreLib.getTileEntity((IBlockAccess) this.world, worldCoord, IPipeConnectable.class);
            if (iPipeConnectable == null) continue;
            int n5 = iPipeConnectable.getPipePressure(i ^ 1);
            n3 = Math.min(n5, n3);
            n4 = Math.max(n5, n4);
            n += n5;
            ++n2;
        }
        if (n2 == 0) {
            this.Pressure = 0;
        } else {
            if (n3 < 0) {
                ++n3;
            }
            if (n4 > 0) {
                --n4;
            }
            this.Pressure = Math.max(n3, Math.min(n4, n / n2 + Integer.signum(n)));
        }
        PipeLib.movePipeLiquid(this.world, this, new WorldCoord(this), this.ConCache);
        this.dirtyBlock();
        if (CoreProxy.isServer() && (this.world.getTime() & 16) == 0) {
            this.sendItemUpdate();
        }
    }

    public void uncache() {
        this.ConCache = -1;
        this.Flanges = -1;
    }

    public void cacheCon() {
        if (this.ConCache < 0) {
            this.ConCache = PipeLib.getConnections((IBlockAccess) this.world, this.x, this.y, this.z);
        }
    }

    public void cacheFlange() {
        if (this.Flanges >= 0) {
            return;
        }
        this.cacheCon();
        this.Flanges = this.getPipeFlangeSides();
        for (int i = 0; i < 6; ++i) {
            if ((this.ConCache & 1 << i) == 0) continue;
            WorldCoord worldCoord = new WorldCoord(this);
            worldCoord.step(i);
            IPipeConnectable iPipeConnectable = (IPipeConnectable) CoreLib.getTileEntity((IBlockAccess) this.world, worldCoord, IPipeConnectable.class);
            if (iPipeConnectable == null || (iPipeConnectable.getPipeFlangeSides() & 1 << (i ^ 1)) <= 0) continue;
            this.Flanges |= 1 << i;
        }
    }

    @Override
    public void onFrameRefresh(IBlockAccess iBlockAccess) {
        if (this.ConCache < 0) {
            this.ConCache = PipeLib.getConnections(iBlockAccess, this.x, this.y, this.z);
        }
        this.Flanges = 0;
    }

    @Override
    public int getBlockID() {
        return RedPowerBase.blockMicro.id;
    }

    @Override
    public int getExtendedID() {
        return 7;
    }

    @Override
    public void onBlockNeighborChange(int n) {
        int n2 = this.Flanges;
        int n3 = this.ConCache;
        this.uncache();
        this.cacheFlange();
        if (this.Flanges != n2 || n3 != this.ConCache) {
            this.updateBlock();
        }
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
        if (n == 29) {
            CoreLib.dropItem(this.world, this.x, this.y, this.z, new ItemStack(RedPowerBase.blockMicro.id, 1, this.getExtendedID() << 8));
            if (this.CoverSides > 0) {
                this.replaceWithCovers();
            } else {
                this.deleteBlock();
            }
        } else {
            super.onHarvestPart(entityHuman, n);
            return;
        }
        this.uncache();
        this.updateBlockChange();
    }

    @Override
    public void addHarvestContents(ArrayList arrayList) {
        super.addHarvestContents(arrayList);
        arrayList.add(new ItemStack(RedPowerBase.blockMicro.id, 1, this.getExtendedID() << 8));
    }

    @Override
    public float getPartStrength(EntityHuman entityHuman, int n) {
        BlockMachine blockMachine = RedPowerMachine.blockMachine;
        if (n == 29) {
            return entityHuman.getCurrentPlayerStrVsBlock((Block) blockMachine, 0) / (blockMachine.m() * 30.0f);
        }
        return super.getPartStrength(entityHuman, n);
    }

    @Override
    public void setPartBounds(BlockMultipart blockMultipart, int n) {
        if (n == 29) {
            blockMultipart.a(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
        } else {
            super.setPartBounds(blockMultipart, n);
        }
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        this.Pressure = nBTTagCompound.getInt("psi");
        this.pipebuf.readFromNBT(nBTTagCompound, "buf");
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        nBTTagCompound.setInt("psi", this.Pressure);
        this.pipebuf.writeToNBT(nBTTagCompound, "buf");
    }

    @Override
    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        if (packet211TileDesc.subId == 11) {
            this.pipebuf.readFromPacket(packet211TileDesc);
            this.updateBlock();
        } else {
            super.readFromPacket(packet211TileDesc);
            this.pipebuf.readFromPacket(packet211TileDesc);
            this.ConCache = -1;
            this.Flanges = -1;
            this.updateBlock();
        }
    }

    @Override
    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        super.writeToPacket(packet211TileDesc);
        this.pipebuf.writeToPacket(packet211TileDesc);
    }

    protected void sendItemUpdate() {
        Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
        packet211TileDesc.subId = 11;
        packet211TileDesc.xCoord = this.x;
        packet211TileDesc.yCoord = this.y;
        packet211TileDesc.zCoord = this.z;
        this.pipebuf.writeToPacket(packet211TileDesc);
        packet211TileDesc.encode();
        CoreProxy.sendPacketToPosition(packet211TileDesc, this.x, this.z);
    }

    @Override
    public void handlePacket(Packet211TileDesc packet211TileDesc) {
        try {
            this.readFromPacket(packet211TileDesc);
        } catch (IOException iOException) {
            // empty catch block
        }
    }

}

