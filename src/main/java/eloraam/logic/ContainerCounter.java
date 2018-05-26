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
 */
package eloraam.logic;

import eloraam.core.IHandleGuiEvent;
import eloraam.core.NullInventory;
import eloraam.core.Packet212GuiEvent;
import eloraam.logic.TileLogicStorage;
import java.io.IOException;
import java.util.List;
import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;

public class ContainerCounter
extends Container
implements IHandleGuiEvent {
    int Count = 0;
    int CountMax = 0;
    int Inc = 0;
    int Dec = 0;
    private TileLogicStorage tileLogic;
    private IInventory inv = new NullInventory();

    public ContainerCounter(IInventory iInventory, TileLogicStorage tileLogicStorage) {
        this.tileLogic = tileLogicStorage;
        this.setPlayer(((PlayerInventory)iInventory).player);
    }

    public IInventory getInventory() {
        return this.inv;
    }

    public boolean b(EntityHuman entityHuman) {
        return this.tileLogic.isUseableByPlayer(entityHuman);
    }

    public ItemStack a(int n) {
        return null;
    }

    public void a() {
        super.a();
        TileLogicStorage.LogicStorageCounter logicStorageCounter = (TileLogicStorage.LogicStorageCounter)this.tileLogic.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
        for (int i = 0; i < this.listeners.size(); ++i) {
            ICrafting iCrafting = (ICrafting)this.listeners.get(i);
            if (this.Count != logicStorageCounter.Count) {
                iCrafting.setContainerData((Container)this, 0, logicStorageCounter.Count);
            }
            if (this.CountMax != logicStorageCounter.CountMax) {
                iCrafting.setContainerData((Container)this, 1, logicStorageCounter.CountMax);
            }
            if (this.Inc != logicStorageCounter.Inc) {
                iCrafting.setContainerData((Container)this, 2, logicStorageCounter.Inc);
            }
            if (this.Dec == logicStorageCounter.Dec) continue;
            iCrafting.setContainerData((Container)this, 3, logicStorageCounter.Dec);
        }
        this.Count = logicStorageCounter.Count;
        this.CountMax = logicStorageCounter.CountMax;
        this.Inc = logicStorageCounter.Inc;
        this.Dec = logicStorageCounter.Dec;
    }

    public void updateProgressBar(int n, int n2) {
        TileLogicStorage.LogicStorageCounter logicStorageCounter = (TileLogicStorage.LogicStorageCounter)this.tileLogic.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
        switch (n) {
            case 0: {
                logicStorageCounter.Count = n2;
                break;
            }
            case 1: {
                logicStorageCounter.CountMax = n2;
                break;
            }
            case 2: {
                logicStorageCounter.Inc = n2;
                break;
            }
            case 3: {
                logicStorageCounter.Dec = n2;
            }
        }
    }

    @Override
    public void handleGuiEvent(Packet212GuiEvent packet212GuiEvent) {
        TileLogicStorage.LogicStorageCounter logicStorageCounter = (TileLogicStorage.LogicStorageCounter)this.tileLogic.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
        try {
            switch (packet212GuiEvent.eventId) {
                case 0: {
                    logicStorageCounter.Count = (int)packet212GuiEvent.getUVLC();
                    this.tileLogic.updateBlock();
                    break;
                }
                case 1: {
                    logicStorageCounter.CountMax = (int)packet212GuiEvent.getUVLC();
                    this.tileLogic.updateBlock();
                    break;
                }
                case 2: {
                    logicStorageCounter.Inc = (int)packet212GuiEvent.getUVLC();
                    this.tileLogic.dirtyBlock();
                    break;
                }
                case 3: {
                    logicStorageCounter.Dec = (int)packet212GuiEvent.getUVLC();
                    this.tileLogic.dirtyBlock();
                }
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

