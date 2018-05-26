/* X-RP - decompiled with CFR */
package eloraam.control;

import eloraam.core.IHandleGuiEvent;
import eloraam.core.IRedbusConnectable;
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

public class ContainerBusId extends Container implements IHandleGuiEvent {

    private IRedbusConnectable rbConn;
    int addr = 0;
    private IInventory inv = new NullInventory();

    public ContainerBusId(IInventory iInventory, IRedbusConnectable iRedbusConnectable) {
	this.rbConn = iRedbusConnectable;
	this.setPlayer(((PlayerInventory) iInventory).player);
    }

    public IInventory getInventory() {
	return this.inv;
    }

    public boolean b(EntityHuman entityHuman) {
	return true;
    }

    public ItemStack a(int n) {
	return null;
    }

    public void a() {
	super.a();
	for (int i = 0; i < this.listeners.size(); ++i) {
	    ICrafting iCrafting = (ICrafting) this.listeners.get(i);
	    if (this.rbConn.rbGetAddr() == this.addr)
		continue;
	    iCrafting.setContainerData((Container) this, 0, this.rbConn.rbGetAddr());
	}
	this.addr = this.rbConn.rbGetAddr();
    }

    public void updateProgressBar(int n, int n2) {
	switch (n) {
	case 0: {
	    this.rbConn.rbSetAddr(n2);
	    return;
	}
	}
    }

    @Override
    public void handleGuiEvent(Packet212GuiEvent packet212GuiEvent) {
	try {
	    if (packet212GuiEvent.eventId != 1) {
		return;
	    }
	    this.rbConn.rbSetAddr(packet212GuiEvent.getByte());
	} catch (IOException iOException) {
	    // empty catch block
	}
    }
}
