/* X-RP - decompiled with CFR */
package eloraam.base;

import forge.ITextureProvider;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntitySheep;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

public class ItemDyeIndigo extends Item implements ITextureProvider {

    public ItemDyeIndigo(int n) {
        super(n);
        this.setMaxDurability(0);
        this.a(true);
        this.setNoRepair();
    }

    public String a(ItemStack itemStack) {
        return "item.dyeIndigo";
    }

    public int getIconFromDamage(int n) {
        return 80;
    }

    public String getTextureFile() {
        return "/eloraam/base/items1.png";
    }

    public void a(ItemStack itemStack, EntityLiving entityLiving) {
        EntitySheep entitySheep;
        if (itemStack.getData() != 0) {
            return;
        }
        if (entityLiving instanceof EntitySheep && !(entitySheep = (EntitySheep) entityLiving).isSheared() && entitySheep.getColor() != 11) {
            entitySheep.setColor(11);
            --itemStack.count;
        }
    }
}
