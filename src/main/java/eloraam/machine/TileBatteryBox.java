/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  forge.ISidedInventory
 *  net.minecraft.server.BaseMod
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.IInventory
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.NBTBase
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.NBTTagList
 *  net.minecraft.server.Packet
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 *  org.bukkit.craftbukkit.entity.CraftHumanEntity
 *  org.bukkit.entity.HumanEntity
 */
package eloraam.machine;

import eloraam.core.BluePowerConductor;
import eloraam.core.CoreLib;
import eloraam.core.CoreProxy;
import eloraam.core.IBluePowerConnectable;
import eloraam.core.IFrameSupport;
import eloraam.core.IHandlePackets;
import eloraam.core.Packet211TileDesc;
import eloraam.core.RedPowerLib;
import eloraam.core.TileExtended;
import eloraam.machine.BlockMachine;
import forge.ISidedInventory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.BaseMod;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.IInventory;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.Packet;
import net.minecraft.server.RedPowerMachine;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import net.minecraft.server.mod_RedPowerMachine;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

public class TileBatteryBox
extends TileExtended
implements IHandlePackets,
IInventory,
IBluePowerConnectable,
ISidedInventory,
IFrameSupport {
    BluePowerConductor cond;
    protected ItemStack[] contents;
    public int Charge;
    public int Storage;
    public int ConMask;
    public boolean Powered;
    public List<HumanEntity> transaction = new ArrayList<HumanEntity>();

    public TileBatteryBox() {
        this.cond = new BluePowerConductor(){

            @Override
            public TileEntity getParent() {
                return TileBatteryBox.this;
            }

            @Override
            public double getInvCap() {
                return 0.25;
            }
        };
        this.Charge = 0;
        this.Storage = 0;
        this.ConMask = -1;
        this.Powered = false;
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
        return n != 0 ? 0 : 1;
    }

    public int getSizeInventorySide(int n) {
        return n < 2 ? 1 : 0;
    }

    @Override
    public int getExtendedID() {
        return 6;
    }

    @Override
    public int getBlockID() {
        return RedPowerMachine.blockMachine.id;
    }

    public int getMaxStorage() {
        return 6000;
    }

    public int getStorageForRender() {
        return this.Storage * 8 / this.getMaxStorage();
    }

    public int getChargeScaled(int n) {
        return Math.min(n, n * this.Charge / 1000);
    }

    public int getStorageScaled(int n) {
        return Math.min(n, n * this.Storage / this.getMaxStorage());
    }

    @Override
    public void q_() {
        int n;
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
        this.Charge = (int)(this.cond.getVoltage() * 10.0);
        int n2 = this.getStorageForRender();
        if (this.contents[0] != null && this.Storage > 0) {
            if (this.contents[0].getItem() == RedPowerMachine.itemBatteryEmpty) {
                this.contents[0] = new ItemStack(RedPowerMachine.itemBatteryPowered, 1, RedPowerMachine.itemBatteryPowered.getMaxDurability());
                this.update();
            }
            if (this.contents[0].getItem() == RedPowerMachine.itemBatteryPowered) {
                n = Math.min(this.contents[0].getData() - 1, this.Storage);
                n = Math.min(n, 25);
                this.Storage -= n;
                this.contents[0].setData(this.contents[0].getData() - n);
                this.update();
            }
        }
        if (this.contents[1] != null && this.contents[1].getItem() == RedPowerMachine.itemBatteryPowered) {
            n = Math.min(this.contents[1].i() - this.contents[1].getData(), this.getMaxStorage() - this.Storage);
            n = Math.min(n, 25);
            this.Storage += n;
            this.contents[1].setData(this.contents[1].getData() + n);
            if (this.contents[1].getData() == this.contents[1].i()) {
                this.contents[1] = new ItemStack(RedPowerMachine.itemBatteryEmpty, 1);
            }
            this.update();
        }
        if (this.Charge > 900 && this.Storage < this.getMaxStorage()) {
            n = Math.min((this.Charge - 900) / 10, 10);
            n = Math.min(n, this.getMaxStorage() - this.Storage);
            this.cond.drawPower(n * 1000);
            this.Storage += n;
        } else if (this.Charge < 800 && this.Storage > 0 && !this.Powered) {
            n = Math.min((800 - this.Charge) / 10, 10);
            n = Math.min(n, this.Storage);
            this.cond.applyPower(n * 1000);
            this.Storage -= n;
        }
        if (n2 != this.getStorageForRender()) {
            this.updateBlock();
        }
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
        return "Battery Box";
    }

    public int getMaxStackSize() {
        return 1;
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

    @Override
    public void onBlockNeighborChange(int n) {
        this.ConMask = -1;
        if (RedPowerLib.isPowered((IBlockAccess)this.world, this.x, this.y, this.z, 16777215, 63)) {
            if (this.Powered) {
                return;
            }
        } else {
            if (!this.Powered) {
                return;
            }
            this.Powered = false;
            this.dirtyBlock();
            return;
        }
        this.Powered = true;
        this.dirtyBlock();
    }

    @Override
    public boolean onBlockActivated(EntityHuman entityHuman) {
        if (entityHuman.isSneaking()) {
            return false;
        }
        if (CoreProxy.isClient(this.world)) {
            return true;
        }
        entityHuman.openGui((BaseMod)mod_RedPowerMachine.instance, 8, this.world, this.x, this.y, this.z);
        return true;
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        for (int i = 0; i < 2; ++i) {
            ItemStack itemStack = this.contents[i];
            if (itemStack == null || itemStack.count <= 0) continue;
            CoreLib.dropItem(this.world, this.x, this.y, this.z, itemStack);
        }
    }

    @Override
    public byte[] getFramePacket() {
        Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
        packet211TileDesc.subId = 7;
        this.writeToPacket(packet211TileDesc);
        packet211TileDesc.headout.write(packet211TileDesc.subId);
        return packet211TileDesc.toByteArray();
    }

    @Override
    public void handleFramePacket(byte[] arrby) throws IOException {
        Packet211TileDesc packet211TileDesc = new Packet211TileDesc(arrby);
        packet211TileDesc.subId = packet211TileDesc.getByte();
        this.readFromPacket(packet211TileDesc);
    }

    @Override
    public void onFrameRefresh(IBlockAccess iBlockAccess) {
    }

    @Override
    public void onFramePickup(IBlockAccess iBlockAccess) {
    }

    @Override
    public void onFrameDrop() {
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        int n;
        super.a(nBTTagCompound);
        NBTTagList nBTTagList = nBTTagCompound.getList("Items");
        this.contents = new ItemStack[this.getSize()];
        for (n = 0; n < nBTTagList.size(); ++n) {
            NBTTagCompound nBTTagCompound2 = (NBTTagCompound)nBTTagList.get(n);
            int n2 = nBTTagCompound2.getByte("Slot") & 255;
            if (n2 < 0 || n2 >= this.contents.length) continue;
            this.contents[n2] = ItemStack.a((NBTTagCompound)nBTTagCompound2);
        }
        this.cond.readFromNBT(nBTTagCompound);
        this.Charge = nBTTagCompound.getShort("chg");
        this.Storage = nBTTagCompound.getShort("stor");
        n = nBTTagCompound.getByte("ps");
        this.Powered = (n & 1) > 0;
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        int n;
        super.b(nBTTagCompound);
        NBTTagList nBTTagList = new NBTTagList();
        for (n = 0; n < this.contents.length; ++n) {
            if (this.contents[n] == null) continue;
            NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
            nBTTagCompound2.setByte("Slot", (byte)n);
            this.contents[n].save(nBTTagCompound2);
            nBTTagList.add((NBTBase)nBTTagCompound2);
        }
        nBTTagCompound.set("Items", (NBTBase)nBTTagList);
        this.cond.writeToNBT(nBTTagCompound);
        nBTTagCompound.setShort("chg", (short)this.Charge);
        nBTTagCompound.setShort("stor", (short)this.Storage);
        n = this.Powered ? 1 : 0;
        nBTTagCompound.setByte("ps", (byte)n);
    }

    protected void readFromPacket(Packet211TileDesc packet211TileDesc) throws IOException {
        this.Storage = (int)packet211TileDesc.getUVLC();
    }

    protected void writeToPacket(Packet211TileDesc packet211TileDesc) {
        packet211TileDesc.addUVLC(this.Storage);
    }

    public Packet d() {
        Packet211TileDesc packet211TileDesc = new Packet211TileDesc();
        packet211TileDesc.subId = 7;
        packet211TileDesc.xCoord = this.x;
        packet211TileDesc.yCoord = this.y;
        packet211TileDesc.zCoord = this.z;
        this.writeToPacket(packet211TileDesc);
        packet211TileDesc.encode();
        return packet211TileDesc;
    }

    @Override
    public void handlePacket(Packet211TileDesc packet211TileDesc) {
        try {
            if (packet211TileDesc.subId != 7) {
                return;
            }
            this.readFromPacket(packet211TileDesc);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.updateBlock();
    }

}

