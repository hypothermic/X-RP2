/* X-RP - decompiled with CFR */
package eloraam.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

public class NullInventory implements IInventory {

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
	return null;
    }

    public InventoryHolder getOwner() {
	return null;
    }

    public int getSize() {
	return 0;
    }

    public ItemStack getItem(int n) {
	return null;
    }

    public ItemStack splitStack(int n, int n2) {
	return null;
    }

    public ItemStack splitWithoutUpdate(int n) {
	return null;
    }

    public void setItem(int n, ItemStack itemStack) {
    }

    public String getName() {
	return "NullInventory";
    }

    public int getMaxStackSize() {
	return 0;
    }

    public void update() {
    }

    public boolean a(EntityHuman entityHuman) {
	return false;
    }

    public void f() {
    }

    public void g() {
    }
}
