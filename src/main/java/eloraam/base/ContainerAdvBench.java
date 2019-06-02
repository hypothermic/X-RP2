/* X-RP - decompiled with CFR */
package eloraam.base;

import eloraam.core.CoreLib;
import net.minecraft.server.*;

public class ContainerAdvBench extends Container {

    SlotCraftRefill slotCraft;
    private TileAdvBench tileAdvBench;
    public InventorySubCraft craftMatrix;
    public IInventory craftResult;

    public ContainerAdvBench(PlayerInventory playerInventory, TileAdvBench tileAdvBench) {
        int n;
        int n2;
        this.tileAdvBench = tileAdvBench;
        this.craftMatrix = new InventorySubCraft(this, tileAdvBench);
        this.craftMatrix.resultInventory = this.craftResult = new InventoryCraftResult();
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 3; ++n) {
                this.a(new Slot((IInventory) this.craftMatrix, n + n2 * 3, 30 + n * 18, 18 + n2 * 18));
            }
        }
        this.slotCraft = new SlotCraftRefill(playerInventory.player, (IInventory) this.craftMatrix, this.craftResult, tileAdvBench, this, 0, 124, 36);
        this.a((Slot) this.slotCraft);
        for (n2 = 0; n2 < 2; ++n2) {
            for (n = 0; n < 9; ++n) {
                this.a(new Slot((IInventory) tileAdvBench, n + n2 * 9 + 9, 8 + n * 18, 90 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 9; ++n) {
                this.a(new Slot((IInventory) playerInventory, n + n2 * 9 + 9, 8 + n * 18, 140 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.a(new Slot((IInventory) playerInventory, n2, 8 + n2 * 18, 198));
        }
        this.a((IInventory) this.craftMatrix);
        this.setPlayer(playerInventory.player);
    }

    public IInventory getInventory() {
        return this.tileAdvBench;
    }

    public boolean b(EntityHuman entityHuman) {
        return this.tileAdvBench.a(entityHuman);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void a(IInventory iInventory) {
        TileAdvBench tileAdvBench = this.tileAdvBench;
        synchronized (tileAdvBench) {
            this.craftResult.setItem(0, CraftingManager.getInstance().craft((InventoryCrafting) this.craftMatrix));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ItemStack a(int n) {
        TileAdvBench tileAdvBench = this.tileAdvBench;
        synchronized (tileAdvBench) {
            ItemStack itemStack = null;
            Slot slot = (Slot) this.e.get(n);
            if (slot != null && slot.c()) {
                ItemStack itemStack2 = slot.getItem();
                itemStack = itemStack2.cloneItemStack();
                if (n == 9) {
                    this.mergeCrafting(slot, 28, 64);
                    return null;
                }
                if (n < 9) {
                    if (!this.a(itemStack2, 10, 28, false)) {
                        return null;
                    }
                } else if (n < 28) {
                    if (!this.a(itemStack2, 28, 64, true)) {
                        return null;
                    }
                } else if (!this.a(itemStack2, 10, 28, false)) {
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
    }

    protected boolean canFit(ItemStack itemStack, int n, int n2) {
        int n3 = 0;
        for (int i = n; i < n2; ++i) {
            Slot slot = (Slot) this.e.get(i);
            ItemStack itemStack2 = slot.getItem();
            if (itemStack2 == null) {
                return true;
            }
            if (CoreLib.compareItemStack(itemStack2, itemStack) != 0 || (n3 += itemStack2.getMaxStackSize() - itemStack2.count) < itemStack.count)
                continue;
            return true;
        }
        return false;
    }

    protected void fitItem(ItemStack itemStack, int n, int n2) {
        ItemStack itemStack2;
        int n3;
        Slot slot;
        if (itemStack.isStackable()) {
            for (n3 = n; n3 < n2; ++n3) {
                int n4;
                slot = (Slot) this.e.get(n3);
                itemStack2 = slot.getItem();
                if (itemStack2 == null || CoreLib.compareItemStack(itemStack2, itemStack) != 0 || (n4 = Math.min(itemStack.count, itemStack.getMaxStackSize() - itemStack2.count)) == 0)
                    continue;
                itemStack.count -= n4;
                itemStack2.count += n4;
                slot.d();
                if (itemStack.count != 0)
                    continue;
                return;
            }
        }
        for (n3 = n; n3 < n2; ++n3) {
            slot = (Slot) this.e.get(n3);
            itemStack2 = slot.getItem();
            if (itemStack2 != null)
                continue;
            slot.set(itemStack);
            slot.d();
            return;
        }
    }

    protected void mergeCrafting(Slot slot, int n, int n2) {
        int n3 = 0;
        ItemStack itemStack = slot.getItem();
        if (itemStack == null || itemStack.count == 0) {
            return;
        }
        ItemStack itemStack2 = itemStack.cloneItemStack();
        int n4 = itemStack2.getMaxStackSize();
        if (n4 == 1) {
            n4 = 16;
        }
        do {
            if (!this.canFit(itemStack, n, n2)) {
                return;
            }
            this.fitItem(itemStack, n, n2);
            slot.c(itemStack);
            if ((n3 += itemStack.count) >= n4) {
                return;
            }
            if (this.slotCraft.isLastUse()) {
                return;
            }
            itemStack = slot.getItem();
            if (itemStack != null && itemStack.count != 0)
                continue;
            return;
        } while (CoreLib.compareItemStack(itemStack, itemStack2) == 0);
    }
}
