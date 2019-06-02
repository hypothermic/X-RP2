/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  forge.ITextureProvider
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.PlayerInventory
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.IChargeable;
import forge.ITextureProvider;
import net.minecraft.server.*;

import java.util.ArrayList;

public class ItemBattery
        extends Item
        implements ITextureProvider {
    public ItemBattery(int n) {
        super(n);
        this.d(25);
        this.e(1);
        this.setNoRepair();
        this.setMaxDurability(1500);
    }

    public ItemStack a(ItemStack itemStack, World world, EntityHuman entityHuman) {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack2 = entityHuman.inventory.getItem(i);
            if (itemStack2 == null || !(itemStack2.getItem() instanceof IChargeable) || itemStack2.getData() <= 1)
                continue;
            int n = Math.min(itemStack2.getData() - 1, itemStack.i() - itemStack2.getData());
            n = Math.min(n, 25);
            itemStack.setData(itemStack.getData() + n);
            itemStack2.setData(itemStack2.getData() - n);
            entityHuman.inventory.update();
            if (itemStack.getData() != itemStack.i()) break;
            return new ItemStack(RedPowerMachine.itemBatteryEmpty, 1);
        }
        return itemStack;
    }

    public void addCreativeItems(ArrayList arrayList) {
    }

    public String getTextureFile() {
        return "/eloraam/base/items1.png";
    }
}

