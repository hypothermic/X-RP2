package eloraam.logic;

import eloraam.core.BlockCoverable;
import eloraam.core.CoreLib;
import eloraam.core.IRedPowerConnectable;
import eloraam.core.RedPowerLib;
import forge.IConnectRedstone;
import net.minecraft.server.IBlockAccess;

public class BlockLogic
        extends BlockCoverable
        implements IConnectRedstone {
    public BlockLogic(int n) {
        super(n, CoreLib.materialRedpower);
        this.a(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
        this.c(0.1f);
        this.a(0.625f);
    }

    public boolean canConnectRedstone(IBlockAccess iBlockAccess, int n, int n2, int n3, int n4) {
        if (n4 < 0) {
            return false;
        }
        IRedPowerConnectable iRedPowerConnectable = (IRedPowerConnectable) CoreLib.getTileEntity(iBlockAccess, n, n2, n3, IRedPowerConnectable.class);
        if (iRedPowerConnectable == null) {
            return false;
        }
        int n5 = RedPowerLib.mapLocalToRot(iRedPowerConnectable.getConnectableMask(), 2);
        return (n5 & 1 << n4) > 0;
    }

    public int getLightValue(IBlockAccess iBlockAccess, int n, int n2, int n3) {
        TileLogic tileLogic = (TileLogic) CoreLib.getTileEntity(iBlockAccess, n, n2, n3, TileLogic.class);
        if (tileLogic == null) {
            return super.getLightValue(iBlockAccess, n, n2, n3);
        }
        return tileLogic.getLightValue();
    }

    public boolean isPowerSource() {
        return true;
    }
}

