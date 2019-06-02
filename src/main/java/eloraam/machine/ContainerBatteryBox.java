/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Container
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.ICrafting
 *  net.minecraft.server.IInventory
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.PlayerInventory
 *  net.minecraft.server.Slot
 */
package eloraam.machine;

import net.minecraft.server.*;

public class ContainerBatteryBox
        extends Container {
    private TileBatteryBox tileBB;
    public int charge;
    public int storage;

    public ContainerBatteryBox(IInventory iInventory, TileBatteryBox tileBatteryBox) {
        int n;
        this.tileBB = tileBatteryBox;
        this.a(new Slot((IInventory) tileBatteryBox, 0, 120, 27));
        this.a(new Slot((IInventory) tileBatteryBox, 1, 120, 55));
        for (n = 0; n < 3; ++n) {
            for (int i = 0; i < 9; ++i) {
                this.a(new Slot(iInventory, i + n * 9 + 9, 8 + i * 18, 88 + n * 18));
            }
        }
        for (n = 0; n < 9; ++n) {
            this.a(new Slot(iInventory, n, 8 + n * 18, 146));
        }
        this.setPlayer(((PlayerInventory) iInventory).player);
    }

    public IInventory getInventory() {
        return this.tileBB;
    }

    public boolean b(EntityHuman entityHuman) {
        return this.tileBB.a(entityHuman);
    }

    public ItemStack a(int n) {
        ItemStack itemStack = null;
        Slot slot = (Slot) this.e.get(n);
        if (slot != null && slot.c()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.cloneItemStack();
            if (n < 2) {
                if (!this.a(itemStack2, 2, 38, true)) {
                    return null;
                }
            } else {
                Slot slot2 = (Slot) this.e.get(0);
                ItemStack itemStack3 = slot2.getItem();
                if (itemStack3 != null && itemStack3.count != 0) {
                    return null;
                }
                slot2.set(itemStack2.a(1));
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
            if (this.charge != this.tileBB.Charge) {
                iCrafting.setContainerData((Container) this, 0, this.tileBB.Charge);
            }
            if (this.storage == this.tileBB.Storage) continue;
            iCrafting.setContainerData((Container) this, 1, this.tileBB.Storage);
        }
        this.charge = this.tileBB.Charge;
        this.storage = this.tileBB.Storage;
    }

    public void updateProgressBar(int n, int n2) {
        switch (n) {
            case 0: {
                this.tileBB.Charge = n2;
                break;
            }
            case 1: {
                this.tileBB.Storage = n2;
            }
        }
    }
}

