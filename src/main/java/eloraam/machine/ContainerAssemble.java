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

public class ContainerAssemble
        extends Container
        implements IHandleGuiEvent {
    private TileAssemble tileAssemble;
    public int mode = 0;
    public int select = 0;
    public int skipSlots = 0;

    public ContainerAssemble(IInventory iInventory, TileAssemble tileAssemble) {
        int n;
        int n2;
        this.tileAssemble = tileAssemble;
        for (n2 = 0; n2 < 2; ++n2) {
            for (n = 0; n < 8; ++n) {
                this.a(new Slot((IInventory) tileAssemble, n + n2 * 8, 8 + n * 18, 18 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 2; ++n2) {
            for (n = 0; n < 9; ++n) {
                this.a(new Slot((IInventory) tileAssemble, n + n2 * 9 + 16, 8 + n * 18, 63 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 9; ++n) {
                this.a(new Slot(iInventory, n + n2 * 9 + 9, 8 + n * 18, 113 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.a(new Slot(iInventory, n2, 8 + n2 * 18, 171));
        }
        this.setPlayer(((PlayerInventory) iInventory).player);
    }

    public IInventory getInventory() {
        return this.tileAssemble;
    }

    public boolean b(EntityHuman entityHuman) {
        return this.tileAssemble.a(entityHuman);
    }

    public ItemStack a(int n) {
        ItemStack itemStack = null;
        Slot slot = (Slot) this.e.get(n);
        if (slot != null && slot.c()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.cloneItemStack();
            if (n < 34 ? !this.a(itemStack2, 34, 70, true) : !this.a(itemStack2, 16, 34, false)) {
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
            if (this.mode != this.tileAssemble.mode) {
                iCrafting.setContainerData((Container) this, 0, (int) this.tileAssemble.mode);
            }
            if (this.select != this.tileAssemble.select) {
                iCrafting.setContainerData((Container) this, 1, (int) this.tileAssemble.select);
            }
            if (this.skipSlots == this.tileAssemble.skipSlots) continue;
            iCrafting.setContainerData((Container) this, 2, this.tileAssemble.skipSlots);
        }
        this.mode = this.tileAssemble.mode;
        this.select = this.tileAssemble.select;
        this.skipSlots = this.tileAssemble.skipSlots;
    }

    public void updateProgressBar(int n, int n2) {
        switch (n) {
            case 0: {
                this.tileAssemble.mode = (byte) n2;
                break;
            }
            case 1: {
                this.tileAssemble.select = (byte) n2;
                break;
            }
            case 2: {
                this.tileAssemble.skipSlots = n2 & 65535;
            }
        }
    }

    @Override
    public void handleGuiEvent(Packet212GuiEvent packet212GuiEvent) {
        try {
            switch (packet212GuiEvent.eventId) {
                case 1: {
                    this.tileAssemble.mode = (byte) packet212GuiEvent.getByte();
                    this.tileAssemble.updateBlockChange();
                    break;
                }
                case 2: {
                    this.tileAssemble.skipSlots = (int) packet212GuiEvent.getUVLC();
                    this.tileAssemble.dirtyBlock();
                }
            }
        } catch (IOException iOException) {
            // empty catch block
        }
    }
}

