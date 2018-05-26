/* X-RP - decompiled with CFR */
package eloraam.base;

import eloraam.base.TileAppliance;
import eloraam.core.CoreLib;
import eloraam.core.CoreProxy;
import forge.ISidedInventory;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.BaseMod;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import net.minecraft.server.mod_RedPowerCore;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

public class TileAdvBench extends TileAppliance implements IInventory, ISidedInventory {

    private ItemStack[] contents = new ItemStack[27];
    public List<HumanEntity> transaction = new ArrayList<HumanEntity>();

    public void onOpen(CraftHumanEntity craftHumanEntity) {
	this.transaction.add((HumanEntity) craftHumanEntity);
    }

    public void onClose(CraftHumanEntity craftHumanEntity) {
	this.transaction.remove((Object) craftHumanEntity);
    }

    public List<HumanEntity> getViewers() {
	return this.transaction;
    }

    public void setMaxStackSize(int n) {
    }

    public ItemStack[] getContents() {
	return this.contents;
    }

    @Override
    public int getExtendedID() {
	return 3;
    }

    public boolean canUpdate() {
	return false;
    }

    @Override
    public boolean onBlockActivated(EntityHuman entityHuman) {
	if (entityHuman.isSneaking()) {
	    return false;
	}
	if (CoreProxy.isClient(this.world)) {
	    return true;
	}
	entityHuman.openGui((BaseMod) mod_RedPowerCore.instance, 2, this.world, this.x, this.y, this.z);
	return true;
    }

    @Override
    public void onBlockPlacedBy(EntityLiving entityLiving) {
	this.Rotation = (int) Math.floor((double) (entityLiving.yaw * 4.0f / 360.0f) + 0.5) & 3;
    }

    @Override
    public void onBlockRemoval() {
	for (int i = 0; i < 27; ++i) {
	    ItemStack itemStack = this.contents[i];
	    if (itemStack == null || itemStack.count <= 0)
		continue;
	    CoreLib.dropItem(this.world, this.x, this.y, this.z, itemStack);
	}
    }

    public int getStartInventorySide(int n) {
	return 9;
    }

    public int getSizeInventorySide(int n) {
	return 18;
    }

    public int getSize() {
	return 27;
    }

    public ItemStack getItem(int n) {
	return this.contents[n];
    }

    public synchronized ItemStack splitStack(int n, int n2) {
	if (this.contents[n] == null) {
	    return null;
	}
	if (this.contents[n].count <= n2) {
	    ItemStack itemStack = this.contents[n];
	    this.contents[n] = null;
	    this.update();
	    return itemStack;
	}
	ItemStack itemStack = this.contents[n].a(n2);
	if (this.contents[n].count == 0) {
	    this.contents[n] = null;
	}
	this.update();
	return itemStack;
    }

    public synchronized ItemStack splitWithoutUpdate(int n) {
	if (this.contents[n] == null) {
	    return null;
	}
	ItemStack itemStack = this.contents[n];
	this.contents[n] = null;
	return itemStack;
    }

    public synchronized void setItem(int n, ItemStack itemStack) {
	this.contents[n] = itemStack;
	if (itemStack != null && itemStack.count > this.getMaxStackSize()) {
	    itemStack.count = this.getMaxStackSize();
	}
	this.update();
    }

    public String getName() {
	return "Project Table";
    }

    public int getMaxStackSize() {
	return 64;
    }

    public boolean a(EntityHuman entityHuman) {
	if (this.world.getTileEntity(this.x, this.y, this.z) != this) {
	    return false;
	}
	return entityHuman.e((double) this.x + 0.5, (double) this.y + 0.5, (double) this.z + 0.5) <= 64.0;
    }

    public void g() {
    }

    public void f() {
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
	super.a(nBTTagCompound);
	NBTTagList nBTTagList = nBTTagCompound.getList("Items");
	this.contents = new ItemStack[this.getSize()];
	for (int i = 0; i < nBTTagList.size(); ++i) {
	    NBTTagCompound nBTTagCompound2 = (NBTTagCompound) nBTTagList.get(i);
	    int n = nBTTagCompound2.getByte("Slot") & 255;
	    if (n < 0 || n >= this.contents.length)
		continue;
	    this.contents[n] = ItemStack.a((NBTTagCompound) nBTTagCompound2);
	}
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
	super.b(nBTTagCompound);
	NBTTagList nBTTagList = new NBTTagList();
	for (int i = 0; i < this.contents.length; ++i) {
	    if (this.contents[i] == null)
		continue;
	    NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
	    nBTTagCompound2.setByte("Slot", (byte) i);
	    this.contents[i].save(nBTTagCompound2);
	    nBTTagList.add((NBTBase) nBTTagCompound2);
	}
	nBTTagCompound.set("Items", (NBTBase) nBTTagList);
    }
}
