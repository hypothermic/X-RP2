/* X-RP - decompiled with CFR */
package eloraam.world;

import net.minecraft.server.Item;
import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemStack;

public class ItemCustomStone extends ItemBlock {

    public ItemCustomStone(int n) {
	super(n);
	this.setMaxDurability(0);
	this.a(true);
	this.setNoRepair();
    }

    public int getPlacedBlockMetadata(int n) {
	return n;
    }

    public int filterData(int n) {
	return n;
    }

    public String a(ItemStack itemStack) {
	switch (itemStack.getData()) {
	case 0: {
	    return "tile.marble";
	}
	case 1: {
	    return "tile.basalt";
	}
	case 2: {
	    return "tile.marbleBrick";
	}
	case 3: {
	    return "tile.basaltCobble";
	}
	case 4: {
	    return "tile.basaltBrick";
	}
	}
	throw new IndexOutOfBoundsException();
    }
}
