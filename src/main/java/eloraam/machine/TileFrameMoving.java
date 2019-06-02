/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.AxisAlignedBB
 *  net.minecraft.server.Block
 *  net.minecraft.server.Entity
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.Material
 *  net.minecraft.server.NBTBase
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.Packet
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 *  net.minecraft.server.WorldChunkManager
 */
package eloraam.machine;

import eloraam.core.*;
import net.minecraft.server.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TileFrameMoving
        extends TileMultipart
        implements IFrameLink,
        IHandlePackets {
    FrameBlockAccess frameblock;
    public int motorX;
    public int motorY;
    public int motorZ;
    public int movingBlockID;
    public int movingBlockMeta;
    public boolean movingCrate;
    public TileEntity movingTileEntity;
    public byte lastMovePos;

    public TileFrameMoving() {
        this.frameblock = new FrameBlockAccess();
        this.movingBlockID = 0;
        this.movingBlockMeta = 0;
        this.movingCrate = false;
        this.movingTileEntity = null;
        this.lastMovePos = 0;
    }

    @Override
    public boolean isFrameMoving() {
        return true;
    }

    @Override
    public boolean canFrameConnectIn(int n) {
        return true;
    }

    @Override
    public boolean canFrameConnectOut(int n) {
        return true;
    }

    @Override
    public WorldCoord getFrameLinkset() {
        return new WorldCoord(this.motorX, this.motorY, this.motorZ);
    }

    @Override
    public int getExtendedID() {
        return 1;
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
        return this.movingBlockID != 0 ? 536870912 : 0;
    }

    @Override
    public int getSolidPartsMask() {
        return this.movingBlockID != 0 ? 536870912 : 0;
    }

    @Override
    public boolean blockEmpty() {
        return false;
    }

    @Override
    public void onHarvestPart(EntityHuman entityHuman, int n) {
    }

    @Override
    public void addHarvestContents(ArrayList arrayList) {
        super.addHarvestContents(arrayList);
    }

    @Override
    public float getPartStrength(EntityHuman entityHuman, int n) {
        BlockMachine blockMachine = RedPowerMachine.blockMachine;
        return 0.0f;
    }

    @Override
    public void setPartBounds(BlockMultipart blockMultipart, int n) {
        TileMotor tileMotor = (TileMotor) CoreLib.getTileEntity((IBlockAccess) this.world, this.motorX, this.motorY, this.motorZ, TileMotor.class);
        if (tileMotor == null) {
            return;
        }
        float f = tileMotor.getMoveScaled();
        switch (tileMotor.MoveDir) {
            case 0: {
                blockMultipart.a(0.0f, 0.0f - f, 0.0f, 1.0f, 1.0f - f, 1.0f);
                break;
            }
            case 1: {
                blockMultipart.a(0.0f, 0.0f + f, 0.0f, 1.0f, 1.0f + f, 1.0f);
                break;
            }
            case 2: {
                blockMultipart.a(0.0f, 0.0f, 0.0f - f, 1.0f, 1.0f, 1.0f - f);
                break;
            }
            case 3: {
                blockMultipart.a(0.0f, 0.0f, 0.0f + f, 1.0f, 1.0f, 1.0f + f);
                break;
            }
            case 4: {
                blockMultipart.a(0.0f - f, 0.0f, 0.0f, 1.0f - f, 1.0f, 1.0f);
                break;
            }
            case 5: {
                blockMultipart.a(0.0f + f, 0.0f, 0.0f, 1.0f + f, 1.0f, 1.0f);
            }
        }
    }

    public IBlockAccess getFrameBlockAccess() {
        return this.frameblock;
    }

    public void setContents(int n, int n2, int n3, int n4, int n5, TileEntity tileEntity) {
        this.movingBlockID = n;
        this.movingBlockMeta = n2;
        this.motorX = n3;
        this.motorY = n4;
        this.motorZ = n5;
        this.movingTileEntity = tileEntity;
        if (this.movingTileEntity != null) {
            if (RedPowerMachine.FrameAlwaysCrate) {
                this.movingCrate = true;
            }
            if (!(this.movingTileEntity instanceof IFrameSupport)) {
                this.movingCrate = true;
            }
        }
    }

    public void doRefresh(IBlockAccess iBlockAccess) {
        if (!(this.movingTileEntity instanceof IFrameSupport)) {
            return;
        }
        IFrameSupport iFrameSupport = (IFrameSupport) this.movingTileEntity;
        iFrameSupport.onFrameRefresh(iBlockAccess);
    }

    public void dropBlock() {
        this.world.setRawTypeIdAndData(this.x, this.y, this.z, this.movingBlockID, this.movingBlockMeta);
        if (this.movingTileEntity != null) {
            this.movingTileEntity.x = this.x;
            this.movingTileEntity.y = this.y;
            this.movingTileEntity.z = this.z;
            this.movingTileEntity.m();
            this.world.setTileEntity(this.x, this.y, this.z, this.movingTileEntity);
        }
        this.world.notify(this.x, this.y, this.z);
        CoreLib.markBlockDirty(this.world, this.x, this.y, this.z);
        RedPowerLib.updateIndirectNeighbors(this.world, this.x, this.y, this.z, this.movingBlockID);
    }

    private AxisAlignedBB getAABB(int n, float f) {
        AxisAlignedBB axisAlignedBB = AxisAlignedBB.b((double) this.x, (double) this.y, (double) this.z, (double) (this.x + 1), (double) (this.y + 1), (double) (this.z + 1));
        switch (n) {
            case 0: {
                axisAlignedBB.b -= (double) f;
                axisAlignedBB.e -= (double) f;
                break;
            }
            case 1: {
                axisAlignedBB.b += (double) f;
                axisAlignedBB.e += (double) f;
                break;
            }
            case 2: {
                axisAlignedBB.c -= (double) f;
                axisAlignedBB.f -= (double) f;
                break;
            }
            case 3: {
                axisAlignedBB.c += (double) f;
                axisAlignedBB.f += (double) f;
                break;
            }
            case 4: {
                axisAlignedBB.a -= (double) f;
                axisAlignedBB.d -= (double) f;
                break;
            }
            case 5: {
                axisAlignedBB.a += (double) f;
                axisAlignedBB.d += (double) f;
            }
        }
        return axisAlignedBB;
    }

    void pushEntities(TileMotor tileMotor) {
        float f = (float) this.lastMovePos / 16.0f;
        float f2 = (float) tileMotor.MovePos / 16.0f;
        this.lastMovePos = (byte) tileMotor.MovePos;
        float f3 = 0.0f;
        float f4 = 0.0f;
        float f5 = 0.0f;
        switch (tileMotor.MoveDir) {
            case 0: {
                f4 -= f2 - f;
                break;
            }
            case 1: {
                f4 += f2 - f;
                break;
            }
            case 2: {
                f5 -= f2 - f;
                break;
            }
            case 3: {
                f5 += f2 - f;
                break;
            }
            case 4: {
                f3 -= f2 - f;
                break;
            }
            case 5: {
                f3 += f2 - f;
            }
        }
        AxisAlignedBB axisAlignedBB = this.getAABB(tileMotor.MoveDir, f2);
        List list = this.world.getEntities(null, axisAlignedBB);
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(list);
        for (Object e : arrayList) {
            Entity entity = (Entity) e;
            entity.move((double) f3, (double) f4, (double) f5);
        }
    }

    @Override
    public void q_() {
        super.q_();
        TileMotor tileMotor = (TileMotor) CoreLib.getTileEntity((IBlockAccess) this.world, this.motorX, this.motorY, this.motorZ, TileMotor.class);
        if (tileMotor == null || tileMotor.MovePos < 0) {
            if (CoreProxy.isClient(this.world)) {
                return;
            }
            this.dropBlock();
            return;
        }
        this.pushEntities(tileMotor);
    }

    public void m() {
        super.m();
        if (this.movingTileEntity != null) {
            this.movingTileEntity.world = this.world;
        }
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        this.motorX = nBTTagCompound.getInt("mx");
        this.motorY = nBTTagCompound.getInt("my");
        this.motorZ = nBTTagCompound.getInt("mz");
        this.movingBlockID = nBTTagCompound.getInt("mbid");
        this.movingBlockMeta = nBTTagCompound.getInt("mbmd");
        this.lastMovePos = nBTTagCompound.getByte("lmp");
        if (nBTTagCompound.hasKey("mte")) {
            NBTTagCompound nBTTagCompound2 = nBTTagCompound.getCompound("mte");
            this.movingTileEntity = TileEntity.c((NBTTagCompound) nBTTagCompound2);
        } else {
            this.movingTileEntity = null;
        }
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        nBTTagCompound.setInt("mx", this.motorX);
        nBTTagCompound.setInt("my", this.motorY);
        nBTTagCompound.setInt("mz", this.motorZ);
        nBTTagCompound.setInt("mbid", this.movingBlockID);
        nBTTagCompound.setInt("mbmd", this.movingBlockMeta);
        nBTTagCompound.setByte("lmp", this.lastMovePos);
        if (this.movingTileEntity != null) {
            NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
            this.movingTileEntity.b(nBTTagCompound2);
            nBTTagCompound.set("mte", (NBTBase) nBTTagCompound2);
        }
    }

    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        this.motorX = (int) packet211TileDesc.getVLC();
        this.motorY = (int) packet211TileDesc.getVLC();
        this.motorZ = (int) packet211TileDesc.getVLC();
        this.movingBlockID = (int) packet211TileDesc.getUVLC();
        this.movingBlockMeta = packet211TileDesc.getByte();
        if (this.movingBlockID != 0) {
            this.movingTileEntity = Block.byId[this.movingBlockID].getTileEntity(this.movingBlockMeta);
            if (this.movingTileEntity != null) {
                if (!(this.movingTileEntity instanceof IFrameSupport)) {
                    this.movingCrate = true;
                    return;
                }
                this.movingTileEntity.world = this.world;
                this.movingTileEntity.x = this.x;
                this.movingTileEntity.y = this.y;
                this.movingTileEntity.z = this.z;
                IFrameSupport iFrameSupport = (IFrameSupport) this.movingTileEntity;
                iFrameSupport.handleFramePacket(packet211TileDesc.getByteArray());
            }
        }
    }

    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        packet211TileDesc.addVLC(this.motorX);
        packet211TileDesc.addVLC(this.motorY);
        packet211TileDesc.addVLC(this.motorZ);
        packet211TileDesc.addUVLC(this.movingBlockID);
        packet211TileDesc.addByte(this.movingBlockMeta);
        if (this.movingTileEntity instanceof IFrameSupport) {
            IFrameSupport iFrameSupport = (IFrameSupport) this.movingTileEntity;
            packet211TileDesc.addByteArray(iFrameSupport.getFramePacket());
        } else {
            packet211TileDesc.addByteArray(new byte[0]);
        }
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

    private class FrameBlockAccess
            implements IBlockAccess {
        private TileFrameMoving getFrame(int n, int n2, int n3) {
            TileFrameMoving tileFrameMoving = (TileFrameMoving) CoreLib.getTileEntity((IBlockAccess) TileFrameMoving.this.world, n, n2, n3, TileFrameMoving.class);
            if (tileFrameMoving == null) {
                return null;
            }
            if (tileFrameMoving.motorX != TileFrameMoving.this.motorX || tileFrameMoving.motorY != TileFrameMoving.this.motorY || tileFrameMoving.motorZ != tileFrameMoving.motorZ) {
                return null;
            }
            return tileFrameMoving;
        }

        public int getTypeId(int n, int n2, int n3) {
            TileFrameMoving tileFrameMoving = this.getFrame(n, n2, n3);
            if (tileFrameMoving == null) {
                return 0;
            }
            return tileFrameMoving.movingBlockID;
        }

        public TileEntity getTileEntity(int n, int n2, int n3) {
            TileFrameMoving tileFrameMoving = this.getFrame(n, n2, n3);
            if (tileFrameMoving == null) {
                return null;
            }
            return tileFrameMoving.movingTileEntity;
        }

        public int getLightBrightnessForSkyBlocks(int n, int n2, int n3, int n4) {
            return CoreProxy.getLightBrightnessForSkyBlocks((IBlockAccess) TileFrameMoving.this.world, n, n2, n3, n4);
        }

        public float getBrightness(int n, int n2, int n3, int n4) {
            return CoreProxy.getBrightness((IBlockAccess) TileFrameMoving.this.world, n, n2, n3, n4);
        }

        public float getLightBrightness(int n, int n2, int n3) {
            return TileFrameMoving.this.world.p(n, n2, n3);
        }

        public int getData(int n, int n2, int n3) {
            TileFrameMoving tileFrameMoving = this.getFrame(n, n2, n3);
            if (tileFrameMoving == null) {
                return 0;
            }
            return tileFrameMoving.movingBlockMeta;
        }

        public Material getMaterial(int n, int n2, int n3) {
            int n4 = this.getTypeId(n, n2, n3);
            if (n4 == 0) {
                return Material.AIR;
            }
            return Block.byId[n4].material;
        }

        public boolean isBlockOpaqueCube(int n, int n2, int n3) {
            Block block = Block.byId[this.getTypeId(n, n2, n3)];
            if (block == null) {
                return false;
            }
            return block.a();
        }

        public boolean e(int n, int n2, int n3) {
            Block block = Block.byId[this.getTypeId(n, n2, n3)];
            if (block == null) {
                return false;
            }
            return block.isBlockNormalCube(TileFrameMoving.this.world, n, n2, n3);
        }

        public boolean isAirBlock(int n, int n2, int n3) {
            int n4 = this.getTypeId(n, n2, n3);
            if (n4 == 0) {
                return true;
            }
            return Block.byId[n4].isAirBlock(TileFrameMoving.this.world, n, n2, n3);
        }

        public WorldChunkManager getWorldChunkManager() {
            return TileFrameMoving.this.world.getWorldChunkManager();
        }

        public int getHeight() {
            return TileFrameMoving.this.world.getHeight();
        }
    }

}

