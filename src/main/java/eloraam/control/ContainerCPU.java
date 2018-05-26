/* X-RP - decompiled with CFR */
package eloraam.control;

import eloraam.control.TileCPU;
import eloraam.core.IHandleGuiEvent;
import eloraam.core.NullInventory;
import eloraam.core.Packet212GuiEvent;
import java.io.IOException;
import java.util.List;
import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;

public class ContainerCPU extends Container implements IHandleGuiEvent {

    private TileCPU tileCPU;
    int byte0 = 0;
    int byte1 = 0;
    int rbaddr = 0;
    boolean isrun = false;
    private IInventory inv = new NullInventory();

    public ContainerCPU(IInventory iInventory, TileCPU tileCPU) {
	this.tileCPU = tileCPU;
	this.setPlayer(((PlayerInventory) iInventory).player);
    }

    public IInventory getInventory() {
	return this.inv;
    }

    public boolean b(EntityHuman entityHuman) {
	return this.tileCPU.isUseableByPlayer(entityHuman);
    }

    public ItemStack a(int n) {
	return null;
    }

    public void a() {
	super.a();
	for (int i = 0; i < this.listeners.size(); ++i) {
	    ICrafting iCrafting = (ICrafting) this.listeners.get(i);
	    if (this.tileCPU.byte0 != this.byte0) {
		iCrafting.setContainerData((Container) this, 0, this.tileCPU.byte0);
	    }
	    if (this.tileCPU.byte1 != this.byte1) {
		iCrafting.setContainerData((Container) this, 1, this.tileCPU.byte1);
	    }
	    if (this.tileCPU.rbaddr != this.rbaddr) {
		iCrafting.setContainerData((Container) this, 2, this.tileCPU.rbaddr);
	    }
	    if (this.tileCPU.isRunning() == this.isrun)
		continue;
	    iCrafting.setContainerData((Container) this, 3, this.tileCPU.isRunning() ? 1 : 0);
	}
	this.byte0 = this.tileCPU.byte0;
	this.byte1 = this.tileCPU.byte1;
	this.rbaddr = this.tileCPU.rbaddr;
	this.isrun = this.tileCPU.isRunning();
    }

    public void updateProgressBar(int n, int n2) {
	switch (n) {
	case 0: {
	    this.tileCPU.byte0 = n2;
	    break;
	}
	case 1: {
	    this.tileCPU.byte1 = n2;
	    break;
	}
	case 2: {
	    this.tileCPU.rbaddr = n2;
	    break;
	}
	case 3: {
	    this.tileCPU.sliceCycles = n2 <= 0 ? -1 : 0;
	}
	}
    }

    @Override
    public void handleGuiEvent(Packet212GuiEvent packet212GuiEvent) {
	try {
	    switch (packet212GuiEvent.eventId) {
	    case 1: {
		this.tileCPU.byte0 = packet212GuiEvent.getByte();
		break;
	    }
	    case 2: {
		this.tileCPU.byte1 = packet212GuiEvent.getByte();
		break;
	    }
	    case 3: {
		this.tileCPU.rbaddr = packet212GuiEvent.getByte();
		break;
	    }
	    case 4: {
		this.tileCPU.warmBootCPU();
		break;
	    }
	    case 5: {
		this.tileCPU.haltCPU();
		break;
	    }
	    case 6: {
		this.tileCPU.coldBootCPU();
	    }
	    }
	} catch (IOException iOException) {
	    // empty catch block
	}
    }
}
