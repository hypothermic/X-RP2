/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  forge.ISidedInventory
 *  net.minecraft.server.BaseMod
 *  net.minecraft.server.EntityHuman
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

public class TileFilter
extends TileTranspose
implements IInventory,
ISidedInventory {
    protected ItemStack[] contents = new ItemStack[9];
    protected MachineLib.FilterMap filterMap = null;
    public byte color = 0;
    public List<HumanEntity> transaction = new ArrayList<HumanEntity>();

    public void onOpen(CraftHumanEntity craftHumanEntity) {
        this.transaction.add((HumanEntity)craftHumanEntity);
    }

    public void onClose(CraftHumanEntity craftHumanEntity) {
        this.transaction.remove((Object)craftHumanEntity);
    }

    public List<HumanEntity> getViewers() {
        return this.transaction;
    }

    public void setMaxStackSize(int n) {
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    void regenFilterMap() {
        this.filterMap = MachineLib.makeFilterMap(this.contents);
    }

    @Override
    public boolean tubeItemEnter(int n, int n2, TubeItem tubeItem) {
        if (n != (this.Rotation ^ 1) || n2 != 1) {
            return super.tubeItemEnter(n, n2, tubeItem);
        }
        if (this.filterMap == null) {
            this.regenFilterMap();
        }
        if (this.filterMap.size() == 0) {
            return super.tubeItemEnter(n, n2, tubeItem);
        }
        if (!this.filterMap.containsKey(tubeItem.item)) {
            return false;
        }
        return super.tubeItemEnter(n, n2, tubeItem);
    }

    @Override
    public boolean tubeItemCanEnter(int n, int n2, TubeItem tubeItem) {
        if (n != (this.Rotation ^ 1) || n2 != 1) {
            return super.tubeItemCanEnter(n, n2, tubeItem);
        }
        if (this.filterMap == null) {
            this.regenFilterMap();
        }
        if (this.filterMap.size() == 0) {
            return super.tubeItemCanEnter(n, n2, tubeItem);
        }
        if (!this.filterMap.containsKey(tubeItem.item)) {
            return false;
        }
        return super.tubeItemCanEnter(n, n2, tubeItem);
    }

    @Override
    protected void addToBuffer(ItemStack itemStack) {
        this.buffer.addNewColor(itemStack, this.color);
    }

    public int getStartInventorySide(int n) {
        return 0;
    }

    public int getSizeInventorySide(int n) {
        return n != this.Rotation && n != (this.Rotation ^ 1) ? 9 : 0;
    }

    @Override
    public boolean onBlockActivated(EntityHuman entityHuman) {
        if (entityHuman.isSneaking()) {
            return false;
        }
        if (CoreProxy.isClient(this.world)) {
            return true;
        }
        entityHuman.openGui((BaseMod)mod_RedPowerMachine.instance, 2, this.world, this.x, this.y, this.z);
        return true;
    }

    @Override
    public int getExtendedID() {
        return 3;
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = this.contents[i];
            if (itemStack == null || itemStack.count <= 0) continue;
            CoreLib.dropItem(this.world, this.x, this.y, this.z, itemStack);
        }
    }

    @Override
    protected boolean handleExtract(IInventory iInventory, int n, int n2) {
        if (this.filterMap == null) {
            this.regenFilterMap();
        }
        if (this.filterMap.size() == 0) {
            ItemStack itemStack = MachineLib.collectOneStack(iInventory, n, n2, null);
            if (itemStack == null) {
                return false;
            }
            this.buffer.addNewColor(itemStack, this.color);
            this.drainBuffer();
            return true;
        }
        int n3 = MachineLib.matchAnyStack(this.filterMap, iInventory, n, n2);
        if (n3 < 0) {
            return false;
        }
        ItemStack itemStack = MachineLib.collectOneStack(iInventory, n, n2, this.contents[n3]);
        this.buffer.addNewColor(itemStack, this.color);
        this.drainBuffer();
        return true;
    }

    @Override
    protected boolean suckFilter(ItemStack itemStack) {
        if (this.filterMap == null) {
            this.regenFilterMap();
        }
        if (this.filterMap.size() == 0) {
            return true;
        }
        return this.filterMap.containsKey(itemStack);
    }

    public int getSize() {
        return 9;
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
        return "Filter";
    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean a(EntityHuman entityHuman) {
        if (this.world.getTileEntity(this.x, this.y, this.z) != this) {
            return false;
        }
        return entityHuman.e((double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5) <= 64.0;
    }

    public void update() {
        this.filterMap = null;
        super.update();
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
            NBTTagCompound nBTTagCompound2 = (NBTTagCompound)nBTTagList.get(i);
            int n = nBTTagCompound2.getByte("Slot") & 255;
            if (n < 0 || n >= this.contents.length) continue;
            this.contents[n] = ItemStack.a((NBTTagCompound)nBTTagCompound2);
        }
        this.color = nBTTagCompound.getByte("color");
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
        NBTTagList nBTTagList = new NBTTagList();
        for (int i = 0; i < this.contents.length; ++i) {
            if (this.contents[i] == null) continue;
            NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
            nBTTagCompound2.setByte("Slot", (byte)i);
            this.contents[i].save(nBTTagCompound2);
            nBTTagList.add((NBTBase)nBTTagCompound2);
        }
        nBTTagCompound.set("Items", (NBTBase)nBTTagList);
        nBTTagCompound.setByte("color", this.color);
    }
}

