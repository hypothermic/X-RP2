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

public class ContainerRegulator
        extends Container
        implements IHandleGuiEvent {
    private TileRegulator tileRegulator;
    public int color = 0;
    public int mode = 0;

    public ContainerRegulator(IInventory iInventory, TileRegulator tileRegulator) {
        int n;
        int n2;
        this.tileRegulator = tileRegulator;
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 3; ++n) {
                for (int i = 0; i < 3; ++i) {
                    this.a(new Slot((IInventory) tileRegulator, i + n * 3 + n2 * 9, 8 + i * 18 + n2 * 72, 18 + n * 18));
                }
            }
        }
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 9; ++n) {
                this.a(new Slot(iInventory, n + n2 * 9 + 9, 26 + n * 18, 86 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.a(new Slot(iInventory, n2, 26 + n2 * 18, 144));
        }
        this.setPlayer(((PlayerInventory) iInventory).player);
    }

    public IInventory getInventory() {
        return this.tileRegulator;
    }

    public boolean b(EntityHuman entityHuman) {
        return this.tileRegulator.a(entityHuman);
    }

    public ItemStack a(int n) {
        ItemStack itemStack = null;
        Slot slot = (Slot) this.e.get(n);
        if (slot != null && slot.c()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.cloneItemStack();
            if (n < 27 ? !this.a(itemStack2, 27, 63, true) : !this.a(itemStack2, 9, 18, false)) {
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
            if (this.color != this.tileRegulator.color) {
                iCrafting.setContainerData((Container) this, 0, this.tileRegulator.color);
            }
            if (this.mode == this.tileRegulator.mode) continue;
            iCrafting.setContainerData((Container) this, 1, (int) this.tileRegulator.mode);
        }
        this.color = this.tileRegulator.color;
        this.mode = this.tileRegulator.mode;
    }

    public void updateProgressBar(int n, int n2) {
        switch (n) {
            case 0: {
                this.tileRegulator.color = (byte) n2;
                break;
            }
            case 1: {
                this.tileRegulator.mode = (byte) n2;
            }
        }
    }

    @Override
    public void handleGuiEvent(Packet212GuiEvent packet212GuiEvent) {
        try {
            switch (packet212GuiEvent.eventId) {
                case 1: {
                    this.tileRegulator.color = (byte) packet212GuiEvent.getByte();
                    this.tileRegulator.dirtyBlock();
                    break;
                }
                case 2: {
                    this.tileRegulator.mode = (byte) packet212GuiEvent.getByte();
                    this.tileRegulator.dirtyBlock();
                    break;
                }
                default: {
                    return;
                }
            }
        } catch (IOException iOException) {
            // empty catch block
        }
    }
}

