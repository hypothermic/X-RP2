/* X-RP - decompiled with CFR */
package eloraam.world;

import forge.ITextureProvider;
import net.minecraft.server.*;

public class ItemPaintCan extends Item implements ITextureProvider {

    int color;

    public ItemPaintCan(int n, int n2) {
        super(n);
        this.color = n2;
        this.d(96 + n2);
        this.e(1);
        this.setMaxDurability(15);
        this.setNoRepair();
    }

    public ItemStack a(ItemStack itemStack, World world, EntityHuman entityHuman) {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack2 = entityHuman.inventory.getItem(i);
            if (itemStack2 == null || itemStack2.id != RedPowerWorld.itemBrushDry.id || itemStack2.count != 1)
                continue;
            entityHuman.inventory.setItem(i, new ItemStack(RedPowerWorld.itemBrushPaint[this.color]));
            itemStack.damage(1, (EntityLiving) entityHuman);
            if (itemStack.count == 0) {
                return new ItemStack(RedPowerWorld.itemPaintCanEmpty);
            }
            return itemStack;
        }
        return itemStack;
    }

    public String getTextureFile() {
        return "/eloraam/base/items1.png";
    }
}
