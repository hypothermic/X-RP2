/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.CoreLib;
import eloraam.core.CoreProxy;
import eloraam.core.MachineLib;
import eloraam.core.TubeBuffer;
import eloraam.core.TubeItem;
import eloraam.machine.TileTranspose;
import forge.ISidedInventory;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.BaseMod;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import net.minecraft.server.mod_RedPowerMachine;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

public class TileFilter extends TileTranspose implements IInventory, ISidedInventory {

    protected ItemStack[] contents = new ItemStack[9];
    protected MachineLib.FilterMap filterMap = null;
    public byte color = 0;
    public List<HumanEntity> transaction = new ArrayList<HumanEntity>();

    public boolean a(EntityHuman arg0) {
	return this.world.getTileEntity(this.x, this.y, this.z) != this ? false : arg0.e((double) this.x + 0.5, (double) this.y + 0.5, (double) this.z + 0.5) <= 64.0;
    }

    @Override
    public void a(NBTTagCompound arg0) {
	super.a(arg0);
	NBTTagList arg1 = arg0.getList("Items");
	this.contents = new ItemStack[this.getSize()];
	int arg2 = 0;
	while (arg2 < arg1.size()) {
	    NBTTagCompound arg3 = (NBTTagCompound) arg1.get(arg2);
	    int arg4 = arg3.getByte("Slot") & 255;
	    if (arg4 >= 0 && arg4 < this.contents.length) {
		this.contents[arg4] = ItemStack.a((NBTTagCompound) arg3);
	    }
	    ++arg2;
	}
	this.color = arg0.getByte("color");
    }

    @Override
    protected void addToBuffer(ItemStack arg0) {
	this.buffer.addNewColor(arg0, this.color);
    }

    @Override
    public void b(NBTTagCompound arg0) {
	super.b(arg0);
	NBTTagList arg1 = new NBTTagList();
	int arg2 = 0;
	while (arg2 < this.contents.length) {
	    if (this.contents[arg2] != null) {
		NBTTagCompound arg3 = new NBTTagCompound();
		arg3.setByte("Slot", (byte) arg2);
		this.contents[arg2].save(arg3);
		arg1.add((NBTBase) arg3);
	    }
	    ++arg2;
	}
	arg0.set("Items", (NBTBase) arg1);
	arg0.setByte("color", this.color);
    }

    public void f() {
    }

    public void g() {
    }

    public ItemStack[] getContents() {
	return this.contents;
    }

    @Override
    public int getExtendedID() {
	return 3;
    }

    public ItemStack getItem(int arg0) {
	return this.contents[arg0];
    }

    public int getMaxStackSize() {
	return 64;
    }

    public String getName() {
	return "Filter";
    }

    public int getSize() {
	return 9;
    }

    public int getSizeInventorySide(int arg0) {
	return arg0 != this.Rotation && arg0 != (this.Rotation ^ 1) ? 9 : 0;
    }

    public int getStartInventorySide(int arg0) {
	return 0;
    }

    public List<HumanEntity> getViewers() {
	return this.transaction;
    }

    @Override
    protected boolean handleExtract(IInventory arg0, int arg1, int arg2) {
	if (this.filterMap == null) {
	    this.regenFilterMap();
	}
	if (this.filterMap.size() == 0) {
	    ItemStack arg5 = MachineLib.collectOneStack(arg0, arg1, arg2, null);
	    if (arg5 == null) {
		return false;
	    }
	    this.buffer.addNewColor(arg5, this.color);
	    this.drainBuffer();
	    return true;
	}
	int arg3 = MachineLib.matchAnyStack(this.filterMap, arg0, arg1, arg2);
	if (arg3 < 0) {
	    return false;
	}
	ItemStack arg4 = MachineLib.collectOneStack(arg0, arg1, arg2, this.contents[arg3]);
	this.buffer.addNewColor(arg4, this.color);
	this.drainBuffer();
	return true;
    }

    @Override
    public boolean onBlockActivated(EntityHuman arg0) {
	if (arg0.isSneaking()) {
	    return false;
	}
	if (CoreProxy.isClient(this.world)) {
	    return true;
	}
	arg0.openGui((BaseMod) mod_RedPowerMachine.instance, 2, this.world, this.x, this.y, this.z);
	return true;
    }

    @Override
    public void onBlockRemoval() {
	super.onBlockRemoval();
	int arg0 = 0;
	while (arg0 < 9) {
	    ItemStack arg1 = this.contents[arg0];
	    if (arg1 != null && arg1.count > 0) {
		CoreLib.dropItem(this.world, this.x, this.y, this.z, arg1);
	    }
	    ++arg0;
	}
    }

    public void onClose(CraftHumanEntity arg0) {
	this.transaction.remove((Object) arg0);
    }

    public void onOpen(CraftHumanEntity arg0) {
	this.transaction.add((HumanEntity) arg0);
    }

    void regenFilterMap() {
	this.filterMap = MachineLib.makeFilterMap(this.contents);
    }

    public void setItem(int arg0, ItemStack arg1) {
	this.contents[arg0] = arg1;
	if (arg1 != null && arg1.count > this.getMaxStackSize()) {
	    arg1.count = this.getMaxStackSize();
	}
	this.update();
    }

    public void setMaxStackSize(int arg0) {
    }

    public ItemStack splitStack(int arg0, int arg1) {
	if (this.contents[arg0] == null) {
	    return null;
	}
	if (this.contents[arg0].count <= arg1) {
	    ItemStack arg2 = this.contents[arg0];
	    this.contents[arg0] = null;
	    this.update();
	    return arg2;
	}
	ItemStack arg2 = this.contents[arg0].a(arg1);
	if (this.contents[arg0].count == 0) {
	    this.contents[arg0] = null;
	}
	this.update();
	return arg2;
    }

    public ItemStack splitWithoutUpdate(int arg0) {
	if (this.contents[arg0] == null) {
	    return null;
	}
	ItemStack arg1 = this.contents[arg0];
	this.contents[arg0] = null;
	return arg1;
    }

    @Override
    protected boolean suckFilter(ItemStack arg0) {
	if (this.filterMap == null) {
	    this.regenFilterMap();
	}
	return this.filterMap.size() == 0 ? true : this.filterMap.containsKey(arg0);
    }

    @Override
    public boolean tubeItemCanEnter(int arg0, int arg1, TubeItem arg2) {
	if (arg0 == (this.Rotation ^ 1) && arg1 == 1) {
	    if (this.filterMap == null) {
		this.regenFilterMap();
	    }
	    return this.filterMap.size() == 0 ? super.tubeItemCanEnter(arg0, arg1, arg2) : (!this.filterMap.containsKey(arg2.item) ? false : super.tubeItemCanEnter(arg0, arg1, arg2));
	}
	return super.tubeItemCanEnter(arg0, arg1, arg2);
    }

    @Override
    public boolean tubeItemEnter(int arg0, int arg1, TubeItem arg2) {
	if (arg0 == (this.Rotation ^ 1) && arg1 == 1) {
	    if (this.filterMap == null) {
		this.regenFilterMap();
	    }
	    return this.filterMap.size() == 0 ? super.tubeItemEnter(arg0, arg1, arg2) : (!this.filterMap.containsKey(arg2.item) ? false : super.tubeItemEnter(arg0, arg1, arg2));
	}
	return super.tubeItemEnter(arg0, arg1, arg2);
    }

    public void update() {
	this.filterMap = null;
	super.update();
    }
}
