/* X-RP - decompiled with CFR */
package eloraam.base;

import eloraam.core.CoreLib;
import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.SlotResult;

public class SlotCraftRefill extends SlotResult {

    IInventory allSlots;
    IInventory craftingMatrix;
    Container eventHandler;

    public SlotCraftRefill(EntityHuman entityHuman, IInventory iInventory, IInventory iInventory2, IInventory iInventory3, Container container, int n, int n2, int n3) {
	super(entityHuman, iInventory, iInventory2, n, n2, n3);
	this.allSlots = iInventory3;
	this.craftingMatrix = iInventory;
	this.eventHandler = container;
    }

    private int findMatch(ItemStack itemStack) {
	for (int i = 0; i < 18; ++i) {
	    ItemStack itemStack2 = this.allSlots.getItem(9 + i);
	    if (itemStack2 == null || itemStack2.count == 0 || CoreLib.compareItemStack(itemStack, itemStack2) != 0)
		continue;
	    return 9 + i;
	}
	return -1;
    }

    public boolean isLastUse() {
	int n;
	ItemStack itemStack;
	int n2 = 0;
	for (n = 0; n < 9; ++n) {
	    itemStack = this.allSlots.getItem(n);
	    if (itemStack == null) {
		n2 |= 1 << n;
		continue;
	    }
	    if (!itemStack.isStackable()) {
		n2 |= 1 << n;
		continue;
	    }
	    if (itemStack.count <= 1)
		continue;
	    n2 |= 1 << n;
	}
	if (n2 == 511) {
	    return false;
	}
	block1: for (n = 0; n < 18; ++n) {
	    itemStack = this.allSlots.getItem(9 + n);
	    if (itemStack == null || itemStack.count == 0)
		continue;
	    int n3 = itemStack.count;
	    for (int i = 0; i < 9; ++i) {
		ItemStack itemStack2;
		if ((n2 & 1 << i) > 0 || (itemStack2 = this.allSlots.getItem(i)) == null || CoreLib.compareItemStack(itemStack2, itemStack) != 0)
		    continue;
		n2 |= 1 << i;
		if (--n3 == 0)
		    continue block1;
	    }
	}
	return n2 != 511;
    }

    public void c(ItemStack itemStack) {
	ItemStack[] arritemStack = new ItemStack[9];
	for (int n = 0; n < 9; ++n) {
	    ItemStack itemStack2 = this.allSlots.getItem(n);
	    arritemStack[n] = itemStack2 == null ? null : itemStack2.cloneItemStack();
	}
	boolean n = this.isLastUse();
	super.c(itemStack);
	if (n) {
	    return;
	}
	boolean bl = false;
	for (int i = 0; i < 9; ++i) {
	    if (arritemStack[i] == null)
		continue;
	    ItemStack itemStack3 = this.allSlots.getItem(i);
	    if (itemStack3 != null) {
		Item item;
		int n2;
		if (CoreLib.compareItemStack(itemStack3, arritemStack[i]) == 0 || (item = arritemStack[i].getItem().j()) == null || item.id != itemStack3.getItem().id || (n2 = this.findMatch(arritemStack[i])) < 0)
		    continue;
		ItemStack itemStack4 = this.allSlots.getItem(n2);
		this.allSlots.setItem(n2, itemStack3);
		this.allSlots.setItem(i, itemStack4);
		bl = true;
		continue;
	    }
	    int n3 = this.findMatch(arritemStack[i]);
	    if (n3 < 0)
		continue;
	    ItemStack itemStack5 = this.allSlots.getItem(n3);
	    this.allSlots.setItem(i, this.allSlots.splitStack(n3, 1));
	    bl = true;
	}
	if (bl) {
	    this.eventHandler.a(this.craftingMatrix);
	}
    }
}
