/* X-RP - decompiled with CFR */
package eloraam.base;

import eloraam.core.CoreLib;
import eloraam.core.CoreProxy;
import eloraam.core.CraftLib;
import forge.ISidedInventory;
import net.minecraft.server.*;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.List;

public class TileAlloyFurnace extends TileAppliance implements IInventory, ISidedInventory {

    private ItemStack[] contents = new ItemStack[11];
    public int totalburn = 0;
    public int burntime = 0;
    public int cooktime = 0;
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

    void updateLight() {
        this.world.v(this.x, this.y, this.z);
    }

    @Override
    public int getExtendedID() {
        return 0;
    }

    @Override
    public void q_() {
        super.q_();
        boolean bl = false;
        if (this.burntime > 0) {
            --this.burntime;
            if (this.burntime == 0) {
                bl = true;
                this.Active = false;
            }
        }
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        boolean bl2 = this.canSmelt();
        if (this.burntime == 0 && bl2 && this.contents[9] != null) {
            this.burntime = this.totalburn = CoreLib.getBurnTime(this.contents[9]);
            if (this.burntime > 0) {
                this.Active = true;
                if (this.contents[9].getItem().k()) {
                    this.contents[9] = new ItemStack(this.contents[9].getItem().j());
                } else {
                    --this.contents[9].count;
                }
                if (this.contents[9].count == 0) {
                    this.contents[9] = null;
                }
                if (!bl) {
                    this.update();
                    this.updateBlock();
                    this.updateLight();
                }
            }
        }
        if (this.burntime > 0 && bl2) {
            ++this.cooktime;
            if (this.cooktime == 200) {
                this.cooktime = 0;
                this.smeltItem();
                this.update();
            }
        } else {
            this.cooktime = 0;
        }
        if (bl) {
            this.updateBlock();
            this.updateLight();
        }
    }

    boolean canSmelt() {
        ItemStack itemStack = CraftLib.getAlloyResult(this.contents, 0, 9, false);
        if (itemStack == null) {
            return false;
        }
        if (this.contents[10] == null) {
            return true;
        }
        if (!this.contents[10].doMaterialsMatch(itemStack)) {
            return false;
        }
        int n = this.contents[10].count + itemStack.count;
        return n <= this.getMaxStackSize() && n <= itemStack.getMaxStackSize();
    }

    void smeltItem() {
        if (!this.canSmelt()) {
            return;
        }
        ItemStack itemStack = CraftLib.getAlloyResult(this.contents, 0, 9, true);
        if (this.contents[10] == null) {
            this.contents[10] = itemStack.cloneItemStack();
        } else {
            this.contents[10].count += itemStack.count;
        }
    }

    int getCookScaled(int n) {
        return this.cooktime * n / 200;
    }

    int getBurnScaled(int n) {
        if (this.totalburn == 0) {
            return 0;
        }
        return this.burntime * n / this.totalburn;
    }

    @Override
    public boolean onBlockActivated(EntityHuman entityHuman) {
        if (entityHuman.isSneaking()) {
            return false;
        }
        if (CoreProxy.isClient(this.world)) {
            return true;
        }
        entityHuman.openGui((BaseMod) mod_RedPowerCore.instance, 1, this.world, this.x, this.y, this.z);
        return true;
    }

    @Override
    public void onBlockPlacedBy(EntityLiving entityLiving) {
        this.Rotation = (int) Math.floor((double) (entityLiving.yaw * 4.0f / 360.0f) + 0.5) & 3;
    }

    @Override
    public void onBlockRemoval() {
        for (int i = 0; i < 11; ++i) {
            ItemStack itemStack = this.contents[i];
            if (itemStack == null || itemStack.count <= 0)
                continue;
            CoreLib.dropItem(this.world, this.x, this.y, this.z, itemStack);
        }
    }

    public int getSize() {
        return 11;
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
        return "AlloyFurnace";
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
        if (n == 1) {
            return 0;
        }
        int n2 = CoreLib.rotToSide(this.Rotation);
        if (n == n2) {
            return 9;
        }
        return n != (n2 ^ 1) ? 0 : 10;
    }

    public int getSizeInventorySide(int n) {
        if (n == 1) {
            return 9;
        }
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
            NBTTagCompound nBTTagCompound2 = (NBTTagCompound) nBTTagList.get(i);
            int n = nBTTagCompound2.getByte("Slot") & 255;
            if (n < 0 || n >= this.contents.length)
                continue;
            this.contents[n] = ItemStack.a((NBTTagCompound) nBTTagCompound2);
        }
        this.totalburn = nBTTagCompound.getShort("TotalBurn");
        this.burntime = nBTTagCompound.getShort("BurnTime");
        this.cooktime = nBTTagCompound.getShort("CookTime");
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
        nBTTagCompound.setShort("TotalBurn", (short) this.totalburn);
        nBTTagCompound.setShort("BurnTime", (short) this.burntime);
        nBTTagCompound.setShort("CookTime", (short) this.cooktime);
    }
}
