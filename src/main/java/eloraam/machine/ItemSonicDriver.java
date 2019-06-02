/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Entity
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.EntityLiving
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.base.ItemScrewdriver;
import eloraam.core.IChargeable;
import net.minecraft.server.*;

public class ItemSonicDriver
        extends ItemScrewdriver
        implements IChargeable {
    public ItemSonicDriver(int n) {
        super(n);
        this.setMaxDurability(400);
        this.setNoRepair();
    }

    @Override
    public boolean onItemUseFirst(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
        if (itemStack.getData() == itemStack.i()) {
            return false;
        }
        return super.onItemUseFirst(itemStack, entityHuman, world, n, n2, n3, n4);
    }

    @Override
    public int a(Entity entity) {
        return 1;
    }

    @Override
    public boolean a(ItemStack itemStack, EntityLiving entityLiving, EntityLiving entityLiving2) {
        return false;
    }
}

