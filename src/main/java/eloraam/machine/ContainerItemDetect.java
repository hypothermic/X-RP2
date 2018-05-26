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
import eloraam.machine.TileItemDetect;
import java.io.IOException;
import java.util.List;
import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.Slot;

public class ContainerItemDetect
extends Container
implements IHandleGuiEvent {
    private TileItemDetect tileDetect;
    byte mode;

    public ContainerItemDetect(IInventory iInventory, TileItemDetect tileItemDetect) {
        int n;
        int n2;
        this.tileDetect = tileItemDetect;
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 3; ++n) {
                this.a(new Slot((IInventory)tileItemDetect, n + n2 * 3, 62 + n * 18, 17 + n2 * 18));
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
        this.setPlayer(((PlayerInventory)iInventory).player);
    }

    public IInventory getInventory() {
        return this.tileDetect;
    }

    public boolean b(EntityHuman entityHuman) {
        return this.tileDetect.a(entityHuman);
    }

    public ItemStack a(int n) {
        ItemStack itemStack = null;
        Slot slot = (Slot)this.e.get(n);
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
            ICrafting iCrafting = (ICrafting)this.listeners.get(i);
            if (this.mode == this.tileDetect.mode) continue;
            iCrafting.setContainerData((Container)this, 0, (int)this.tileDetect.mode);
        }
        this.mode = this.tileDetect.mode;
    }

    public void a(int n, int n2) {
        this.updateProgressBar(n, n2);
    }

    public void updateProgressBar(int n, int n2) {
        if (n == 0) {
            this.tileDetect.mode = (byte)n2;
        }
    }

    @Override
    public void handleGuiEvent(Packet212GuiEvent packet212GuiEvent) {
        if (packet212GuiEvent.eventId != 1) {
            return;
        }
        try {
            this.tileDetect.mode = (byte)packet212GuiEvent.getByte();
            this.tileDetect.dirtyBlock();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.a();
    }
}

