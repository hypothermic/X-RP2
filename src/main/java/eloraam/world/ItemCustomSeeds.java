/* X-RP - decompiled with CFR */
package eloraam.world;

import eloraam.world.BlockCustomCrops;
import forge.ITextureProvider;
import java.util.ArrayList;
import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.RedPowerWorld;
import net.minecraft.server.World;

public class ItemCustomSeeds extends Item implements ITextureProvider {

    public ItemCustomSeeds(int n) {
	super(n);
	this.setMaxDurability(0);
	this.a(true);
    }

    public int getIconFromDamage(int n) {
	switch (n) {
	case 0: {
	    return 3;
	}
	}
	return 0;
    }

    public boolean interactWith(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
	if (n4 != 1) {
	    return false;
	}
	int n5 = world.getTypeId(n, n2, n3);
	if (n5 != Block.SOIL.id || !world.isEmpty(n, n2 + 1, n3) || !world.isEmpty(n, n2 + 2, n3) || world.getData(n, n2, n3) == 0) {
	    return false;
	}
	world.setTypeIdAndData(n, n2 + 1, n3, RedPowerWorld.blockCrops.id, 0);
	--itemStack.count;
	return true;
    }

    public String a(ItemStack itemStack) {
	switch (itemStack.getData()) {
	case 0: {
	    return "item.seedFlax";
	}
	}
	throw new IndexOutOfBoundsException();
    }

    public void addCreativeItems(ArrayList arrayList) {
	for (int i = 0; i <= 0; ++i) {
	    arrayList.add(new ItemStack((Item) this, 1, i));
	}
    }

    public String getTextureFile() {
	return "/eloraam/world/worlditems1.png";
    }
}
