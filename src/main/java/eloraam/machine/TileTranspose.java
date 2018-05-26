/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  forge.ISidedInventory
 *  net.minecraft.server.AxisAlignedBB
 *  net.minecraft.server.Block
 *  net.minecraft.server.BlockMinecartTrack
 *  net.minecraft.server.Entity
 *  net.minecraft.server.EntityItem
 *  net.minecraft.server.EntityMinecart
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.IInventory
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.TileEntity
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.CoreProxy;
import eloraam.core.ITubeConnectable;
import eloraam.core.MachineLib;
import eloraam.core.RedPowerLib;
import eloraam.core.TubeBuffer;
import eloraam.core.TubeItem;
import eloraam.core.WorldCoord;
import eloraam.machine.TileMachine;
import forge.ISidedInventory;
import java.util.List;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.BlockMinecartTrack;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityMinecart;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public class TileTranspose
extends TileMachine
implements ITubeConnectable {
    protected TubeBuffer buffer;

    public TileTranspose() {
        this.buffer = new TubeBuffer(this);
    }

    @Override
    public void a(NBTTagCompound arg0) {
        super.a(arg0);
        this.buffer.readFromNBT(arg0);
    }

    protected void addToBuffer(ItemStack arg0) {
        this.buffer.addNew(arg0);
    }

    @Override
    public void b(NBTTagCompound arg0) {
        super.b(arg0);
        this.buffer.writeToNBT(arg0);
    }

    @Override
    public boolean canRouteItems() {
        return false;
    }

    public boolean canSuck(int arg0, int arg1, int arg2) {
        if (this.world.isBlockSolidOnSide(arg0, arg1, arg2, this.Rotation)) {
            return false;
        }
        TileEntity arg3 = this.world.getTileEntity(arg0, arg1, arg2);
        return arg3 == null ? true : !(arg3 instanceof IInventory) && !(arg3 instanceof ITubeConnectable);
    }

    protected void doSuck() {
        this.suckEntities(this.getSizeBox(1.55, 3.05, -0.95));
    }

    public void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem arg0 = this.buffer.getLast();
            if (this.stuffCart(arg0.item)) {
                this.buffer.pop();
                continue;
            }
            if (!this.handleItem(arg0)) {
                this.buffer.plugged = true;
                return;
            }
            this.buffer.pop();
            if (!this.buffer.plugged) continue;
            return;
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.getSizeBox(0.5, 0.95, 0.0);
    }

    @Override
    public int getExtendedID() {
        return 2;
    }

    protected AxisAlignedBB getSizeBox(double arg0, double arg2, double arg4) {
        double arg6 = (double)this.x + 0.5;
        double arg8 = (double)this.y + 0.5;
        double arg10 = (double)this.z + 0.5;
        switch (this.Rotation) {
            case 0: {
                return AxisAlignedBB.b((double)(arg6 - arg0), (double)((double)this.y - arg4), (double)(arg10 - arg0), (double)(arg6 + arg0), (double)((double)this.y + arg2), (double)(arg10 + arg0));
            }
            case 1: {
                return AxisAlignedBB.b((double)(arg6 - arg0), (double)((double)(this.y + 1) - arg2), (double)(arg10 - arg0), (double)(arg6 + arg0), (double)((double)(this.y + 1) + arg4), (double)(arg10 + arg0));
            }
            case 2: {
                return AxisAlignedBB.b((double)(arg6 - arg0), (double)(arg8 - arg0), (double)((double)this.z - arg4), (double)(arg6 + arg0), (double)(arg8 + arg0), (double)((double)this.z + arg2));
            }
            case 3: {
                return AxisAlignedBB.b((double)(arg6 - arg0), (double)(arg8 - arg0), (double)((double)(this.z + 1) - arg2), (double)(arg6 + arg0), (double)(arg8 + arg0), (double)((double)(this.z + 1) + arg4));
            }
            case 4: {
                return AxisAlignedBB.b((double)((double)this.x - arg4), (double)(arg8 - arg0), (double)(arg10 - arg0), (double)((double)this.x + arg2), (double)(arg8 + arg0), (double)(arg10 + arg0));
            }
        }
        return AxisAlignedBB.b((double)((double)(this.x + 1) - arg2), (double)(arg8 - arg0), (double)(arg10 - arg0), (double)((double)(this.x + 1) + arg4), (double)(arg8 + arg0), (double)(arg10 + arg0));
    }

    @Override
    public int getTubeConClass() {
        return 0;
    }

    @Override
    public int getTubeConnectableSides() {
        return 3 << (this.Rotation & -2);
    }

    protected boolean handleExtract(IInventory arg0, int arg1, int arg2) {
        int arg3 = arg1;
        while (arg3 < arg1 + arg2) {
            ItemStack arg4 = arg0.getItem(arg3);
            if (arg4 != null && arg4.count != 0) {
                this.addToBuffer(arg0.splitStack(arg3, 1));
                this.drainBuffer();
                return true;
            }
            ++arg3;
        }
        return false;
    }

    protected boolean handleExtract(WorldCoord arg0) {
        IInventory arg1 = MachineLib.getInventory(this.world, arg0);
        if (arg1 == null) {
            return false;
        }
        int arg2 = 0;
        int arg3 = arg1.getSize();
        if (arg1 instanceof ISidedInventory) {
            ISidedInventory arg4 = (ISidedInventory)arg1;
            arg2 = arg4.getStartInventorySide(this.Rotation);
            arg3 = arg4.getSizeInventorySide(this.Rotation);
        }
        return this.handleExtract(arg1, arg2, arg3);
    }

    @Override
    public void onBlockNeighborChange(int arg0) {
        if (RedPowerLib.isPowered((IBlockAccess)this.world, this.x, this.y, this.z, 16777215, 63)) {
            if (!this.Powered) {
                this.Powered = true;
                this.dirtyBlock();
                if (!this.Active) {
                    this.Active = true;
                    WorldCoord arg1 = new WorldCoord(this.x, this.y, this.z);
                    arg1.step(this.Rotation ^ 1);
                    if (this.canSuck(arg1.x, arg1.y, arg1.z)) {
                        this.doSuck();
                        this.updateBlock();
                    } else if (this.handleExtract(arg1)) {
                        this.updateBlock();
                    }
                }
            }
        } else {
            if (this.Active && !this.isTickScheduled()) {
                this.scheduleTick(5);
            }
            this.Powered = false;
            this.dirtyBlock();
        }
    }

    @Override
    public void onBlockRemoval() {
        this.buffer.onRemove(this);
    }

    @Override
    public void onEntityCollidedWithBlock(Entity arg0) {
        if (!CoreProxy.isClient(this.world) && !this.Powered && this.buffer.isEmpty()) {
            this.suckEntities(this.getSizeBox(0.55, 1.05, -0.95));
        }
    }

    @Override
    public void onTileTick() {
        if (!CoreProxy.isClient(this.world)) {
            if (!this.buffer.isEmpty()) {
                this.drainBuffer();
                if (!this.buffer.isEmpty()) {
                    this.scheduleTick(10);
                } else {
                    this.scheduleTick(5);
                }
            } else if (!this.Powered) {
                this.Active = false;
                this.updateBlock();
            }
        }
    }

    public boolean stuffCart(ItemStack arg0) {
        WorldCoord arg1 = new WorldCoord(this);
        arg1.step(this.Rotation);
        Block arg2 = Block.byId[this.world.getTypeId(arg1.x, arg1.y, arg1.z)];
        if (!(arg2 instanceof BlockMinecartTrack)) {
            return false;
        }
        List arg3 = this.world.a(EntityMinecart.class, this.getSizeBox(0.8, 0.05, 1.05));
        for (Object arg5 : arg3) {
            EntityMinecart arg6;
            if (!(arg5 instanceof EntityMinecart) || !MachineLib.addToInventoryCore((IInventory)(arg6 = (EntityMinecart)arg5), arg0, 0, arg6.getSize(), true)) continue;
            return true;
        }
        return false;
    }

    protected void suckEntities(AxisAlignedBB arg0) {
        boolean arg1 = false;
        List arg2 = this.world.a(Entity.class, arg0);
        for (Object arg5 : arg2) {
            int arg4 = this.suckEntity(arg5);
            if (arg4 == 0) continue;
            arg1 = true;
            if (arg4 == 2) break;
        }
        if (arg1) {
            if (!this.Active) {
                this.Active = true;
                this.updateBlock();
            }
            this.drainBuffer();
            this.scheduleTick(5);
        }
    }

    protected int suckEntity(Object arg0) {
        if (arg0 instanceof EntityItem) {
            EntityItem arg2 = (EntityItem)arg0;
            if (arg2.itemStack.count != 0 && !arg2.dead) {
                if (!this.suckFilter(arg2.itemStack)) {
                    return 0;
                }
                this.addToBuffer(arg2.itemStack);
                arg2.die();
                return 1;
            }
            return 0;
        }
        if (arg0 instanceof EntityMinecart) {
            if (this.Active) {
                return 0;
            }
            EntityMinecart arg1 = (EntityMinecart)arg0;
            if (this.handleExtract((IInventory)arg1, 0, arg1.getSize())) {
                return 2;
            }
        }
        return 0;
    }

    protected boolean suckFilter(ItemStack arg0) {
        return true;
    }

    @Override
    public boolean tubeItemCanEnter(int arg0, int arg1, TubeItem arg2) {
        return arg0 == this.Rotation && arg1 == 2 ? true : (arg0 == (this.Rotation ^ 1) && arg1 == 1 ? this.buffer.isEmpty() && !this.Powered : false);
    }

    @Override
    public boolean tubeItemEnter(int arg0, int arg1, TubeItem arg2) {
        if (arg0 == this.Rotation && arg1 == 2) {
            this.buffer.addBounce(arg2);
            this.Active = true;
            this.updateBlock();
            this.scheduleTick(5);
            return true;
        }
        if (arg0 == (this.Rotation ^ 1) && arg1 == 1) {
            if (this.Powered) {
                return false;
            }
            if (!this.buffer.isEmpty()) {
                return false;
            }
            this.addToBuffer(arg2.item);
            this.Active = true;
            this.updateBlock();
            this.scheduleTick(5);
            this.drainBuffer();
            return true;
        }
        return false;
    }

    @Override
    public int tubeWeight(int arg0, int arg1) {
        return arg0 == this.Rotation && arg1 == 2 ? this.buffer.size() : 0;
    }
}

