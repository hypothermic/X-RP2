/* X-RP - decompiled with CFR */
package eloraam.wiring;

import eloraam.core.CoreLib;
import eloraam.core.IRedPowerWiring;
import eloraam.core.Packet211TileDesc;
import eloraam.core.RedPowerLib;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;

import java.io.IOException;

public class TileInsulatedWire extends TileWiring implements IRedPowerWiring {

    public short PowerState = 0;

    @Override
    public float getWireHeight() {
        return 0.188f;
    }

    @Override
    public int getExtendedID() {
        return 2;
    }

    @Override
    public boolean isBlockWeakPoweringTo(int n) {
        if (RedPowerLib.isSearching()) {
            return false;
        }
        int n2 = RedPowerLib.getConDirMask(n ^ 1);
        if ((n2 &= this.getConnectableMask()) == 0) {
            return false;
        }
        if (RedPowerLib.isBlockRedstone((IBlockAccess) this.world, this.x, this.y, this.z, n ^ 1)) {
            return this.PowerState > 15;
        }
        return this.PowerState > 0;
    }

    @Override
    public int getConnectClass(int n) {
        return 2 + this.Metadata;
    }

    @Override
    public int scanPoweringStrength(int n, int n2) {
        return !RedPowerLib.isPowered((IBlockAccess) this.world, this.x, this.y, this.z, n, 0) ? 0 : 255;
    }

    @Override
    public int getCurrentStrength(int n, int n2) {
        if (n2 != 0 && n2 != this.Metadata + 1) {
            return -1;
        }
        if ((n & this.getConnectableMask()) == 0) {
            return -1;
        }
        return this.PowerState;
    }

    @Override
    public void updateCurrentStrength() {
        this.PowerState = (short) RedPowerLib.updateBlockCurrentStrength(this.world, this, this.x, this.y, this.z, 16777215, 1 | 2 << this.Metadata);
        CoreLib.markBlockDirty(this.world, this.x, this.y, this.z);
    }

    @Override
    public int getPoweringMask(int n) {
        if (this.PowerState == 0) {
            return 0;
        }
        if (n != 0 && n != this.Metadata + 1) {
            return 0;
        }
        return this.getConnectableMask();
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        this.PowerState = (short) (nBTTagCompound.getByte("pwr") & 255);
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        nBTTagCompound.setByte("pwr", (byte) this.PowerState);
    }

    @Override
    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        super.readFromPacket(packet211TileDesc);
        this.PowerState = (short) packet211TileDesc.getByte();
    }

    @Override
    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        super.writeToPacket(packet211TileDesc);
        packet211TileDesc.addByte(this.PowerState);
    }
}
