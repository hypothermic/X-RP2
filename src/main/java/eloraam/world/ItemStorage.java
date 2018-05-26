/* X-RP - decompiled with CFR */
package eloraam.world;

import net.minecraft.server.Item;
import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemStack;

public class ItemStorage extends ItemBlock {

    public ItemStorage(int n) {
	super(n);
	this.setMaxDurability(0);
	this.a(true);
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
	    return "tile.blockRuby";
	}
	case 1: {
	    return "tile.blockEmerald";
	}
	case 2: {
	    return "tile.blockSapphire";
	}
	}
	throw new IndexOutOfBoundsException();
    }
}
