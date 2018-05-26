/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.base.ItemHandsaw;
import eloraam.core.CoverLib;
import net.minecraft.server.Block;
import net.minecraft.server.CraftingRecipe;
import net.minecraft.server.InventoryCrafting;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;

public class CoverRecipe implements CraftingRecipe {

    private static ItemStack newCover(int n, int n2, int n3) {
	return new ItemStack(CoverLib.blockCoverPlate, n, n2 << 8 | n3);
    }

    private ItemStack getSawRecipe(InventoryCrafting inventoryCrafting, ItemStack itemStack, int n, ItemStack itemStack2, int n2) {
	Object object;
	int n3 = n & 15;
	int n4 = n >> 4;
	int n5 = n2 & 15;
	int n6 = n2 >> 4;
	int n7 = -1;
	int n8 = -1;
	if (itemStack2.id == CoverLib.blockCoverPlate.id) {
	    n8 = itemStack2.getData();
	    n7 = n8 & 255;
	    n8 >>= 8;
	} else {
	    object = CoverLib.getMaterial(itemStack2);
	    if (object == null) {
		return null;
	    }
	// X-RP: start, code may be unstable past this point
	    n7 = ((Integer) object).intValue();
	}
	object = (ItemHandsaw) itemStack.getItem();
	if (((ItemHandsaw) object).getSharpness() < CoverLib.getHardness(n7)) {
	    return null;
	}
	// X-RP: end.
	if (n3 == n5 && (n4 == n6 + 1 || n4 == n6 - 1)) {
	    switch (n8) {
	    case -1: {
		return CoverRecipe.newCover(2, 17, n7);
	    }
	    case 17: {
		return CoverRecipe.newCover(2, 16, n7);
	    }
	    case 16: {
		return CoverRecipe.newCover(2, 0, n7);
	    }
	    case 29: {
		return CoverRecipe.newCover(2, 27, n7);
	    }
	    case 26: {
		return CoverRecipe.newCover(2, 25, n7);
	    }
	    case 25: {
		return CoverRecipe.newCover(2, 24, n7);
	    }
	    case 33: {
		return CoverRecipe.newCover(2, 31, n7);
	    }
	    }
	    return null;
	}
	if (n4 == n6 && (n3 == n5 + 1 || n3 == n5 - 1)) {
	    switch (n8) {
	    case 17: {
		return CoverRecipe.newCover(2, 23, n7);
	    }
	    case 16: {
		return CoverRecipe.newCover(2, 22, n7);
	    }
	    case 0: {
		return CoverRecipe.newCover(2, 21, n7);
	    }
	    case 23: {
		return CoverRecipe.newCover(2, 20, n7);
	    }
	    case 22: {
		return CoverRecipe.newCover(2, 19, n7);
	    }
	    case 21: {
		return CoverRecipe.newCover(2, 18, n7);
	    }
	    case 27: {
		return CoverRecipe.newCover(2, 39, n7);
	    }
	    case 28: {
		return CoverRecipe.newCover(2, 40, n7);
	    }
	    case 29: {
		return CoverRecipe.newCover(2, 41, n7);
	    }
	    case 30: {
		return CoverRecipe.newCover(2, 42, n7);
	    }
	    case 39: {
		return CoverRecipe.newCover(2, 35, n7);
	    }
	    case 40: {
		return CoverRecipe.newCover(2, 36, n7);
	    }
	    case 41: {
		return CoverRecipe.newCover(2, 37, n7);
	    }
	    case 42: {
		return CoverRecipe.newCover(2, 38, n7);
	    }
	    }
	    return null;
	}
	return null;
    }

    private ItemStack getColumnRecipe(ItemStack itemStack) {
	if (itemStack.id != CoverLib.blockCoverPlate.id) {
	    return null;
	}
	int n = itemStack.getData();
	int n2 = n & 255;
	switch (n >>= 8) {
	case 22: {
	    return CoverRecipe.newCover(1, 43, n2);
	}
	case 43: {
	    return CoverRecipe.newCover(1, 22, n2);
	}
	case 23: {
	    return CoverRecipe.newCover(1, 44, n2);
	}
	case 44: {
	    return CoverRecipe.newCover(1, 23, n2);
	}
	case 41: {
	    return CoverRecipe.newCover(1, 45, n2);
	}
	case 45: {
	    return CoverRecipe.newCover(1, 41, n2);
	}
	}
	return null;
    }

    private ItemStack getMergeRecipe(int n, int n2, int n3) {
	int n4 = n >> 20;
	n &= 255;
	switch (n4) {
	default: {
	    break;
	}
	case 0: {
	    switch (n2) {
	    case 2: {
		return CoverRecipe.newCover(1, 16, n);
	    }
	    case 3: {
		return CoverRecipe.newCover(1, 27, n);
	    }
	    case 4: {
		return CoverRecipe.newCover(1, 17, n);
	    }
	    case 5: {
		return CoverRecipe.newCover(1, 28, n);
	    }
	    case 6: {
		return CoverRecipe.newCover(1, 29, n);
	    }
	    case 7: {
		return CoverRecipe.newCover(1, 30, n);
	    }
	    case 8: {
		return CoverLib.getItemStack(n);
	    }
	    }
	    break;
	}
	case 1: {
	    switch (n2) {
	    case 2: {
		return CoverRecipe.newCover(1, 25, n);
	    }
	    case 3: {
		return CoverRecipe.newCover(1, 31, n);
	    }
	    case 4: {
		return CoverRecipe.newCover(1, 26, n);
	    }
	    case 5: {
		return CoverRecipe.newCover(1, 32, n);
	    }
	    case 6: {
		return CoverRecipe.newCover(1, 33, n);
	    }
	    case 7: {
		return CoverRecipe.newCover(1, 34, n);
	    }
	    case 8: {
		return CoverLib.getItemStack(n);
	    }
	    }
	    break;
	}
	case 16: {
	    switch (n2) {
	    case 2: {
		return CoverRecipe.newCover(1, 0, n);
	    }
	    case 4: {
		return CoverRecipe.newCover(1, 16, n);
	    }
	    case 8: {
		return CoverRecipe.newCover(1, 17, n);
	    }
	    case 16: {
		return CoverLib.getItemStack(n);
	    }
	    }
	    break;
	}
	case 32: {
	    if (n3 == 2) {
		switch (n2) {
		case 2: {
		    return CoverRecipe.newCover(1, 21, n);
		}
		case 4: {
		    return CoverRecipe.newCover(1, 22, n);
		}
		case 8: {
		    return CoverRecipe.newCover(1, 23, n);
		}
		}
		break;
	    }
	    switch (n2) {
	    case 4: {
		return CoverRecipe.newCover(1, 0, n);
	    }
	    case 8: {
		return CoverRecipe.newCover(1, 16, n);
	    }
	    case 16: {
		return CoverRecipe.newCover(1, 17, n);
	    }
	    case 32: {
		return CoverLib.getItemStack(n);
	    }
	    }
	}
	}
	return null;
    }

    private ItemStack getHollowRecipe(int n) {
	int n2 = n >> 8 & 255;
	n &= 255;
	switch (n2) {
	case 0: {
	    return CoverRecipe.newCover(8, 24, n);
	}
	case 16: {
	    return CoverRecipe.newCover(8, 25, n);
	}
	case 17: {
	    return CoverRecipe.newCover(8, 26, n);
	}
	case 27: {
	    return CoverRecipe.newCover(8, 31, n);
	}
	case 28: {
	    return CoverRecipe.newCover(8, 32, n);
	}
	case 29: {
	    return CoverRecipe.newCover(8, 33, n);
	}
	case 30: {
	    return CoverRecipe.newCover(8, 34, n);
	}
	}
	return null;
    }

    private int getMicroClass(ItemStack itemStack) {
	if (itemStack.id != CoverLib.blockCoverPlate.id) {
	    return -1;
	}
	int n = itemStack.getData();
	return CoverLib.damageToCoverData(n);
    }

    private ItemStack findResult(InventoryCrafting inventoryCrafting) {
	ItemStack itemStack = null;
	ItemStack itemStack2 = null;
	boolean bl = false;
	boolean bl2 = true;
	boolean bl3 = true;
	int n = 0;
	int n2 = 0;
	int n3 = -1;
	int n4 = 0;
	int n5 = 0;
	for (int i = 0; i < 3; ++i) {
	    for (int j = 0; j < 3; ++j) {
		ItemStack itemStack3 = inventoryCrafting.b(i, j);
		if (itemStack3 == null)
		    continue;
		if (itemStack3.getItem() instanceof ItemHandsaw) {
		    if (itemStack != null) {
			bl = true;
			continue;
		    }
		    itemStack = itemStack3;
		    n = i + j * 16;
		    continue;
		}
		if (itemStack2 == null) {
		    itemStack2 = itemStack3;
		    n2 = i + j * 16;
		    n3 = this.getMicroClass(itemStack3);
		    if (n3 >= 0) {
			n4 += n3 >> 16 & 15;
		    } else {
			bl2 = false;
		    }
		    n5 = 1;
		    continue;
		}
		bl = true;
		if (!bl2)
		    continue;
		int n6 = this.getMicroClass(itemStack3);
		if (((n6 ^ n3) & -1048321) != 0) {
		    bl2 = false;
		    continue;
		}
		if (n6 != n3) {
		    bl3 = false;
		}
		n4 += n6 >> 16 & 15;
		++n5;
	    }
	}
	if (itemStack != null && itemStack2 != null && !bl) {
	    return this.getSawRecipe(inventoryCrafting, itemStack, n, itemStack2, n2);
	}
	if (itemStack == null && itemStack2 != null && !bl) {
	    return this.getColumnRecipe(itemStack2);
	}
	if (bl2 && bl && itemStack == null) {
	    if (n5 == 8 && bl3 && inventoryCrafting.b(1, 1) == null && n3 >> 20 == 0) {
		return this.getHollowRecipe(n3);
	    }
	    return this.getMergeRecipe(n3, n4, n5);
	}
	return null;
    }

    public boolean a(InventoryCrafting inventoryCrafting) {
	return this.findResult(inventoryCrafting) != null;
    }

    public int a() {
	return 9;
    }

    public ItemStack b(InventoryCrafting inventoryCrafting) {
	return this.findResult(inventoryCrafting).cloneItemStack();
    }

    public ItemStack b() {
	return new ItemStack(CoverLib.blockCoverPlate, 1, 0);
    }

    public Recipe toBukkitRecipe() {
	return null;
    }
}
