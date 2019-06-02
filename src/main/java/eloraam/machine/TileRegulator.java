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

public class TileRegulator
        extends TileMachine
        implements ITubeConnectable,
        IInventory,
        ISidedInventory {
    TubeBuffer buffer;
    public byte mode;
    protected ItemStack[] contents;
    protected MachineLib.FilterMap inputMap;
    protected MachineLib.FilterMap outputMap;
    public int color;
    public List<HumanEntity> transaction = new ArrayList<HumanEntity>();

    public TileRegulator() {
        this.buffer = new TubeBuffer(this);
        this.mode = 0;
        this.inputMap = null;
        this.outputMap = null;
        this.color = 0;
        this.contents = new ItemStack[27];
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
        this.inputMap = MachineLib.makeFilterMap(this.contents, 0, 9);
        this.outputMap = MachineLib.makeFilterMap(this.contents, 18, 9);
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
            int n3 = this.inCount(tubeItem.item);
            if (n3 == 0) {
                return false;
            }
            boolean bl = true;
            ItemStack itemStack = tubeItem.item;
            if (n3 < itemStack.count) {
                bl = false;
                itemStack = itemStack.a(n3);
            }
            if (MachineLib.addToInventoryCore(this, itemStack, 9, 9, true)) {
                this.update();
                this.scheduleTick(2);
                this.dirtyBlock();
                return bl;
            }
            this.dirtyBlock();
            return false;
        }
        return false;
    }

    @Override
    public boolean tubeItemCanEnter(int n, int n2, TubeItem tubeItem) {
        if (n == this.Rotation && n2 == 2) {
            return true;
        }
        if (n == (this.Rotation ^ 1) && n2 == 1) {
            if (this.inCount(tubeItem.item) == 0) {
                return false;
            }
            return MachineLib.addToInventoryCore(this, tubeItem.item, 9, 9, false);
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
        return 9;
    }

    public int getSizeInventorySide(int n) {
        return n != this.Rotation && n != (this.Rotation ^ 1) ? 9 : 0;
    }

    public void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem tubeItem = this.buffer.getLast();
            if (!this.handleItem(tubeItem)) {
                this.buffer.plugged = true;
                return;
            }
            this.buffer.pop();
            if (!this.buffer.plugged) continue;
            return;
        }
    }

    @Override
    public void q_() {
        super.q_();
        if (!this.isTickScheduled()) {
            this.scheduleTick(10);
        }
    }

    @Override
    public boolean isPoweringTo(int n) {
        if (n == (this.Rotation ^ 1)) {
            return false;
        }
        return this.Powered;
    }

    @Override
    public boolean onBlockActivated(EntityHuman entityHuman) {
        if (entityHuman.isSneaking()) {
            return false;
        }
        if (CoreProxy.isClient(this.world)) {
            return true;
        }
        entityHuman.openGui((BaseMod) mod_RedPowerMachine.instance, 9, this.world, this.x, this.y, this.z);
        return true;
    }

    @Override
    public int getExtendedID() {
        return 10;
    }

    @Override
    public void onBlockRemoval() {
        this.buffer.onRemove(this);
        for (int i = 0; i < 27; ++i) {
            ItemStack itemStack = this.contents[i];
            if (itemStack == null || itemStack.count <= 0) continue;
            CoreLib.dropItem(this.world, this.x, this.y, this.z, itemStack);
        }
    }

    private int[] scanInput() {
        if (this.inputMap == null) {
            this.regenFilterMap();
        }
        if (this.inputMap.size() == 0) {
            return null;
        }
        int[] arrn = MachineLib.genMatchCounts(this.inputMap);
        MachineLib.decMatchCounts(this.inputMap, arrn, this, 9, 9);
        return arrn;
    }

    private int inCount(ItemStack itemStack) {
        if (this.inputMap == null) {
            this.regenFilterMap();
        }
        if (this.inputMap.size() == 0) {
            return 0;
        }
        if (!this.inputMap.containsKey(itemStack)) {
            return 0;
        }
        int[] arrn = MachineLib.genMatchCounts(this.inputMap);
        MachineLib.decMatchCounts(this.inputMap, arrn, this, 9, 9);
        return MachineLib.decMatchCount(this.inputMap, arrn, itemStack);
    }

    // X-RP: modified this method quite a bit.
    private int[] scanOutput() {
        Object arrn;
        WorldCoord worldCoord = new WorldCoord(this);
        worldCoord.step(this.Rotation);
        IInventory iInventory = MachineLib.getInventory(this.world, worldCoord);
        if (iInventory == null) {
            return null;
        }
        int n = 0;
        int n2 = iInventory.getSize();
        if (iInventory instanceof ISidedInventory) {
            arrn = iInventory;
            int n3 = (this.Rotation ^ 1) & 255;
            n = ((ISidedInventory) arrn).getStartInventorySide(n3);
            n2 = ((ISidedInventory) arrn).getSizeInventorySide(n3);
        }
        if (this.outputMap == null) {
            this.regenFilterMap();
        }
        if (this.outputMap.size() == 0) {
            return null;
        }
        int[] xo2 = MachineLib.genMatchCounts(this.outputMap);
        MachineLib.decMatchCounts(this.outputMap, xo2, iInventory, n, n2);
        return xo2;
    }

    private void handleTransfer(int[] arrn) {
        if (this.mode == 0 || arrn == null) {
            for (int i = 0; i < 9; ++i) {
                ItemStack itemStack = this.contents[9 + i];
                if (itemStack == null || itemStack.count == 0) continue;
                this.buffer.addNewColor(itemStack, this.color);
                this.contents[9 + i] = null;
            }
        } else {
            boolean bl = false;
            for (int i = 0; i < 9; ++i) {
                while (arrn[i] > 0) {
                    ItemStack itemStack = this.contents[18 + i].cloneItemStack();
                    int n = Math.min(itemStack.count, arrn[i]);
                    int[] arrn2 = arrn;
                    int n2 = i;
                    arrn2[n2] = arrn2[n2] - n;
                    itemStack.count = n;
                    ItemStack itemStack2 = MachineLib.collectOneStack(this, 9, 9, itemStack);
                    if (itemStack2 == null) continue;
                    this.buffer.addNewColor(itemStack2, this.color);
                    bl = true;
                }
            }
            if (!bl) {
                return;
            }
        }
        this.update();
        this.Powered = true;
        this.Active = true;
        this.updateBlockChange();
        this.drainBuffer();
        if (!this.buffer.isEmpty()) {
            this.scheduleTick(10);
        } else {
            this.scheduleTick(5);
        }
    }

    @Override
    public void onTileTick() {
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (this.Active) {
            if (!this.buffer.isEmpty()) {
                this.Powered = true;
                this.drainBuffer();
                this.updateBlockChange();
                if (!this.buffer.isEmpty()) {
                    this.scheduleTick(10);
                }
                return;
            }
            this.Active = false;
            this.updateBlock();
        }
        if (this.Powered) {
            int[] arrn = this.scanOutput();
            if (arrn == null) {
                this.Powered = false;
                this.updateBlockChange();
                return;
            }
            if (MachineLib.isMatchEmpty(arrn)) {
                return;
            }
            int[] arrn2 = this.scanInput();
            if (arrn2 == null || !MachineLib.isMatchEmpty(arrn2)) {
                this.Powered = false;
                this.updateBlockChange();
                return;
            }
            this.handleTransfer(arrn);
            return;
        }
        int[] arrn = this.scanOutput();
        if (arrn != null && MachineLib.isMatchEmpty(arrn)) {
            this.Powered = true;
            this.updateBlockChange();
            return;
        }
        int[] arrn3 = this.scanInput();
        if (arrn3 != null && MachineLib.isMatchEmpty(arrn3)) {
            this.handleTransfer(arrn);
            return;
        }
        if (arrn != null && this.mode == 1) {
            this.handleTransfer(arrn);
            return;
        }
    }

    public int getSize() {
        return 27;
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
        return "Regulator";
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
        this.inputMap = null;
        this.outputMap = null;
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
        this.mode = nBTTagCompound.getByte("mode");
        this.color = nBTTagCompound.getByte("col");
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
        nBTTagCompound.setByte("mode", this.mode);
        nBTTagCompound.setByte("col", (byte) this.color);
    }
}

