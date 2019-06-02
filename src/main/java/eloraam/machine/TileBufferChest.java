/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  forge.ISidedInventory
 *  net.minecraft.server.BaseMod
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.EntityLiving
 *  net.minecraft.server.IInventory
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.NBTBase
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.NBTTagList
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 *  org.bukkit.craftbukkit.entity.CraftHumanEntity
 *  org.bukkit.entity.HumanEntity
 */
package eloraam.machine;

import eloraam.base.TileAppliance;
import eloraam.core.CoreLib;
import eloraam.core.CoreProxy;
import eloraam.core.IRotatable;
import forge.ISidedInventory;
import net.minecraft.server.*;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.List;

public class TileBufferChest
        extends TileAppliance
        implements IInventory,
        ISidedInventory,
        IRotatable {
    private ItemStack[] contents = new ItemStack[20];
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
        return 2;
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
        entityHuman.openGui((BaseMod) mod_RedPowerMachine.instance, 4, this.world, this.x, this.y, this.z);
        return true;
    }

    public int getFacing(EntityLiving entityLiving) {
        int n = (int) Math.floor((double) (entityLiving.yaw * 4.0f / 360.0f) + 0.5) & 3;
        if (Math.abs(entityLiving.locX - (double) this.x) < 2.0 && Math.abs(entityLiving.locZ - (double) this.z) < 2.0) {
            double d = entityLiving.locY + 1.82 - (double) entityLiving.height - (double) this.y;
            if (d > 2.0) {
                return 0;
            }
            if (d < 0.0) {
                return 1;
            }
        }
        switch (n) {
            case 0: {
                return 3;
            }
            case 1: {
                return 4;
            }
            case 2: {
                return 2;
            }
        }
        return 5;
    }

    @Override
    public void onBlockPlacedBy(EntityLiving entityLiving) {
        this.Rotation = this.getFacing(entityLiving);
    }

    @Override
    public void onBlockRemoval() {
        for (int i = 0; i < 20; ++i) {
            ItemStack itemStack = this.contents[i];
            if (itemStack == null || itemStack.count <= 0) continue;
            CoreLib.dropItem(this.world, this.x, this.y, this.z, itemStack);
        }
    }

    @Override
    public int getPartMaxRotation(int n, boolean bl) {
        return !bl ? 5 : 0;
    }

    @Override
    public int getPartRotation(int n, boolean bl) {
        if (bl) {
            return 0;
        }
        return this.Rotation;
    }

    @Override
    public void setPartRotation(int n, boolean bl, int n2) {
        if (bl) {
            return;
        }
        this.Rotation = n2;
        this.updateBlockChange();
    }

    public int getSize() {
        return 20;
    }

    public ItemStack getItem(int n) {
        return this.contents[n];
    }

    public ItemStack splitStack(int n, int n2) {
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

    public ItemStack splitWithoutUpdate(int n) {
        if (this.contents[n] == null) {
            return null;
        }
        ItemStack itemStack = this.contents[n];
        this.contents[n] = null;
        return itemStack;
    }

    public void setItem(int n, ItemStack itemStack) {
        this.contents[n] = itemStack;
        if (itemStack != null && itemStack.count > this.getMaxStackSize()) {
            itemStack.count = this.getMaxStackSize();
        }
        this.update();
    }

    public String getName() {
        return "Buffer";
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

    public int getStartInventorySide(int n) {
        if ((n ^ 1) == this.Rotation) {
            return 0;
        }
        return 4 * ((5 + (n ^ 1) - this.Rotation) % 6);
    }

    public int getSizeInventorySide(int n) {
        return (n ^ 1) != this.Rotation ? 4 : 20;
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
        NBTTagList nBTTagList = nBTTagCompound.getList("Items");
        this.contents = new ItemStack[this.getSize()];
        for (int i = 0; i < nBTTagList.size(); ++i) {
            NBTTagCompound nBTTagCompound2 = (NBTTagCompound) nBTTagList.get(i);
            int n = nBTTagCompound2.getByte("Slot") & 255;
            if (n < 0 || n >= this.contents.length) continue;
            this.contents[n] = ItemStack.a((NBTTagCompound) nBTTagCompound2);
        }
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        NBTTagList nBTTagList = new NBTTagList();
        for (int i = 0; i < this.contents.length; ++i) {
            if (this.contents[i] == null) continue;
            NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
            nBTTagCompound2.setByte("Slot", (byte) i);
            this.contents[i].save(nBTTagCompound2);
            nBTTagList.add((NBTBase) nBTTagCompound2);
        }
        nBTTagCompound.set("Items", (NBTBase) nBTTagList);
    }
}

