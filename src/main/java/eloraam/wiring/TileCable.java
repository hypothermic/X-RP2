/* X-RP - decompiled with CFR */
package eloraam.wiring;

import eloraam.core.CoreLib;
import eloraam.core.IRedPowerWiring;
import eloraam.core.RedPowerLib;
import eloraam.wiring.TileWiring;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.World;

public class TileCable extends TileWiring implements IRedPowerWiring {

    public short[] PowerState = new short[16];

    @Override
    public float getWireHeight() {
	return 0.25f;
    }

    @Override
    public int getExtendedID() {
	return 3;
    }

    @Override
    public int getConnectClass(int n) {
	return 18 + this.Metadata;
    }

    @Override
    public int scanPoweringStrength(int n, int n2) {
	return 0;
    }

    @Override
    public int getCurrentStrength(int n, int n2) {
	if (n2 < 1 || n2 > 16) {
	    return -1;
	}
	if ((n & this.getConnectableMask()) == 0) {
	    return -1;
	}
	return this.PowerState[n2 - 1];
    }

    @Override
    public void updateCurrentStrength() {
	for (int i = 0; i < 16; ++i) {
	    this.PowerState[i] = (short) RedPowerLib.updateBlockCurrentStrength(this.world, this, this.x, this.y, this.z, 1073741823, 2 << i);
	}
	CoreLib.markBlockDirty(this.world, this.x, this.y, this.z);
    }

    @Override
    public int getPoweringMask(int n) {
	if (n < 1 || n > 16) {
	    return 0;
	}
	if (this.PowerState[n - 1] == 0) {
	    return 0;
	}
	return this.getConnectableMask();
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
	super.a(nBTTagCompound);
	byte[] arrby = nBTTagCompound.getByteArray("pwrs");
	if (arrby == null) {
	    return;
	}
	for (int i = 0; i < 16; ++i) {
	    this.PowerState[i] = (short) (arrby[i] & 255);
	}
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
	super.b(nBTTagCompound);
	byte[] arrby = new byte[16];
	for (int i = 0; i < 16; ++i) {
	    arrby[i] = (byte) this.PowerState[i];
	}
	nBTTagCompound.setByteArray("pwrs", arrby);
    }
}
