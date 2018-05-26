/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.CoreLib;
import eloraam.core.IRedbusConnectable;
import eloraam.core.IWiring;
import eloraam.core.WirePathfinder;
import eloraam.core.WorldCoord;
import net.minecraft.server.IBlockAccess;

public class RedbusLib {

    public static IRedbusConnectable getAddr(IBlockAccess iBlockAccess, WorldCoord worldCoord, int n) {
	RedbusPathfinder redbusPathfinder = new RedbusPathfinder(iBlockAccess, n);
	redbusPathfinder.addSearchBlocks(worldCoord, 16777215, 0);
	while (redbusPathfinder.iterate()) {
	}
	return redbusPathfinder.result;
    }

    private static class RedbusPathfinder extends WirePathfinder {

	public IRedbusConnectable result = null;
	IBlockAccess iba;
	int addr;

	@Override
	public boolean step(WorldCoord worldCoord) {
	    IRedbusConnectable iRedbusConnectable = (IRedbusConnectable) CoreLib.getTileEntity(this.iba, worldCoord, IRedbusConnectable.class);
	    if (iRedbusConnectable != null && iRedbusConnectable.rbGetAddr() == this.addr) {
		this.result = iRedbusConnectable;
		return false;
	    }
	    IWiring iWiring = (IWiring) CoreLib.getTileEntity(this.iba, worldCoord, IWiring.class);
	    if (iWiring == null) {
		return true;
	    }
	    this.addSearchBlocks(worldCoord, iWiring.getConnectionMask(), iWiring.getExtConnectionMask());
	    return true;
	}

	public RedbusPathfinder(IBlockAccess iBlockAccess, int n) {
	    this.iba = iBlockAccess;
	    this.addr = n;
	    this.init();
	}
    }

}
