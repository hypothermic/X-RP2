/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.RedPowerLib;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;

public class TileEject
        extends TileEjectBase {
    @Override
    public int getExtendedID() {
        return 14;
    }

    @Override
    public void onBlockNeighborChange(int n) {
        if (RedPowerLib.isPowered((IBlockAccess) this.world, this.x, this.y, this.z, 16777215, 63)) {
            if (this.Powered) {
                return;
            }
            this.Powered = true;
            this.dirtyBlock();
            if (this.Active) {
                return;
            }
        } else {
            if (this.Active && !this.isTickScheduled()) {
                this.scheduleTick(5);
            }
            this.Powered = false;
            this.dirtyBlock();
            return;
        }
        this.Active = true;
        if (this.handleExtract()) {
            this.updateBlock();
        }
    }

    protected boolean handleExtract() {
        for (int i = 0; i < this.getSize(); ++i) {
            ItemStack itemStack = this.getItem(i);
            if (itemStack == null || itemStack.count == 0) continue;
            this.addToBuffer(this.splitStack(i, 1));
            this.drainBuffer();
            return true;
        }
        return false;
    }

    @Override
    public void a(NBTTagCompound nBTTagCompound) {
        super.a(nBTTagCompound);
    }

    @Override
    public void b(NBTTagCompound nBTTagCompound) {
        super.b(nBTTagCompound);
    }
}

