/* X-RP - decompiled with CFR */
package eloraam.world;

import forge.ITextureProvider;
import net.minecraft.server.*;

public class ItemSickle extends Item implements ITextureProvider {

    public int radius = 2;

    public ItemSickle(int n) {
        super(n);
        this.setMaxDurability(200);
        this.maxStackSize = 1;
    }

    public boolean a(ItemStack itemStack, int n, int n2, int n3, int n4, EntityLiving entityLiving) {
        boolean bl = false;
        if (!(entityLiving instanceof EntityHuman)) {
            return false;
        }
        World world = entityLiving.world;
        EntityHuman entityHuman = (EntityHuman) entityLiving;
        for (int i = -this.radius; i <= this.radius; ++i) {
            for (int j = -this.radius; j <= this.radius; ++j) {
                boolean bl2;
                Block block;
                int n5 = world.getTypeId(n2 + i, n3, n4 + j);
                int n6 = world.getData(n2 + i, n3, n4 + j);
                if (n5 == 0 || !((block = Block.byId[n5]) instanceof BlockFlower) || !(bl2 = world.setTypeId(n2 + i, n3, n4 + j, 0)))
                    continue;
                if (block.canHarvestBlock(entityHuman, n6)) {
                    block.a(world, entityHuman, n2 + i, n3, n4 + j, n6);
                }
                bl = true;
            }
        }
        if (bl) {
            itemStack.damage(1, entityLiving);
        }
        return bl;
    }

    public String getTextureFile() {
        return "/eloraam/world/worlditems1.png";
    }
}
