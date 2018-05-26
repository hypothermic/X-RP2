/* X-RP - decompiled with CFR */
package eloraam.control;

import eloraam.core.CoverLib;
import eloraam.wiring.MicroPlacementWire;
import java.util.ArrayList;
import net.minecraft.server.Block;
import net.minecraft.server.ItemStack;

public class MicroPlacementRibbon extends MicroPlacementWire {

    @Override
    public String getMicroName(int n, int n2) {
	if (n != 12 && n2 != 0) {
	    return null;
	}
	return "tile.ribbon";
    }

    @Override
    public void addCreativeItems(int n, ArrayList arrayList) {
	if (n == 12) {
	    arrayList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 3072));
	}
    }
}
