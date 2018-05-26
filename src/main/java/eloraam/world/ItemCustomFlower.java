/* X-RP - decompiled with CFR */
package eloraam.world;

import forge.ITextureProvider;
import net.minecraft.server.Item;
import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemStack;

public class ItemCustomFlower extends ItemBlock implements ITextureProvider {

    public ItemCustomFlower(int n) {
	super(n);
	this.setMaxDurability(0);
	this.a(true);
	this.setNoRepair();
    }

    public int getIconFromDamage(int n) {
	switch (n) {
	case 0: {
	    return 1;
	}
	case 1: {
	    return 2;
	}
	}
	return 1;
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
	    return "tile.indigo";
	}
	case 1: {
	    return "tile.rubbersapling";
	}
	}
	throw new IndexOutOfBoundsException();
    }

    public String getTextureFile() {
	return "/eloraam/world/worlditems1.png";
    }
}
