/* X-RP - decompiled with CFR */
package eloraam.wiring;

import eloraam.core.BluePowerConductor;
import eloraam.core.CoreProxy;
import eloraam.core.IBluePowerConnectable;
import eloraam.core.RedPowerLib;
import eloraam.wiring.TileWiring;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public class TileBluewire extends TileWiring implements IBluePowerConnectable {

    BluePowerConductor cond;

    public TileBluewire() {
	this.cond = new BluePowerConductor() {

	    @Override
	    public TileEntity getParent() {
		return TileBluewire.this;
	    }

	    @Override
	    public double getInvCap() {
		return 8.0;
	    }
	};
    }

    @Override
    public float getWireHeight() {
	return 0.188f;
    }

    @Override
    public int getExtendedID() {
	return 5;
    }

    @Override
    public int getConnectClass(int n) {
	return 64;
    }

    @Override
    public BluePowerConductor getBlueConductor() {
	return this.cond;
    }

    @Override
    public int getConnectionMask() {
	if (this.ConMask >= 0) {
	    return this.ConMask;
	}
	this.ConMask = RedPowerLib.getConnections((IBlockAccess) this.world, this, this.x, this.y, this.z);
	if (this.EConMask < 0) {
	    this.EConMask = RedPowerLib.getExtConnections((IBlockAccess) this.world, this, this.x, this.y, this.z);
	}
	if (CoreProxy.isClient(this.world)) {
	    return this.ConMask;
	}
	this.cond.recache(this.ConMask, this.EConMask);
	return this.ConMask;
    }

    @Override
    public int getExtConnectionMask() {
	if (this.EConMask >= 0) {
	    return this.EConMask;
	}
	this.EConMask = RedPowerLib.getExtConnections((IBlockAccess) this.world, this, this.x, this.y, this.z);
	if (this.ConMask < 0) {
	    this.ConMask = RedPowerLib.getConnections((IBlockAccess) this.world, this, this.x, this.y, this.z);
	}
	if (CoreProxy.isClient(this.world)) {
	    return this.EConMask;
	}
	this.cond.recache(this.ConMask, this.EConMask);
	return this.EConMask;
    }

    @Override
    public boolean canUpdate() {
	return true;
    }

    @Override
    public void q_() {
	if (CoreProxy.isClient(this.world)) {
	    return;
	}
	if (this.ConMask < 0 || this.EConMask < 0) {
	    if (this.ConMask < 0) {
		this.ConMask = RedPowerLib.getConnections((IBlockAccess) this.world, this, this.x, this.y, this.z);
	    }
	    if (this.EConMask < 0) {
		this.EConMask = RedPowerLib.getExtConnections((IBlockAccess) this.world, this, this.x, this.y, this.z);
	    }
	    this.cond.recache(this.ConMask, this.EConMask);
	}
	this.cond.iterate();
	this.dirtyBlock();
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
	super.a(nBTTagCompound);
	this.cond.readFromNBT(nBTTagCompound);
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
	super.b(nBTTagCompound);
	this.cond.writeToNBT(nBTTagCompound);
    }

}
