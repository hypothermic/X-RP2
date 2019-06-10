/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.CoreLib;
import eloraam.core.IRedPowerWiring;
import eloraam.core.Packet211TileDesc;
import eloraam.core.RedPowerLib;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;

import java.io.IOException;

public class TileRedstoneTube extends TileTube implements IRedPowerWiring {

    public short PowerState = 0;
    public int ConMask = -1;

    // load from NBT
    @Override
    public void a(NBTTagCompound tag) {
        super.a(tag);
        this.PowerState = (short) (tag.getByte("pwr") & 255);
    }

    // save to NBT
    @Override
    public void b(NBTTagCompound tag) {
        super.b(tag);
        tag.setByte("pwr", (byte) this.PowerState);
    }

    @Override
    public int getConnectableMask() {
        int arg0 = 63;
        int arg1 = 0;
        while (arg1 < 6) {
            if ((this.CoverSides & 1 << arg1) > 0 && this.Covers[arg1] >> 8 < 3) {
                arg0 &= ~(1 << arg1);
            }
            ++arg1;
        }
        return arg0 << 24;
    }

    @Override
    public int getConnectClass(int arg0) {
        return 1;
    }

    @Override
    public int getConnectionMask() {
        if (this.ConMask >= 0) {
            return this.ConMask;
        }
        this.ConMask = RedPowerLib.getConnections(this.world, this, this.x, this.y, this.z);
        return this.ConMask;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public int getCurrentStrength(int arg0, int arg1) {
        return arg1 != 0 ? -1 : ((arg0 & this.getConnectableMask()) == 0 ? -1 : this.PowerState);
    }

    @Override
    public int getExtConnectionMask() {
        return 0;
    }

    @Override
    public int getExtendedID() {
        return 9;
    }

    @Override
    public int getPoweringMask(int arg0) {
        return arg0 == 0 && this.PowerState != 0 ? this.getConnectableMask() : 0;
    }

    @Override
    public boolean isBlockWeakPoweringTo(int arg0) {
        return !RedPowerLib.isSearching() && ((this.getConnectionMask() & 16777216 << (arg0 ^ 1)) != 0 && (RedPowerLib.isBlockRedstone(this.world, this.x, this.y, this.z, arg0 ^ 1) ? this.PowerState > 15 : this.PowerState > 0));
    }

    @Override
    public void onBlockNeighborChange(int arg0) {
        super.onBlockNeighborChange(arg0);
        if (this.ConMask >= 0) {
            this.world.notify(this.x, this.y, this.z);
        }
        this.ConMask = -1;
        RedPowerLib.updateCurrent(this.world, this.x, this.y, this.z);
    }

    @Override
    public void onFrameRefresh(IBlockAccess frame) {
        if (this.ConMask < 0) {
            this.ConMask = RedPowerLib.getConnections(frame, this, this.x, this.y, this.z);
        }
    }

    @Override
    protected void readFromPacket(Packet211TileDesc packet) throws IOException {
        super.readFromPacket(packet);
        this.PowerState = (short) packet.getByte();
        this.ConMask = -1;
    }

    @Override
    public int scanPoweringStrength(int arg0, int arg1) {
        return arg1 != 0 ? 0 : (!RedPowerLib.isPowered(this.world, this.x, this.y, this.z, arg0, this.getConnectionMask()) ? 0 : 255);
    }

    @Override
    public boolean tryAddCover(int coverSide, int cover) {
        if (!this.canAddCover(coverSide, cover)) {
            return false;
        }
        this.CoverSides |= 1 << coverSide;
        this.Covers[coverSide] = (short) cover;
        this.ConMask = -1;
        this.updateBlockChange();
        return true;
    }

    @Override
    public int tryRemoveCover(int coverSide) {
        if ((this.CoverSides & 1 << coverSide) == 0) {
            return -1;
        }
        this.CoverSides &= ~(1 << coverSide);
        short arg1 = this.Covers[coverSide];
        this.Covers[coverSide] = 0;
        this.ConMask = -1;
        this.updateBlockChange();
        return arg1;
    }

    @Override
    public void updateCurrentStrength() {
        this.PowerState = (short) RedPowerLib.updateBlockCurrentStrength(this.world, this, this.x, this.y, this.z, 1073741823, 1);
        CoreLib.markBlockDirty(this.world, this.x, this.y, this.z);
    }

    @Override
    protected void writeToPacket(Packet211TileDesc packet) {
        super.writeToPacket(packet);
        packet.addByte(this.PowerState);
    }
}

