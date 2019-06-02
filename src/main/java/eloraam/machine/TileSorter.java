/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  forge.ISidedInventory
 *  net.minecraft.server.BaseMod
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.IBlockAccess
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

public class TileSorter
        extends TileTranspose
        implements IInventory,
        ISidedInventory,
        IBluePowerConnectable {
    BluePowerEndpoint cond;
    public int ConMask;
    private ItemStack[] contents;
    public byte[] colors;
    public byte mode;
    public byte defcolor;
    public byte draining;
    public byte column;
    protected MachineLib.FilterMap filterMap;
    TubeBuffer[] channelBuffers;
    public List<HumanEntity> transaction = new ArrayList<HumanEntity>();

    public TileSorter() {
        this.cond = new BluePowerEndpoint() {

            @Override
            public TileEntity getParent() {
                return TileSorter.this;
            }
        };
        this.ConMask = -1;
        this.mode = 0;
        this.defcolor = 0;
        this.draining = -1;
        this.column = 0;
        this.filterMap = null;
        this.contents = new ItemStack[40];
        this.colors = new byte[8];
        this.channelBuffers = new TubeBuffer[8];
        for (int i = 0; i < 8; ++i) {
            this.channelBuffers[i] = new TubeBuffer(this);
        }
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
    public int getConnectableMask() {
        return 1073741823;
    }

    @Override
    public int getConnectClass(int n) {
        return 65;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public BluePowerConductor getBlueConductor() {
        return this.cond;
    }

    public int getStartInventorySide(int n) {
        return 0;
    }

    public int getSizeInventorySide(int n) {
        return 0;
    }

    @Override
    public void q_() {
        super.q_();
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (this.ConMask < 0) {
            this.ConMask = RedPowerLib.getConnections((IBlockAccess) this.world, this, this.x, this.y, this.z);
            this.cond.recache(this.ConMask, 0);
        }
        this.cond.iterate();
        this.dirtyBlock();
        if (this.cond.Flow == 0) {
            if (this.Charged) {
                this.Charged = false;
                this.updateBlock();
            }
        } else if (!this.Charged) {
            this.Charged = true;
            this.updateBlock();
        }
    }

    @Override
    public boolean onBlockActivated(EntityHuman entityHuman) {
        if (entityHuman.isSneaking()) {
            return false;
        }
        if (CoreProxy.isClient(this.world)) {
            return true;
        }
        entityHuman.openGui((BaseMod) mod_RedPowerMachine.instance, 5, this.world, this.x, this.y, this.z);
        return true;
    }

    @Override
    public int getExtendedID() {
        return 5;
    }

    @Override
    public void onBlockRemoval() {
        int n;
        super.onBlockRemoval();
        for (n = 0; n < 8; ++n) {
            this.channelBuffers[n].onRemove(this);
        }
        for (n = 0; n < 40; ++n) {
            ItemStack itemStack = this.contents[n];
            if (itemStack == null || itemStack.count <= 0) continue;
            CoreLib.dropItem(this.world, this.x, this.y, this.z, itemStack);
        }
    }

    @Override
    public void onBlockNeighborChange(int n) {
        this.ConMask = -1;
        super.onBlockNeighborChange(n);
    }

    protected int getColumnMatch(ItemStack itemStack) {
        if (this.filterMap == null) {
            this.regenFilterMap();
        }
        if (this.filterMap.size() == 0) {
            return -2;
        }
        int n = this.filterMap.firstMatch(itemStack);
        if (n < 0) {
            return n;
        }
        return n & 7;
    }

    protected void fireMatch() {
        this.Active = true;
        this.updateBlock();
        this.scheduleTick(5);
    }

    protected boolean tryDrainBuffer(TubeBuffer tubeBuffer) {
        if (tubeBuffer.isEmpty()) {
            return false;
        }
        while (!tubeBuffer.isEmpty()) {
            TubeItem tubeItem = tubeBuffer.getLast();
            if (this.stuffCart(tubeItem.item)) {
                tubeBuffer.pop();
                continue;
            }
            if (!this.handleItem(tubeItem)) {
                tubeBuffer.plugged = true;
                return true;
            }
            tubeBuffer.pop();
            if (!tubeBuffer.plugged) continue;
            return true;
        }
        return true;
    }

    protected boolean tryDrainBuffer() {
        for (int i = 0; i < 9; ++i) {
            TubeBuffer tubeBuffer;
            this.draining = (byte) (this.draining + 1);
            if (this.draining > 7) {
                this.draining = -1;
                tubeBuffer = this.buffer;
            } else {
                tubeBuffer = this.channelBuffers[this.draining];
            }
            if (!this.tryDrainBuffer(tubeBuffer)) continue;
            return false;
        }
        return true;
    }

    protected boolean isBufferEmpty() {
        if (!this.buffer.isEmpty()) {
            return false;
        }
        for (int i = 0; i < 8; ++i) {
            if (this.channelBuffers[i].isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void drainBuffer() {
        this.tryDrainBuffer();
    }

    @Override
    public void onTileTick() {
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (!this.Active) {
            return;
        }
        if (!this.tryDrainBuffer()) {
            if (this.isBufferEmpty()) {
                this.scheduleTick(5);
            } else {
                this.scheduleTick(10);
            }
            return;
        }
        if (!this.Powered) {
            this.Active = false;
            this.updateBlock();
        }
    }

    @Override
    public boolean tubeItemEnter(int n, int n2, TubeItem tubeItem) {
        if (n == this.Rotation && n2 == 2) {
            int n3 = this.getColumnMatch(tubeItem.item);
            TubeBuffer tubeBuffer = this.buffer;
            if (n3 >= 0 && this.mode > 1) {
                tubeBuffer = this.channelBuffers[n3];
            }
            tubeBuffer.addBounce(tubeItem);
            this.fireMatch();
            return true;
        }
        if (n == (this.Rotation ^ 1) && n2 == 1) {
            if (this.Powered) {
                return false;
            }
            if (this.cond.getVoltage() < 60.0) {
                return false;
            }
            int n4 = this.getColumnMatch(tubeItem.item);
            TubeBuffer tubeBuffer = this.buffer;
            if (n4 >= 0 && this.mode > 1) {
                tubeBuffer = this.channelBuffers[n4];
            }
            if (!tubeBuffer.isEmpty()) {
                return false;
            }
            if (n4 < 0) {
                if (this.mode == 4) {
                    this.cond.drawPower(25 * tubeItem.item.count);
                    tubeBuffer.addNewColor(tubeItem.item, this.defcolor);
                    this.fireMatch();
                    this.tryDrainBuffer(tubeBuffer);
                    return true;
                }
                if (n4 == -2) {
                    this.cond.drawPower(25 * tubeItem.item.count);
                    tubeBuffer.addNewColor(tubeItem.item, 0);
                    this.fireMatch();
                    this.tryDrainBuffer(tubeBuffer);
                    return true;
                }
                return false;
            }
            this.cond.drawPower(25 * tubeItem.item.count);
            tubeBuffer.addNewColor(tubeItem.item, this.colors[n4]);
            this.fireMatch();
            this.tryDrainBuffer(tubeBuffer);
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
            if (this.Powered) {
                return false;
            }
            if (this.cond.getVoltage() < 60.0) {
                return false;
            }
            int n3 = this.getColumnMatch(tubeItem.item);
            TubeBuffer tubeBuffer = this.buffer;
            if (n3 >= 0 && this.mode > 1) {
                tubeBuffer = this.channelBuffers[n3];
            }
            if (!tubeBuffer.isEmpty()) {
                return false;
            }
            if (n3 < 0) {
                if (this.mode == 4) {
                    return true;
                }
                return n3 == -2;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void addToBuffer(ItemStack itemStack) {
        int n = this.getColumnMatch(itemStack);
        TubeBuffer tubeBuffer = this.buffer;
        if (n >= 0 && this.mode > 1) {
            tubeBuffer = this.channelBuffers[n];
        }
        if (n < 0) {
            if (this.mode == 4) {
                tubeBuffer.addNewColor(itemStack, this.defcolor);
                return;
            }
            tubeBuffer.addNewColor(itemStack, 0);
            return;
        }
        tubeBuffer.addNewColor(itemStack, this.colors[n]);
    }

    private void stepColumn() {
        for (int i = 0; i < 8; ++i) {
            this.column = (byte) (this.column + 1);
            if (this.column > 7) {
                this.column = 0;
            }
            for (int j = 0; j < 5; ++j) {
                ItemStack itemStack = this.contents[j * 8 + this.column];
                if (itemStack == null || itemStack.count == 0) continue;
                return;
            }
        }
        this.column = 0;
    }

    private void checkColumn() {
        for (int i = 0; i < 5; ++i) {
            ItemStack itemStack = this.contents[i * 8 + this.column];
            if (itemStack == null || itemStack.count == 0) continue;
            return;
        }
        this.stepColumn();
        this.dirtyBlock();
    }

    @Override
    protected boolean handleExtract(IInventory iInventory, int n, int n2) {
        if (this.cond.getVoltage() < 60.0) {
            return false;
        }
        if (this.filterMap == null) {
            this.regenFilterMap();
        }
        if (this.filterMap.size() == 0) {
            ItemStack itemStack = MachineLib.collectOneStack(iInventory, n, n2, null);
            if (itemStack == null) {
                return false;
            }
            if (this.mode == 4) {
                this.buffer.addNewColor(itemStack, this.defcolor);
            } else {
                this.buffer.addNew(itemStack);
            }
            this.cond.drawPower(25 * itemStack.count);
            this.drainBuffer();
            return true;
        }
        switch (this.mode) {
            case 0: {
                this.checkColumn();
                int n3 = MachineLib.matchAnyStackCol(this.filterMap, iInventory, n, n2, this.column);
                if (n3 < 0) {
                    return false;
                }
                ItemStack itemStack = MachineLib.collectOneStack(iInventory, n, n2, this.contents[n3]);
                this.buffer.addNewColor(itemStack, this.colors[n3 & 7]);
                this.cond.drawPower(25 * itemStack.count);
                this.stepColumn();
                this.drainBuffer();
                return true;
            }
            case 1: {
                this.checkColumn();
                if (!MachineLib.matchAllCol(this.filterMap, iInventory, n, n2, this.column)) {
                    return false;
                }
                for (int i = 0; i < 5; ++i) {
                    ItemStack itemStack = this.contents[i * 8 + this.column];
                    if (itemStack == null || itemStack.count == 0) continue;
                    ItemStack itemStack2 = MachineLib.collectOneStack(iInventory, n, n2, itemStack);
                    this.buffer.addNewColor(itemStack2, this.colors[this.column]);
                    this.cond.drawPower(25 * itemStack2.count);
                }
                this.stepColumn();
                this.drainBuffer();
                return true;
            }
            case 2: {
                int n4;
                for (n4 = 0; n4 < 8 && !MachineLib.matchAllCol(this.filterMap, iInventory, n, n2, n4); ++n4) {
                }
                if (n4 == 8) {
                    return false;
                }
                for (int i = 0; i < 5; ++i) {
                    ItemStack itemStack = this.contents[i * 8 + n4];
                    if (itemStack == null || itemStack.count == 0) continue;
                    ItemStack itemStack3 = MachineLib.collectOneStack(iInventory, n, n2, itemStack);
                    this.channelBuffers[n4].addNewColor(itemStack3, this.colors[n4]);
                    this.cond.drawPower(25 * itemStack3.count);
                }
                this.stepColumn();
                this.drainBuffer();
                return true;
            }
            case 3: {
                int n5 = MachineLib.matchAnyStack(this.filterMap, iInventory, n, n2);
                if (n5 < 0) {
                    return false;
                }
                ItemStack itemStack = MachineLib.collectOneStack(iInventory, n, n2, this.contents[n5]);
                this.channelBuffers[n5 & 7].addNewColor(itemStack, this.colors[n5 & 7]);
                this.cond.drawPower(25 * itemStack.count);
                this.drainBuffer();
                return true;
            }
            case 4: {
                ItemStack itemStack;
                int n6 = MachineLib.matchAnyStack(this.filterMap, iInventory, n, n2);
                if (n6 < 0) {
                    itemStack = MachineLib.collectOneStack(iInventory, n, n2, null);
                    if (itemStack == null) {
                        return false;
                    }
                    this.buffer.addNewColor(itemStack, this.defcolor);
                } else {
                    itemStack = MachineLib.collectOneStack(iInventory, n, n2, this.contents[n6]);
                    this.channelBuffers[n6 & 7].addNewColor(itemStack, this.colors[n6 & 7]);
                }
                this.cond.drawPower(25 * itemStack.count);
                this.drainBuffer();
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean suckFilter(ItemStack itemStack) {
        if (this.cond.getVoltage() < 60.0) {
            return false;
        }
        if (this.filterMap == null) {
            this.regenFilterMap();
        }
        int n = this.getColumnMatch(itemStack);
        TubeBuffer tubeBuffer = this.buffer;
        if (n >= 0 && this.mode > 1) {
            tubeBuffer = this.channelBuffers[n];
        }
        if (tubeBuffer.plugged) {
            return false;
        }
        if (n < 0) {
            if (this.mode == 4 || n == -2) {
                this.cond.drawPower(25 * itemStack.count);
                return true;
            }
            return false;
        }
        this.cond.drawPower(25 * itemStack.count);
        return true;
    }

    public int getSize() {
        return 40;
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
        return "Sorter";
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
        int n;
        super.a(nBTTagCompound);
        NBTTagList nBTTagList = nBTTagCompound.getList("Items");
        this.contents = new ItemStack[this.getSize()];
        for (int i = 0; i < nBTTagList.size(); ++i) {
            NBTTagCompound nBTTagCompound2 = (NBTTagCompound) nBTTagList.get(i);
            n = nBTTagCompound2.getByte("Slot") & 255;
            if (n < 0 || n >= this.contents.length) continue;
            this.contents[n] = ItemStack.a((NBTTagCompound) nBTTagCompound2);
        }
        this.column = nBTTagCompound.getByte("coln");
        byte[] arrby = nBTTagCompound.getByteArray("cols");
        if (arrby.length >= 8) {
            for (int i = 0; i < 8; ++i) {
                this.colors[i] = arrby[i];
            }
        }
        this.mode = nBTTagCompound.getByte("mode");
        this.draining = nBTTagCompound.getByte("drain");
        if (this.mode == 4) {
            this.defcolor = nBTTagCompound.getByte("defc");
        }
        this.cond.readFromNBT(nBTTagCompound);
        NBTTagList nBTTagList2 = nBTTagCompound.getList("buffers");
        for (n = 0; n < nBTTagList2.size(); ++n) {
            NBTTagCompound nBTTagCompound3 = (NBTTagCompound) nBTTagList2.get(n);
            this.channelBuffers[n].readFromNBT(nBTTagCompound3);
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
        nBTTagCompound.setByte("coln", this.column);
        nBTTagCompound.set("Items", (NBTBase) nBTTagList);
        nBTTagCompound.setByteArray("cols", this.colors);
        nBTTagCompound.setByte("mode", this.mode);
        nBTTagCompound.setByte("drain", this.draining);
        if (this.mode == 4) {
            nBTTagCompound.setByte("defc", this.defcolor);
        }
        this.cond.writeToNBT(nBTTagCompound);
        NBTTagList nBTTagList2 = new NBTTagList();
        for (int i = 0; i < 8; ++i) {
            NBTTagCompound nBTTagCompound3 = new NBTTagCompound();
            this.channelBuffers[i].writeToNBT(nBTTagCompound3);
            nBTTagList2.add((NBTBase) nBTTagCompound3);
        }
        nBTTagCompound.set("buffers", (NBTBase) nBTTagList2);
    }

}

