/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.BaseMod
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.NBTTagCompound
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.CoreProxy;
import eloraam.machine.TileEjectBase;
import net.minecraft.server.BaseMod;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.World;
import net.minecraft.server.mod_RedPowerMachine;

public class TileRelay
extends TileEjectBase {
    @Override
    public int getExtendedID() {
        return 15;
    }

    @Override
    public void onTileTick() {
        super.onTileTick();
        if (CoreProxy.isClient(this.world)) {
            return;
        }
        if (this.Active) {
            return;
        }
        if (this.handleExtract()) {
            this.Active = true;
            this.updateBlock();
            this.scheduleTick(5);
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
    public boolean onBlockActivated(EntityHuman entityHuman) {
        if (entityHuman.isSneaking()) {
            return false;
        }
        if (CoreProxy.isClient(this.world)) {
            return true;
        }
        entityHuman.openGui((BaseMod)mod_RedPowerMachine.instance, 13, this.world, this.x, this.y, this.z);
        return true;
    }

    protected boolean handleExtract() {
        for (int i = 0; i < this.getSize(); ++i) {
            ItemStack itemStack = this.getItem(i);
            if (itemStack == null || itemStack.count == 0) continue;
            this.addToBuffer(this.contents[i]);
            this.setItem(i, null);
            this.drainBuffer();
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "Relay";
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

