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
import eloraam.machine.TileTube;
import java.io.IOException;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.World;

public class TileRedstoneTube
extends TileTube
implements IRedPowerWiring {
    public short PowerState = 0;
    public int ConMask = -1;

    @Override
    public void a(NBTTagCompound arg0) {
        super.a(arg0);
        this.PowerState = (short)(arg0.getByte("pwr") & 255);
    }

    @Override
    public void b(NBTTagCompound arg0) {
        super.b(arg0);
        arg0.setByte("pwr", (byte)this.PowerState);
    }

    @Override
    public int getConnectableMask() {
        int arg0 = 63;
        int arg1 = 0;
        while (arg1 < 6) {
            if ((this.CoverSides & 1 << arg1) > 0 && this.Covers[arg1] >> 8 < 3) {
                arg0 &= ~ (1 << arg1);
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
        this.ConMask = RedPowerLib.getConnections((IBlockAccess)this.world, this, this.x, this.y, this.z);
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
        return RedPowerLib.isSearching() ? false : ((this.getConnectionMask() & 16777216 << (arg0 ^ 1)) == 0 ? false : (RedPowerLib.isBlockRedstone((IBlockAccess)this.world, this.x, this.y, this.z, arg0 ^ 1) ? this.PowerState > 15 : this.PowerState > 0));
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
    public void onFrameRefresh(IBlockAccess arg0) {
        if (this.ConMask < 0) {
            this.ConMask = RedPowerLib.getConnections(arg0, this, this.x, this.y, this.z);
        }
    }

    @Override
    protected void readFromPacket(Packet211TileDesc arg0) throws IOException {
        super.readFromPacket(arg0);
        this.PowerState = (short)arg0.getByte();
        this.ConMask = -1;
    }

    @Override
    public int scanPoweringStrength(int arg0, int arg1) {
        return arg1 != 0 ? 0 : (!RedPowerLib.isPowered((IBlockAccess)this.world, this.x, this.y, this.z, arg0, this.getConnectionMask()) ? 0 : 255);
    }

    @Override
    public boolean tryAddCover(int arg0, int arg1) {
        if (!this.canAddCover(arg0, arg1)) {
            return false;
        }
        this.CoverSides |= 1 << arg0;
        this.Covers[arg0] = (short)arg1;
        this.ConMask = -1;
        this.updateBlockChange();
        return true;
    }

    @Override
    public int tryRemoveCover(int arg0) {
        if ((this.CoverSides & 1 << arg0) == 0) {
            return -1;
        }
        this.CoverSides &= ~ (1 << arg0);
        short arg1 = this.Covers[arg0];
        this.Covers[arg0] = 0;
        this.ConMask = -1;
        this.updateBlockChange();
        return arg1;
    }

    @Override
    public void updateCurrentStrength() {
        this.PowerState = (short)RedPowerLib.updateBlockCurrentStrength(this.world, this, this.x, this.y, this.z, 1073741823, 1);
        CoreLib.markBlockDirty(this.world, this.x, this.y, this.z);
    }

    @Override
    protected void writeToPacket(Packet211TileDesc arg0) {
        super.writeToPacket(arg0);
        arg0.addByte(this.PowerState);
    }
}

