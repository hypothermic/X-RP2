/* X-RP - decompiled with CFR */
// Classified as "wonky method", see comments in code.
package eloraam.base;

import eloraam.core.BlockCoverable;
import eloraam.core.CoreLib;
import eloraam.core.IMicroPlacement;
import eloraam.core.RedPowerLib;
import forge.IConnectRedstone;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Item;

public class BlockMicro extends BlockCoverable implements IConnectRedstone {

    public BlockMicro(int n) {
        super(n, CoreLib.materialRedpower);
        this.c(0.1f);
    }

    public boolean isPowerSource() {
        return !RedPowerLib.isSearching();
    }

    public boolean canConnectRedstone(IBlockAccess iBlockAccess, int n, int n2, int n3, int n4) {
        if (RedPowerLib.isSearching()) {
            return false;
        }
        int n5 = iBlockAccess.getData(n, n2, n3);
        return n5 == 1 || n5 == 2;
    }

    public void registerPlacement(int n, IMicroPlacement iMicroPlacement) {
        ((ItemMicro) Item.byId[this.id]).registerPlacement(n, iMicroPlacement);
    }
}
