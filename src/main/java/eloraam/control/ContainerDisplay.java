/* X-RP - decompiled with CFR */
package eloraam.control;

import eloraam.core.IHandleGuiEvent;
import eloraam.core.NullInventory;
import eloraam.core.Packet212GuiEvent;
import net.minecraft.server.*;

import java.io.IOException;

public class ContainerDisplay extends Container implements IHandleGuiEvent {

    private TileDisplay tileDisplay;
    private byte[] screen = new byte[4000];
    int cursx = 0;
    int cursy = 0;
    int cursmode = 0;
    private IInventory inv = new NullInventory();

    public ContainerDisplay(IInventory iInventory, TileDisplay tileDisplay) {
        this.tileDisplay = tileDisplay;
        this.setPlayer(((PlayerInventory) iInventory).player);
    }

    public IInventory getInventory() {
        return this.inv;
    }

    public boolean b(EntityHuman entityHuman) {
        return this.tileDisplay.isUseableByPlayer(entityHuman);
    }

    public ItemStack a(int n) {
        return null;
    }

    public void a() {
        super.a();
        for (int i = 0; i < this.listeners.size(); ++i) {
            ICrafting iCrafting = (ICrafting) this.listeners.get(i);
            if (this.tileDisplay.cursX != this.cursx) {
                iCrafting.setContainerData((Container) this, 0, this.tileDisplay.cursX);
            }
            if (this.tileDisplay.cursY != this.cursy) {
                iCrafting.setContainerData((Container) this, 1, this.tileDisplay.cursY);
            }
            if (this.tileDisplay.cursMode != this.cursmode) {
                iCrafting.setContainerData((Container) this, 2, this.tileDisplay.cursMode);
            }
            for (int j = 3; j < 4003; ++j) {
                if (this.screen[j - 3] == this.tileDisplay.screen[j - 3])
                    continue;
                iCrafting.setContainerData((Container) this, j, (int) this.tileDisplay.screen[j - 3]);
            }
        }
        System.arraycopy(this.tileDisplay.screen, 0, this.screen, 0, 4000);
        this.cursx = this.tileDisplay.cursX;
        this.cursy = this.tileDisplay.cursY;
        this.cursmode = this.tileDisplay.cursMode;
    }

    public void updateProgressBar(int n, int n2) {
        switch (n) {
            case 0: {
                this.tileDisplay.cursX = n2;
                return;
            }
            case 1: {
                this.tileDisplay.cursY = n2;
                return;
            }
            case 2: {
                this.tileDisplay.cursMode = n2;
                return;
            }
        }
        if (n >= 3 && n < 4003) {
            this.tileDisplay.screen[n - 3] = (byte) n2;
            return;
        }
    }

    @Override
    public void handleGuiEvent(Packet212GuiEvent packet212GuiEvent) {
        try {
            if (packet212GuiEvent.eventId != 1) {
                return;
            }
            this.tileDisplay.pushKey((byte) packet212GuiEvent.getByte());
            this.tileDisplay.dirtyBlock();
        } catch (IOException iOException) {
            // empty catch block
        }
    }
}
