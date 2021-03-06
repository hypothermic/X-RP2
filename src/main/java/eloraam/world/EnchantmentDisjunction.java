/* X-RP - decompiled with CFR */
package eloraam.world;

import net.minecraft.server.*;

public class EnchantmentDisjunction extends Enchantment {

    public EnchantmentDisjunction(int n, int n2) {
        super(n, n2, EnchantmentSlotType.WEAPON);
    }

    public int a(int n) {
        return 5 + 8 * n;
    }

    public int b(int n) {
        return this.a(n) + 20;
    }

    public int getMaxLevel() {
        return 5;
    }

    public int a(int n, EntityLiving entityLiving) {
        if (entityLiving instanceof EntityEnderman || entityLiving instanceof EntityEnderDragon) {
            return n * 6;
        }
        return 0;
    }

    public String getName() {
        return "enchantment.damage.disjunction";
    }

    public boolean canEnchantItem(ItemStack itemStack) {
        return itemStack.id == RedPowerWorld.itemAthame.id;
    }

    public boolean a(Enchantment enchantment) {
        if (enchantment == this) {
            return false;
        }
        return !(enchantment instanceof EnchantmentWeaponDamage);
    }
}
