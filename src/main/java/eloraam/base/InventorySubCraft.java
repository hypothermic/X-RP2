/* X-RP - decompiled with CFR */
package eloraam.base;

import net.minecraft.server.Container;
import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryCrafting;
import net.minecraft.server.ItemStack;

public class InventorySubCraft extends InventoryCrafting {

    private Container eventHandler;
    private IInventory parent;

    public InventorySubCraft(Container container, IInventory iInventory) {
	super(container, 3, 3);
	this.parent = iInventory;
	this.eventHandler = container;
    }

    public int getSize() {
	return 9;
    }

    public ItemStack getItem(int n) {
	if (n >= 9) {
	    return null;
	}
	return this.parent.getItem(n);
    }

    public ItemStack b(int n, int n2) {
	if (n < 0 || n >= 3) {
	    return null;
	}
	int n3 = n + n2 * 3;
	return this.getItem(n3);
    }

    public ItemStack splitStack(int n, int n2) {
	ItemStack itemStack = this.parent.splitStack(n, n2);
	if (itemStack != null) {
	    this.eventHandler.a((IInventory) this);
	}
	return itemStack;
    }

    public void setItem(int n, ItemStack itemStack) {
	this.parent.setItem(n, itemStack);
	this.eventHandler.a((IInventory) this);
    }
}
