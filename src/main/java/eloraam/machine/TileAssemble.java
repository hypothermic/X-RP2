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
 *  net.minecraft.server.PlayerInventory
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 *  org.bukkit.craftbukkit.entity.CraftHumanEntity
 *  org.bukkit.entity.HumanEntity
 */
package eloraam.machine;

import eloraam.core.CoreLib;
import eloraam.core.CoreProxy;
import eloraam.core.IRedPowerWiring;
import eloraam.core.Packet211TileDesc;
import eloraam.core.RedPowerLib;
import eloraam.core.WorldCoord;
import eloraam.machine.TileDeployBase;
import forge.ISidedInventory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.BaseMod;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import net.minecraft.server.mod_RedPowerMachine;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

public class TileAssemble
extends TileDeployBase
implements IInventory,
ISidedInventory,
IRedPowerWiring {
    private ItemStack[] contents = new ItemStack[34];
    public byte select = 0;
    public byte mode = 0;
    public int skipSlots = 65534;
    public int ConMask = -1;
    public int PowerState = 0;
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

    @Override
    public int getExtendedID() {
        return 13;
    }

    @Override
    public boolean onBlockActivated(EntityHuman entityHuman) {
        if (entityHuman.isSneaking()) {
            return false;
        }
        if (CoreProxy.isClient(this.world)) {
            return true;
        }
        entityHuman.openGui((BaseMod)mod_RedPowerMachine.instance, 11, this.world, this.x, this.y, this.z);
        return true;
    }

    @Override
    public void onBlockRemoval() {
        for (int i = 0; i < 34; ++i) {
            ItemStack itemStack = this.contents[i];
            if (itemStack == null || itemStack.count <= 0) continue;
            CoreLib.dropItem(this.world, this.x, this.y, this.z, itemStack);
        }
    }

    @Override
    public void onBlockNeighborChange(int n) {
        this.ConMask = -1;
        if (this.mode == 0) {
            super.onBlockNeighborChange(n);
        }
        RedPowerLib.updateCurrent(this.world, this.x, this.y, this.z);
    }

    @Override
    public int getConnectionMask() {
        if (this.ConMask >= 0) {
            return this.ConMask;
        }
        this.ConMask = RedPowerLib.getConnections((IBlockAccess)this.world, this, this.x, this.y, this.z);
        return this.ConMask;
    }

    @Override
    public int getExtConnectionMask() {
        return 0;
    }

    @Override
    public int getPoweringMask(int n) {
        return 0;
    }

    @Override
    public int scanPoweringStrength(int n, int n2) {
        return 0;
    }

    @Override
    public int getCurrentStrength(int n, int n2) {
        return -1;
    }

    @Override
    public void updateCurrentStrength() {
        int n;
        if (this.mode != 1) {
            return;
        }
        for (n = 0; n < 16; ++n) {
            short s = (short)RedPowerLib.getMaxCurrentStrength(this.world, this.x, this.y, this.z, 1073741823, 0, n + 1);
            if (s > 0) {
                this.PowerState |= 1 << n;
                continue;
            }
            this.PowerState &= ~ (1 << n);
        }
        CoreLib.markBlockDirty(this.world, this.x, this.y, this.z);
        if (this.PowerState == 0) {
            if (!this.Active) {
                return;
            }
            this.scheduleTick(5);
            return;
        }
        if (this.Active) {
            return;
        }
        this.Active = true;
        this.updateBlock();
        n = Integer.numberOfTrailingZeros(this.PowerState);
        if (this.contents[n] != null) {
            WorldCoord worldCoord = new WorldCoord(this);
            worldCoord.step(this.Rotation ^ 1);
            int n2 = this.getMatchingStack(n);
            if (n2 >= 0) {
                this.enableTowardsActive(worldCoord, n2);
            }
        }
    }

    @Override
    public int getConnectClass(int n) {
        return this.mode != 0 ? 18 : 0;
    }

    protected void packInv(ItemStack[] arritemStack, int n) {
        int n2;
        for (n2 = 0; n2 < 36; ++n2) {
            arritemStack[n2] = TileAssemble.fakePlayer.inventory.getItem(n2);
            TileAssemble.fakePlayer.inventory.setItem(n2, null);
        }
        for (n2 = 0; n2 < 18; ++n2) {
            if (n == n2) {
                TileAssemble.fakePlayer.inventory.setItem(0, this.contents[16 + n2]);
                continue;
            }
            TileAssemble.fakePlayer.inventory.setItem(n2 + 9, this.contents[16 + n2]);
        }
    }

    protected void unpackInv(ItemStack[] arritemStack, int n) {
        int n2;
        for (n2 = 0; n2 < 18; ++n2) {
            this.contents[16 + n2] = n == n2 ? TileAssemble.fakePlayer.inventory.getItem(0) : TileAssemble.fakePlayer.inventory.getItem(n2 + 9);
        }
        for (n2 = 0; n2 < 36; ++n2) {
            TileAssemble.fakePlayer.inventory.setItem(n2, arritemStack[n2]);
        }
    }

    protected int getMatchingStack(int n) {
        for (int i = 0; i < 18; ++i) {
            ItemStack itemStack = this.contents[16 + i];
            if (this.contents[16 + i] == null || CoreLib.compareItemStack(this.contents[16 + i], this.contents[n]) != 0) continue;
            return i;
        }
        return -1;
    }

    @Override
    public void enableTowards(WorldCoord worldCoord) {
        int n;
        if (this.contents[this.select] != null && (n = this.getMatchingStack(this.select)) >= 0) {
            this.enableTowardsActive(worldCoord, n);
        }
        for (n = 0; n < 16; ++n) {
            this.select = (byte)(this.select + 1 & 15);
            if ((this.skipSlots & 1 << this.select) == 0 || this.select == 0) break;
        }
    }

    protected void enableTowardsActive(WorldCoord worldCoord, int n) {
        ItemStack[] arritemStack = new ItemStack[36];
        this.initPlayer();
        this.packInv(arritemStack, n);
        ItemStack itemStack = this.contents[16 + n];
        if (itemStack != null && itemStack.count > 0 && this.tryUseItemStack(itemStack, worldCoord.x, worldCoord.y, worldCoord.z, 0)) {
            if (fakePlayer.M()) {
                fakePlayer.N();
            }
            this.unpackInv(arritemStack, n);
            if (itemStack.count == 0) {
                this.contents[16 + n] = null;
            }
            this.update();
            return;
        }
        this.unpackInv(arritemStack, n);
    }

    public int getSize() {
        return 34;
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
        if (itemStack != null && n < 16) {
            this.skipSlots &= ~ (1 << n);
        }
        this.update();
    }

    public String getName() {
        return "Assembler";
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

    public void g() {
    }

    public void f() {
    }

    public int getStartInventorySide(int n) {
        return (n ^ 1) != this.Rotation ? 16 : 0;
    }

    public int getSizeInventorySide(int n) {
        return (n ^ 1) != this.Rotation ? 18 : 0;
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
        this.mode = nBTTagCompound.getByte("mode");
        this.select = nBTTagCompound.getByte("sel");
        this.skipSlots = nBTTagCompound.getShort("ssl") & 65535;
        this.PowerState = nBTTagCompound.getInt("psex");
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
        nBTTagCompound.setByte("mode", this.mode);
        nBTTagCompound.setByte("sel", this.select);
        nBTTagCompound.setShort("ssl", (short)this.skipSlots);
        nBTTagCompound.setInt("psex", this.PowerState);
    }

    @Override
    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        super.readFromPacket(packet211TileDesc);
        this.mode = (byte)packet211TileDesc.getByte();
    }

    @Override
    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        super.writeToPacket(packet211TileDesc);
        packet211TileDesc.addByte(this.mode);
    }
}

