/* X-RP - decompiled with CFR */
package eloraam.base;

import eloraam.base.SlotAlloyFurnace;
import eloraam.base.TileAlloyFurnace;
import java.util.List;
import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.Slot;

public class ContainerAlloyFurnace extends Container {

    private TileAlloyFurnace tileFurnace;
    public int totalburn = 0;
    public int burntime = 0;
    public int cooktime = 0;

    public ContainerAlloyFurnace(PlayerInventory playerInventory, TileAlloyFurnace tileAlloyFurnace) {
	int n;
	int n2;
	this.tileFurnace = tileAlloyFurnace;
	for (n2 = 0; n2 < 3; ++n2) {
	    for (n = 0; n < 3; ++n) {
		this.a(new Slot((IInventory) tileAlloyFurnace, n + n2 * 3, 48 + n * 18, 17 + n2 * 18));
	    }
	}
	this.a(new Slot((IInventory) tileAlloyFurnace, 9, 17, 42));
	this.a((Slot) new SlotAlloyFurnace(playerInventory.player, tileAlloyFurnace, 10, 141, 35));
	for (n2 = 0; n2 < 3; ++n2) {
	    for (n = 0; n < 9; ++n) {
		this.a(new Slot((IInventory) playerInventory, n + n2 * 9 + 9, 8 + n * 18, 84 + n2 * 18));
	    }
	}
	for (n2 = 0; n2 < 9; ++n2) {
	    this.a(new Slot((IInventory) playerInventory, n2, 8 + n2 * 18, 142));
	}
	this.setPlayer(playerInventory.player);
    }

    public IInventory getInventory() {
	return this.tileFurnace;
    }

    public boolean b(EntityHuman entityHuman) {
	return this.tileFurnace.a(entityHuman);
    }

    public ItemStack a(int n) {
	ItemStack itemStack = null;
	Slot slot = (Slot) this.e.get(n);
	if (slot != null && slot.c()) {
	    ItemStack itemStack2 = slot.getItem();
	    itemStack = itemStack2.cloneItemStack();
	    if (n < 11 ? !this.a(itemStack2, 11, 47, true) : !this.a(itemStack2, 0, 9, false)) {
		return null;
	    }
	    if (itemStack2.count == 0) {
		slot.set(null);
	    } else {
		slot.d();
	    }
	    if (itemStack2.count != itemStack.count) {
		slot.c(itemStack2);
	    } else {
		return null;
	    }
	}
	return itemStack;
    }

    public void a() {
	super.a();
	for (int i = 0; i < this.listeners.size(); ++i) {
	    ICrafting iCrafting = (ICrafting) this.listeners.get(i);
	    if (this.totalburn != this.tileFurnace.totalburn) {
		iCrafting.setContainerData((Container) this, 0, this.tileFurnace.totalburn);
	    }
	    if (this.burntime != this.tileFurnace.burntime) {
		iCrafting.setContainerData((Container) this, 1, this.tileFurnace.burntime);
	    }
	    if (this.cooktime == this.tileFurnace.burntime)
		continue;
	    iCrafting.setContainerData((Container) this, 2, this.tileFurnace.cooktime);
	}
	this.totalburn = this.tileFurnace.totalburn;
	this.cooktime = this.tileFurnace.cooktime;
	this.burntime = this.tileFurnace.burntime;
    }

    public void a(int n, int n2) {
	this.updateProgressBar(n, n2);
    }

    public void updateProgressBar(int n, int n2) {
	switch (n) {
	case 0: {
	    this.tileFurnace.totalburn = n2;
	    break;
	}
	case 1: {
	    this.tileFurnace.burntime = n2;
	    break;
	}
	case 2: {
	    this.tileFurnace.cooktime = n2;
	}
	}
    }
}
