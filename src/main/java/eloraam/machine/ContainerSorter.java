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

import eloraam.core.BluePowerEndpoint;
import eloraam.core.IHandleGuiEvent;
import eloraam.core.Packet212GuiEvent;
import eloraam.machine.TileSorter;
import java.io.IOException;
import java.util.List;
import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.Slot;

public class ContainerSorter
extends Container
implements IHandleGuiEvent {
    public byte[] colors = new byte[8];
    public int column;
    public int charge = 0;
    public int flow = 0;
    public int mode = 0;
    public int defcolor = 0;
    private TileSorter tileSorter;

    public ContainerSorter(IInventory iInventory, TileSorter tileSorter) {
        int n;
        int n2;
        this.tileSorter = tileSorter;
        for (n2 = 0; n2 < 5; ++n2) {
            for (n = 0; n < 8; ++n) {
                this.a(new Slot((IInventory)tileSorter, n + n2 * 8, 26 + 18 * n, 18 + 18 * n2));
            }
        }
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 9; ++n) {
                this.a(new Slot(iInventory, n + n2 * 9 + 9, 8 + n * 18, 140 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.a(new Slot(iInventory, n2, 8 + n2 * 18, 198));
        }
        this.setPlayer(((PlayerInventory)iInventory).player);
    }

    public IInventory getInventory() {
        return this.tileSorter;
    }

    public boolean b(EntityHuman entityHuman) {
        return this.tileSorter.a(entityHuman);
    }

    public ItemStack a(int n) {
        ItemStack itemStack = null;
        Slot slot = (Slot)this.e.get(n);
        if (slot != null && slot.c()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.cloneItemStack();
            if (n < 40 ? !this.a(itemStack2, 40, 76, true) : !this.a(itemStack2, 0, 40, false)) {
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
        int n;
        super.a();
        for (n = 0; n < this.listeners.size(); ++n) {
            ICrafting iCrafting = (ICrafting)this.listeners.get(n);
            for (int i = 0; i < 8; ++i) {
                if (this.colors[i] == this.tileSorter.colors[i]) continue;
                iCrafting.setContainerData((Container)this, i, (int)this.tileSorter.colors[i]);
            }
            if (this.column != this.tileSorter.column) {
                iCrafting.setContainerData((Container)this, 8, (int)this.tileSorter.column);
            }
            if (this.charge != this.tileSorter.cond.Charge) {
                iCrafting.setContainerData((Container)this, 9, this.tileSorter.cond.Charge);
            }
            if (this.flow != this.tileSorter.cond.Flow) {
                iCrafting.setContainerData((Container)this, 10, this.tileSorter.cond.Flow);
            }
            if (this.mode != this.tileSorter.mode) {
                iCrafting.setContainerData((Container)this, 11, (int)this.tileSorter.mode);
            }
            if (this.defcolor == this.tileSorter.defcolor) continue;
            iCrafting.setContainerData((Container)this, 12, (int)this.tileSorter.defcolor);
        }
        for (n = 0; n < 8; ++n) {
            this.colors[n] = this.tileSorter.colors[n];
        }
        this.column = this.tileSorter.column;
        this.charge = this.tileSorter.cond.Charge;
        this.flow = this.tileSorter.cond.Flow;
        this.mode = this.tileSorter.mode;
        this.defcolor = this.tileSorter.defcolor;
    }

    public void a(int n, int n2) {
        this.updateProgressBar(n, n2);
    }

    public void updateProgressBar(int n, int n2) {
        if (n < 8) {
            this.tileSorter.colors[n] = (byte)n2;
        }
        switch (n) {
            case 8: {
                this.tileSorter.column = (byte)n2;
                break;
            }
            case 9: {
                this.tileSorter.cond.Charge = n2;
                break;
            }
            case 10: {
                this.tileSorter.cond.Flow = n2;
                break;
            }
            case 11: {
                this.tileSorter.mode = (byte)n2;
                break;
            }
            case 12: {
                this.tileSorter.defcolor = (byte)n2;
            }
        }
    }

    @Override
    public void handleGuiEvent(Packet212GuiEvent packet212GuiEvent) {
        try {
            switch (packet212GuiEvent.eventId) {
                default: {
                    break;
                }
                case 1: {
                    this.tileSorter.mode = (byte)packet212GuiEvent.getByte();
                    this.tileSorter.dirtyBlock();
                    break;
                }
                case 2: {
                    byte by = (byte)packet212GuiEvent.getByte();
                    if (by >= 0 && by <= 8) {
                        this.tileSorter.colors[by] = (byte)packet212GuiEvent.getByte();
                        this.tileSorter.dirtyBlock();
                    }
                    break;
                }
                case 3: {
                    this.tileSorter.defcolor = (byte)packet212GuiEvent.getByte();
                    this.tileSorter.dirtyBlock();
                    break;
                }
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

