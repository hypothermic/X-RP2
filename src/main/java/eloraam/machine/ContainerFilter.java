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

import eloraam.core.IHandleGuiEvent;
import eloraam.core.Packet212GuiEvent;
import net.minecraft.server.*;

import java.io.IOException;

public class ContainerFilter
        extends Container
        implements IHandleGuiEvent {
    private TileFilter tileFilter;
    public int color = 0;

    public ContainerFilter(IInventory iInventory, TileFilter tileFilter) {
        int n;
        int n2;
        this.tileFilter = tileFilter;
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 3; ++n) {
                this.a(new Slot((IInventory) tileFilter, n + n2 * 3, 62 + n * 18, 17 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 9; ++n) {
                this.a(new Slot(iInventory, n + n2 * 9 + 9, 8 + n * 18, 84 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.a(new Slot(iInventory, n2, 8 + n2 * 18, 142));
        }
        this.setPlayer(((PlayerInventory) iInventory).player);
    }

    public IInventory getInventory() {
        return this.tileFilter;
    }

    public boolean b(EntityHuman entityHuman) {
        return this.tileFilter.a(entityHuman);
    }

    public ItemStack a(int n) {
        ItemStack itemStack = null;
        Slot slot = (Slot) this.e.get(n);
        if (slot != null && slot.c()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.cloneItemStack();
            if (n < 9 ? !this.a(itemStack2, 9, 45, true) : !this.a(itemStack2, 0, 9, false)) {
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
            if (this.color == this.tileFilter.color) continue;
            iCrafting.setContainerData((Container) this, 0, (int) this.tileFilter.color);
        }
        this.color = this.tileFilter.color;
    }

    public void updateProgressBar(int n, int n2) {
        switch (n) {
            case 0: {
                this.tileFilter.color = (byte) n2;
            }
        }
    }

    @Override
    public void handleGuiEvent(Packet212GuiEvent packet212GuiEvent) {
        try {
            if (packet212GuiEvent.eventId != 1) {
                return;
            }
            this.tileFilter.color = (byte) packet212GuiEvent.getByte();
            this.tileFilter.dirtyBlock();
        } catch (IOException iOException) {
            // empty catch block
        }
    }
}

