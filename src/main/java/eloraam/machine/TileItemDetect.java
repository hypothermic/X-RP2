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

import eloraam.core.*;
import forge.ISidedInventory;
import net.minecraft.server.*;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.List;

public class TileItemDetect
        extends TileMachine
        implements ITubeConnectable,
        IInventory,
        ISidedInventory {
    TubeBuffer buffer;
    int count;
    public byte mode;
    protected ItemStack[] contents;
    protected MachineLib.FilterMap filterMap;
    public List<HumanEntity> transaction = new ArrayList<HumanEntity>();

    public TileItemDetect() {
        this.buffer = new TubeBuffer(this);
        this.count = 0;
        this.mode = 0;
        this.filterMap = null;
        this.contents = new ItemStack[9];
    }

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

    void regenFilterMap() {
        this.filterMap = MachineLib.makeFilterMap(this.contents);
    }

    @Override
    public int getTubeConnectableSides() {
        return 3 << (this.Rotation & -2);
    }

    @Override
    public int getTubeConClass() {
        return 0;
    }

    @Override
    public boolean canRouteItems() {
        return false;
    }

    @Override
    public boolean tubeItemEnter(int n, int n2, TubeItem tubeItem) {
        if (n == this.Rotation && n2 == 2) {
            this.buffer.addBounce(tubeItem);
            this.Active = true;
            this.updateBlock();
            this.scheduleTick(5);
            return true;
        }
        if (n == (this.Rotation ^ 1) && n2 == 1) {
            if (!this.buffer.isEmpty()) {
                return false;
            }
            this.buffer.add(tubeItem);
            if (this.filterMap == null) {
                this.regenFilterMap();
            }
            if (this.filterMap.size() == 0 || this.filterMap.containsKey(tubeItem.item)) {
                if (this.mode == 0) {
                    this.count += tubeItem.item.count;
                } else if (this.mode == 1) {
                    ++this.count;
                }
            }
            this.Active = true;
            this.updateBlock();
            this.scheduleTick(5);
            this.drainBuffer();
            return true;
        }
        return false;
    }

    @Override
    public boolean tubeItemCanEnter(int n, int n2, TubeItem tubeItem) {
        if (n == this.Rotation && n2 == 2) {
            return true;
        }
        if (n == (this.Rotation ^ 1) && n2 == 1) {
            return this.buffer.isEmpty();
        }
        return false;
    }

    @Override
    public int tubeWeight(int n, int n2) {
        if (n == this.Rotation && n2 == 2) {
            return this.buffer.size();
        }
        return 0;
    }

    public int getStartInventorySide(int n) {
        return 0;
    }

    public int getSizeInventorySide(int n) {
        return n != this.Rotation && n != (this.Rotation ^ 1) ? 9 : 0;
    }

    public void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem tubeItem = this.buffer.getLast();
            if (!this.handleItem(tubeItem)) {
                this.buffer.plugged = true;
                if (this.mode == 2 && !this.Powered) {
                    this.Delay = false;
                    this.Powered = true;
                    this.count = 0;
                    this.updateBlockChange();
                }
                return;
            }
            this.buffer.pop();
            if (!this.buffer.plugged) continue;
            if (this.mode == 2 && !this.Powered) {
                this.Delay = false;
                this.Powered = true;
                this.count = 0;
                this.updateBlockChange();
            }
            return;
        }
        if (this.mode == 2 && this.Powered) {
            this.Powered = false;
            this.updateBlockChange();
        }
    }

    @Override
    public void q_() {
        super.q_();
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (this.mode == 2) {
            return;
        }
        if (this.Powered) {
            if (this.Delay) {
                this.Delay = false;
                this.dirtyBlock();
                return;
            }
            this.Powered = false;
            if (this.count > 0) {
                this.Delay = true;
            }
            this.updateBlockChange();
            return;
        }
        if (this.count == 0) {
            return;
        }
        if (this.Delay) {
            this.Delay = false;
            this.dirtyBlock();
            return;
        }
        --this.count;
        this.Powered = true;
        this.Delay = true;
        this.updateBlockChange();
    }

    @Override
    public boolean onBlockActivated(EntityHuman entityHuman) {
        if (entityHuman.isSneaking()) {
            return false;
        }
        if (CoreProxy.isClient(this.world)) {
            return true;
        }
        entityHuman.openGui((BaseMod) mod_RedPowerMachine.instance, 6, this.world, this.x, this.y, this.z);
        return true;
    }

    @Override
    public int getExtendedID() {
        return 4;
    }

    @Override
    public void onBlockRemoval() {
        this.buffer.onRemove(this);
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = this.contents[i];
            if (itemStack == null || itemStack.count <= 0) continue;
            CoreLib.dropItem(this.world, this.x, this.y, this.z, itemStack);
        }
    }

    @Override
    public void onTileTick() {
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (!this.buffer.isEmpty()) {
            this.drainBuffer();
            if (!this.buffer.isEmpty()) {
                this.scheduleTick(10);
            } else {
                this.scheduleTick(5);
            }
            return;
        }
        this.Active = false;
        this.updateBlock();
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
        return "Item Detector";
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
            NBTTagCompound nBTTagCompound2 = (NBTTagCompound) nBTTagList.get(i);
            int n = nBTTagCompound2.getByte("Slot") & 255;
            if (n < 0 || n >= this.contents.length) continue;
            this.contents[n] = ItemStack.a((NBTTagCompound) nBTTagCompound2);
        }
        this.buffer.readFromNBT(nBTTagCompound);
        this.count = nBTTagCompound.getInt("cnt");
        this.mode = nBTTagCompound.getByte("mode");
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
        this.buffer.writeToNBT(nBTTagCompound);
        nBTTagCompound.setInt("cnt", this.count);
        nBTTagCompound.setByte("mode", this.mode);
    }
}

