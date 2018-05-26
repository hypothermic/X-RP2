/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  forge.ISidedInventory
 *  net.minecraft.server.BaseMod
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.EntityLiving
 *  net.minecraft.server.FurnaceRecipes
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.IInventory
 *  net.minecraft.server.Item
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
import eloraam.core.BluePowerConductor;
import eloraam.core.BluePowerEndpoint;
import eloraam.core.CoreLib;
import eloraam.core.CoreProxy;
import eloraam.core.IBluePowerConnectable;
import eloraam.core.RedPowerLib;
import forge.ISidedInventory;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.BaseMod;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.FurnaceRecipes;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.IInventory;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import net.minecraft.server.mod_RedPowerMachine;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

public class TileBlueFurnace
extends TileAppliance
implements IInventory,
ISidedInventory,
IBluePowerConnectable {
    BluePowerEndpoint cond;
    private ItemStack[] contents;
    public int cooktime;
    public int ConMask;
    public List<HumanEntity> transaction = new ArrayList<HumanEntity>();

    public TileBlueFurnace() {
        this.cond = new BluePowerEndpoint(){

            @Override
            public TileEntity getParent() {
                return TileBlueFurnace.this;
            }
        };
        this.cooktime = 0;
        this.ConMask = -1;
        this.contents = new ItemStack[2];
    }

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
    public int getConnectableMask() {
        return 1073741823;
    }

    @Override
    public int getConnectClass(int n) {
        return 64;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public BluePowerConductor getBlueConductor() {
        return this.cond;
    }

    void updateLight() {
        this.world.v(this.x, this.y, this.z);
    }

    @Override
    public int getExtendedID() {
        return 1;
    }

    @Override
    public void q_() {
        super.q_();
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (this.ConMask < 0) {
            this.ConMask = RedPowerLib.getConnections((IBlockAccess)this.world, this, this.x, this.y, this.z);
            this.cond.recache(this.ConMask, 0);
        }
        this.cond.iterate();
        this.dirtyBlock();
        if (this.cond.getVoltage() < 60.0) {
            if (this.Active && this.cond.Flow == 0) {
                this.Active = false;
                this.updateBlock();
                this.updateLight();
            }
            return;
        }
        boolean bl = this.canSmelt();
        if (bl) {
            if (!this.Active) {
                this.Active = true;
                this.updateBlock();
                this.updateLight();
            }
            ++this.cooktime;
            this.cond.drawPower(1000.0);
            if (this.cooktime >= 100) {
                this.cooktime = 0;
                this.smeltItem();
                this.update();
            }
        } else {
            if (this.Active) {
                this.Active = false;
                this.updateBlock();
                this.updateLight();
            }
            this.cooktime = 0;
        }
    }

    boolean canSmelt() {
        if (this.contents[0] == null) {
            return false;
        }
        ItemStack itemStack = FurnaceRecipes.getInstance().getSmeltingResult(this.contents[0]);
        if (itemStack == null) {
            return false;
        }
        if (this.contents[1] == null) {
            return true;
        }
        if (!this.contents[1].doMaterialsMatch(itemStack)) {
            return false;
        }
        int n = this.contents[1].count + itemStack.count;
        return n <= this.getMaxStackSize() && n <= itemStack.getMaxStackSize();
    }

    void smeltItem() {
        if (!this.canSmelt()) {
            return;
        }
        ItemStack itemStack = FurnaceRecipes.getInstance().getSmeltingResult(this.contents[0]);
        if (this.contents[1] == null) {
            this.contents[1] = itemStack.cloneItemStack();
        } else if (this.contents[1].doMaterialsMatch(itemStack)) {
            this.contents[1].count += itemStack.count;
        }
        if (this.contents[0].getItem().k()) {
            this.contents[0] = new ItemStack(this.contents[0].getItem().j());
        } else {
            --this.contents[0].count;
        }
        if (this.contents[0].count <= 0) {
            this.contents[0] = null;
        }
    }

    int getCookScaled(int n) {
        return this.cooktime * n / 100;
    }

    @Override
    public boolean onBlockActivated(EntityHuman entityHuman) {
        if (entityHuman.isSneaking()) {
            return false;
        }
        if (CoreProxy.isClient(this.world)) {
            return true;
        }
        entityHuman.openGui((BaseMod)mod_RedPowerMachine.instance, 3, this.world, this.x, this.y, this.z);
        return true;
    }

    @Override
    public void onBlockPlacedBy(EntityLiving entityLiving) {
        this.Rotation = (int)Math.floor((double)(entityLiving.yaw * 4.0f / 360.0f) + 0.5) & 3;
    }

    @Override
    public void onBlockRemoval() {
        for (int i = 0; i < 2; ++i) {
            ItemStack itemStack = this.contents[i];
            if (itemStack == null || itemStack.count <= 0) continue;
            CoreLib.dropItem(this.world, this.x, this.y, this.z, itemStack);
        }
    }

    @Override
    public void onBlockNeighborChange(int n) {
        this.ConMask = -1;
    }

    public int getSize() {
        return 2;
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
        return "BlueFurnace";
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
        int n2 = CoreLib.rotToSide(this.Rotation);
        if (n == n2) {
            return 0;
        }
        return n != (n2 ^ 1) ? 0 : 1;
    }

    public int getSizeInventorySide(int n) {
        int n2 = CoreLib.rotToSide(this.Rotation);
        if (n == n2) {
            return 1;
        }
        return n != (n2 ^ 1) ? 0 : 1;
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
        this.cooktime = nBTTagCompound.getShort("CookTime");
        this.cond.readFromNBT(nBTTagCompound);
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
        nBTTagCompound.setShort("CookTime", (short)this.cooktime);
        this.cond.writeToNBT(nBTTagCompound);
    }

}

