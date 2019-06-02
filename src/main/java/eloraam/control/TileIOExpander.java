/* X-RP - decompiled with CFR */
package eloraam.control;

import eloraam.base.ItemScrewdriver;
import eloraam.core.*;
import net.minecraft.server.*;

import java.io.IOException;
import java.util.ArrayList;

public class TileIOExpander extends TileMultipart implements IRedbusConnectable, IRedPowerConnectable, IHandlePackets, IFrameSupport {

    public int Rotation = 0;
    public int WBuf = 0;
    public int WBufNew = 0;
    public int RBuf = 0;
    private int rbaddr = 3;

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
        switch (n) {
            case 0: {
                return this.RBuf & 255;
            }
            case 1: {
                return this.RBuf >> 8;
            }
            case 2: {
                return this.WBufNew & 255;
            }
            case 3: {
                return this.WBufNew >> 8;
            }
        }
        return 0;
    }

    @Override
    public void rbWrite(int n, int n2) {
        this.dirtyBlock();
        switch (n) {
            case 0:
            case 2: {
                this.WBufNew = this.WBufNew & 65280 | n2;
                this.scheduleTick(2);
                break;
            }
            case 1:
            case 3: {
                this.WBufNew = this.WBufNew & 255 | n2 << 8;
                this.scheduleTick(2);
            }
        }
    }

    @Override
    public int getConnectableMask() {
        return 15;
    }

    @Override
    public int getConnectClass(int n) {
        return n != CoreLib.rotToSide(this.Rotation) ? 66 : 18;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public int getPoweringMask(int n) {
        if (n == 0) {
            return 0;
        }
        if ((this.WBuf & 1 << n - 1) > 0) {
            return RedPowerLib.mapRotToCon(8, this.Rotation);
        }
        return 0;
    }

    @Override
    public void onBlockPlacedBy(EntityLiving entityLiving) {
        this.Rotation = (int) Math.floor((double) (entityLiving.yaw * 4.0f / 360.0f) + 0.5) + 1 & 3;
    }

    @Override
    public boolean onPartActivateSide(EntityHuman entityHuman, int n, int n2) {
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
        return false;
    }

    @Override
    public void onTileTick() {
        if (this.WBuf == this.WBufNew) {
            return;
        }
        this.WBuf = this.WBufNew;
        this.onBlockNeighborChange(0);
        this.updateBlockChange();
    }

    @Override
    public void onBlockNeighborChange(int n) {
        boolean bl = false;
        for (int i = 0; i < 16; ++i) {
            int n2 = RedPowerLib.getRotPowerState((IBlockAccess) this.world, this.x, this.y, this.z, 8, this.Rotation, i + 1);
            if (n2 == 0) {
                if ((this.RBuf & 1 << i) <= 0)
                    continue;
                this.RBuf &= ~(1 << i);
                bl = true;
                continue;
            }
            if ((this.RBuf & 1 << i) != 0)
                continue;
            this.RBuf |= 1 << i;
            bl = true;
        }
        if (bl) {
            this.updateBlock();
        }
    }

    @Override
    public int getBlockID() {
        return RedPowerControl.blockFlatPeripheral.id;
    }

    @Override
    public int getExtendedID() {
        return 0;
    }

    @Override
    public void addHarvestContents(ArrayList arrayList) {
        super.addHarvestContents(arrayList);
        arrayList.add(new ItemStack(this.getBlockID(), 1, 0));
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
        blockMultipart.a(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
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
        this.WBuf = nBTTagCompound.getShort("wb") & 65535;
        this.WBufNew = nBTTagCompound.getShort("wbn") & 65535;
        this.RBuf = nBTTagCompound.getShort("rb") & 65535;
        this.rbaddr = nBTTagCompound.getByte("rbaddr") & 255;
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        nBTTagCompound.setByte("rot", (byte) this.Rotation);
        nBTTagCompound.setShort("wb", (short) this.WBuf);
        nBTTagCompound.setShort("wbn", (short) this.WBufNew);
        nBTTagCompound.setShort("rb", (short) this.RBuf);
        nBTTagCompound.setByte("rbaddr", (byte) this.rbaddr);
    }

    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        this.Rotation = packet211TileDesc.getByte();
        this.WBuf = (int) packet211TileDesc.getUVLC();
    }

    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        packet211TileDesc.addByte(this.Rotation);
        packet211TileDesc.addUVLC(this.WBuf);
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
