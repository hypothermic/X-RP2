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
 *  net.minecraft.server.SlotResult2
 */
package eloraam.machine;

import eloraam.core.BluePowerEndpoint;
import eloraam.machine.TileBlueFurnace;
import java.util.List;
import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.Slot;
import net.minecraft.server.SlotResult2;

public class ContainerBlueFurnace
extends Container {
    public int cooktime = 0;
    private TileBlueFurnace tileFurnace;
    public int charge = 0;
    public int flow = 0;

    public ContainerBlueFurnace(PlayerInventory playerInventory, TileBlueFurnace tileBlueFurnace) {
        int n;
        this.tileFurnace = tileBlueFurnace;
        this.a(new Slot((IInventory)tileBlueFurnace, 0, 62, 35));
        this.a((Slot)new SlotResult2(playerInventory.player, (IInventory)tileBlueFurnace, 1, 126, 35));
        for (n = 0; n < 3; ++n) {
            for (int i = 0; i < 9; ++i) {
                this.a(new Slot((IInventory)playerInventory, i + n * 9 + 9, 8 + i * 18, 84 + n * 18));
            }
        }
        for (n = 0; n < 9; ++n) {
            this.a(new Slot((IInventory)playerInventory, n, 8 + n * 18, 142));
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
        Slot slot = (Slot)this.e.get(n);
        if (slot != null && slot.c()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.cloneItemStack();
            if (n < 2 ? !this.a(itemStack2, 2, 38, true) : !this.a(itemStack2, 0, 1, false)) {
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
            ICrafting iCrafting = (ICrafting)this.listeners.get(i);
            if (this.cooktime != this.tileFurnace.cooktime) {
                iCrafting.setContainerData((Container)this, 0, this.tileFurnace.cooktime);
            }
            if (this.charge != this.tileFurnace.cond.Charge) {
                iCrafting.setContainerData((Container)this, 1, this.tileFurnace.cond.Charge);
            }
            if (this.flow == this.tileFurnace.cond.Flow) continue;
            iCrafting.setContainerData((Container)this, 2, this.tileFurnace.cond.Flow & 65535);
            iCrafting.setContainerData((Container)this, 3, this.tileFurnace.cond.Flow >> 16 & 65535);
        }
        this.cooktime = this.tileFurnace.cooktime;
        this.charge = this.tileFurnace.cond.Charge;
        this.flow = this.tileFurnace.cond.Flow;
    }

    public void a(int n, int n2) {
        this.updateProgressBar(n, n2);
    }

    public void updateProgressBar(int n, int n2) {
        switch (n) {
            case 0: {
                this.tileFurnace.cooktime = n2;
                break;
            }
            case 1: {
                this.tileFurnace.cond.Charge = n2;
                break;
            }
            case 2: {
                this.tileFurnace.cond.Flow = this.tileFurnace.cond.Flow & -65536 | n2 & 65535;
                break;
            }
            case 3: {
                this.tileFurnace.cond.Flow = this.tileFurnace.cond.Flow & 65535 | (n2 & 65535) << 16;
            }
        }
    }
}

