/* X-RP - decompiled with CFR */
package eloraam.lighting;

import eloraam.core.CoreLib;
import net.minecraft.server.Item;
import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemStack;

public class ItemLamp extends ItemBlock {

    public ItemLamp(int n) {
	super(n);
	this.setMaxDurability(0);
	this.a(true);
    }

    public int filterData(int n) {
	return n;
    }

    public int getPlacedBlockMetadata(int n) {
	return n;
    }

    public String a(ItemStack itemStack) {
	return "tile.rplamp." + CoreLib.rawColorNames[itemStack.getData()];
    }
}
