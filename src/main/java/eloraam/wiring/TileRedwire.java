/* X-RP - decompiled with CFR */
package eloraam.wiring;

import eloraam.core.CoreLib;
import eloraam.core.IRedPowerWiring;
import eloraam.core.Packet211TileDesc;
import eloraam.core.RedPowerLib;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;

import java.io.IOException;

public class TileRedwire extends TileWiring implements IRedPowerWiring {

    public short PowerState = 0;

    @Override
    public void a(NBTTagCompound arg0) {
        super.a(arg0);
        this.PowerState = (short) (arg0.getByte("pwr") & 255);
    }

    @Override
    public void b(NBTTagCompound arg0) {
        super.b(arg0);
        arg0.setByte("pwr", (byte) this.PowerState);
    }

    @Override
    public int getConnectableMask() {
        if (this.ConaMask >= 0) {
            return this.ConaMask;
        }
        int arg0 = super.getConnectableMask();
        if ((this.ConSides & 1) > 0) {
            arg0 |= 16777216;
        }
        if ((this.ConSides & 2) > 0) {
            arg0 |= 33554432;
        }
        if ((this.ConSides & 4) > 0) {
            arg0 |= 67108864;
        }
        if ((this.ConSides & 8) > 0) {
            arg0 |= 134217728;
        }
        if ((this.ConSides & 16) > 0) {
            arg0 |= 268435456;
        }
        if ((this.ConSides & 32) > 0) {
            arg0 |= 536870912;
        }
        this.ConaMask = arg0;
        return arg0;
    }

    @Override
    public int getConnectClass(int arg0) {
        return 1;
    }

    @Override
    public int getCurrentStrength(int arg0, int arg1) {
        return arg1 != 0 ? -1 : ((arg0 & this.getConnectableMask()) == 0 ? -1 : this.PowerState);
    }

    @Override
    public int getExtendedID() {
        return 1;
    }

    @Override
    public int getPoweringMask(int arg0) {
        return arg0 == 0 && this.PowerState != 0 ? this.getConnectableMask() : 0;
    }

    @Override
    public boolean isBlockStrongPoweringTo(int arg0) {
        if (RedPowerLib.isSearching()) {
            return false;
        }
        int arg1 = 15 << ((arg0 ^ 1) << 2);
        return (arg1 &= this.getConnectableMask()) == 0 ? false : this.PowerState > 0;
    }

    @Override
    public boolean isBlockWeakPoweringTo(int arg0) {
        if (RedPowerLib.isSearching()) {
            return false;
        }
        int arg1 = 15 << ((arg0 ^ 1) << 2);
        arg1 |= RedPowerLib.getConDirMask(arg0 ^ 1);
        return (arg1 &= this.getConnectableMask()) == 0 ? false : (RedPowerLib.isBlockRedstone((IBlockAccess) this.world, this.x, this.y, this.z, arg0 ^ 1) ? this.PowerState > 15 : this.PowerState > 0);
    }

    @Override
    protected void readFromPacket(Packet211TileDesc arg0) throws IOException {
        super.readFromPacket(arg0);
        this.PowerState = (short) arg0.getByte();
    }

    @Override
    public int scanPoweringStrength(int arg0, int arg1) {
        return arg1 != 0 ? 0 : (!RedPowerLib.isPowered((IBlockAccess) this.world, this.x, this.y, this.z, arg0, this.ConSides) ? 0 : 255);
    }

    @Override
    public void updateCurrentStrength() {
        this.PowerState = (short) RedPowerLib.updateBlockCurrentStrength(this.world, this, this.x, this.y, this.z, 1073741823, 1);
        CoreLib.markBlockDirty(this.world, this.x, this.y, this.z);
    }

    @Override
    protected void writeToPacket(Packet211TileDesc arg0) {
        super.writeToPacket(arg0);
        arg0.addByte(this.PowerState);
    }
}
