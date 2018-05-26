/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.CoreLib;
import eloraam.core.TubeItem;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityItem;
import net.minecraft.server.Explosion;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.RedPowerMachine;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public class TubeBuffer {

    List buffer = null;
    public boolean plugged = false;
    public TileEntity parent;

    public TubeBuffer(TileEntity arg0) {
	this.parent = arg0;
    }

    public void add(TubeItem arg0) {
	if (this.buffer == null) {
	    this.buffer = Collections.synchronizedList(new LinkedList());
	}
	this.buffer.add(0, arg0);
	this.checkLength();
    }

    public void addAll(Collection<ItemStack> arg0) { // X-RP added type decl for arg0
	if (this.buffer == null) {
	    this.buffer = new LinkedList();
	}
	for (ItemStack arg1 : arg0) {
	    this.buffer.add(new TubeItem(0, arg1));
	}
	this.checkLength();
    }

    public void addBounce(TubeItem arg0) {
	if (this.buffer == null) {
	    this.buffer = Collections.synchronizedList(new LinkedList());
	}
	this.buffer.add(arg0);
	this.plugged = true;
	this.checkLength();
    }

    public void addNew(ItemStack arg0) {
	if (this.buffer == null) {
	    this.buffer = new LinkedList();
	}
	this.buffer.add(0, new TubeItem(0, arg0));
	this.checkLength();
    }

    public void addNewColor(ItemStack arg0, int arg1) {
	if (this.buffer == null) {
	    this.buffer = new LinkedList();
	}
	TubeItem arg2 = new TubeItem(0, arg0);
	arg2.color = (byte) arg1;
	this.buffer.add(0, arg2);
	this.checkLength();
    }

    public void checkLength() {
	if (RedPowerMachine.tubeBufferLength >= 0 && this.buffer.size() > RedPowerMachine.tubeBufferLength) {
	    if (RedPowerMachine.tubeBufferFull.equalsIgnoreCase("vanish")) {
		while (this.buffer.size() > RedPowerMachine.tubeBufferLength) {
		    TubeItem tubeItem = (TubeItem) this.buffer.remove(0);
		}
	    } else if (RedPowerMachine.tubeBufferFull.equalsIgnoreCase("drop")) {
		while (this.buffer.size() > RedPowerMachine.tubeBufferLength) {
		    TubeItem arg0 = (TubeItem) this.buffer.remove(0);
		    ItemStack arg1 = arg0.item;
		    EntityItem arg2 = new EntityItem(this.parent.world, (double) this.parent.x, (double) (this.parent.y + 1), (double) this.parent.z, arg1);
		    this.parent.world.addEntity((Entity) arg2);
		}
	    } else if (RedPowerMachine.tubeBufferFull.equalsIgnoreCase("break")) {
		this.parent.world.setTypeId(this.parent.x, this.parent.y, this.parent.z, 0);
	    } else if (RedPowerMachine.tubeBufferFull.equalsIgnoreCase("explode")) {
		this.parent.world.explode(null, (double) this.parent.x, (double) this.parent.y, (double) this.parent.z, 4.0f);
	    }
	}
    }

    public TubeItem getLast() {
	return this.buffer == null ? null : (TubeItem) this.buffer.get(this.buffer.size() - 1);
    }

    public boolean isEmpty() {
	return this.buffer == null ? true : this.buffer.size() == 0;
    }

    public void onRemove(TileEntity arg0) {
	if (this.buffer != null) {
	    int i = 0;
	    while (i < this.buffer.size()) {
		TubeItem arg2 = (TubeItem) this.buffer.get(i);
		if (arg2 != null && arg2.item.count > 0) {
		    CoreLib.dropItem(arg0.world, arg0.x, arg0.y, arg0.z, arg2.item);
		}
		++i;
	    }
	}
    }

    public void pop() {
	if (this.buffer.size() <= 0) {
	    return;
	}
	this.buffer.remove(this.buffer.size() - 1);
	if (this.buffer.size() == 0) {
	    this.plugged = false;
	}
    }

    public void readFromNBT(NBTTagCompound arg0) {
	byte arg4;
	NBTTagList arg1 = arg0.getList("Buffer");
	if (arg1.size() > 0) {
	    this.buffer = Collections.synchronizedList(new LinkedList());
	    int arg2 = 0;
	    while (arg2 < arg1.size()) {
		NBTTagCompound arg3 = (NBTTagCompound) arg1.get(arg2);
		this.buffer.add(TubeItem.newFromNBT(arg3));
		++arg2;
	    }
	}
	this.plugged = (arg4 = arg0.getByte("Plug")) > 0;
    }

    public int size() {
	return this.buffer == null ? 0 : this.buffer.size();
    }

    public void writeToNBT(NBTTagCompound arg0) {
	NBTTagList arg1 = new NBTTagList();
	if (this.buffer != null) {
	    int i = 0;
	    while (i < this.buffer.size()) {
		TubeItem arg4 = (TubeItem) this.buffer.get(i);
		NBTTagCompound arg2 = new NBTTagCompound();
		arg4.writeToNBT(arg2);
		arg1.add((NBTBase) arg2);
		++i;
	    }
	}
	arg0.set("Buffer", (NBTBase) arg1);
	arg0.setByte("Plug", (byte) (this.plugged ? 1 : 0));
    }
}
