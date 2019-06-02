/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Container
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.IInventory
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.PlayerInventory
 *  net.minecraft.server.Slot
 */
package eloraam.machine;

import net.minecraft.server.*;

public class ContainerBufferChest
        extends Container {
    private TileBufferChest tileBuffer;

    public ContainerBufferChest(IInventory iInventory, TileBufferChest tileBufferChest) {
        int n;
        int n2;
        this.tileBuffer = tileBufferChest;
        for (n2 = 0; n2 < 5; ++n2) {
            for (n = 0; n < 4; ++n) {
                this.a(new Slot((IInventory) tileBufferChest, n + n2 * 4, 44 + n2 * 18, 18 + n * 18));
            }
        }
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 9; ++n) {
                this.a(new Slot(iInventory, n + n2 * 9 + 9, 8 + n * 18, 104 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.a(new Slot(iInventory, n2, 8 + n2 * 18, 162));
        }
        this.setPlayer(((PlayerInventory) iInventory).player);
    }

    public IInventory getInventory() {
        return this.tileBuffer;
    }

    public boolean b(EntityHuman entityHuman) {
        return this.tileBuffer.a(entityHuman);
    }

    public ItemStack a(int n) {
        ItemStack itemStack = null;
        Slot slot = (Slot) this.e.get(n);
        if (slot != null && slot.c()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.cloneItemStack();
            if (n < 20 ? !this.a(itemStack2, 20, 56, true) : !this.a(itemStack2, 0, 20, false)) {
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

