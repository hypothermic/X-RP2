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

public class ContainerRetriever
        extends Container
        implements IHandleGuiEvent {
    private TileRetriever tileRetriever;
    public int charge = 0;
    public int flow = 0;
    public int color = 0;
    public int select = 0;
    public int mode = 0;

    public ContainerRetriever(IInventory iInventory, TileRetriever tileRetriever) {
        int n;
        int n2;
        this.tileRetriever = tileRetriever;
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 3; ++n) {
                this.a(new Slot((IInventory) tileRetriever, n + n2 * 3, 62 + n * 18, 17 + n2 * 18));
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
        return this.tileRetriever;
    }

    public boolean b(EntityHuman entityHuman) {
        return this.tileRetriever.a(entityHuman);
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
            if (this.charge != this.tileRetriever.cond.Charge) {
                iCrafting.setContainerData((Container) this, 0, this.tileRetriever.cond.Charge);
            }
            if (this.flow != this.tileRetriever.cond.Flow) {
                iCrafting.setContainerData((Container) this, 1, this.tileRetriever.cond.Flow);
            }
            if (this.color != this.tileRetriever.color) {
                iCrafting.setContainerData((Container) this, 2, (int) this.tileRetriever.color);
            }
            if (this.select != this.tileRetriever.select) {
                iCrafting.setContainerData((Container) this, 3, (int) this.tileRetriever.select);
            }
            if (this.mode == this.tileRetriever.mode) continue;
            iCrafting.setContainerData((Container) this, 4, (int) this.tileRetriever.mode);
        }
        this.flow = this.tileRetriever.cond.Flow;
        this.charge = this.tileRetriever.cond.Charge;
        this.color = this.tileRetriever.color;
        this.select = this.tileRetriever.select;
        this.mode = this.tileRetriever.mode;
    }

    public void updateProgressBar(int n, int n2) {
        switch (n) {
            case 0: {
                this.tileRetriever.cond.Charge = n2;
                break;
            }
            case 1: {
                this.tileRetriever.cond.Flow = n2;
                break;
            }
            case 2: {
                this.tileRetriever.color = (byte) n2;
                break;
            }
            case 3: {
                this.tileRetriever.select = (byte) n2;
                break;
            }
            case 4: {
                this.tileRetriever.mode = (byte) n2;
            }
        }
    }

    @Override
    public void handleGuiEvent(Packet212GuiEvent packet212GuiEvent) {
        try {
            switch (packet212GuiEvent.eventId) {
                case 1: {
                    this.tileRetriever.color = (byte) packet212GuiEvent.getByte();
                    this.tileRetriever.dirtyBlock();
                    break;
                }
                case 2: {
                    this.tileRetriever.mode = (byte) packet212GuiEvent.getByte();
                    this.tileRetriever.dirtyBlock();
                }
            }
        } catch (IOException iOException) {
            // empty catch block
        }
    }
}

