/* X-RP - decompiled with CFR */
package eloraam.world;

import forge.ITextureProvider;
import net.minecraft.server.Block;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityEnderDragon;
import net.minecraft.server.EntityEnderman;
import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.ItemSword;

public class ItemAthame extends ItemSword implements ITextureProvider {

    public ItemAthame(int n) {
	super(n, EnumToolMaterial.DIAMOND);
	this.setMaxDurability(100);
	this.a(0, 7);
    }

    public float getDestroySpeed(ItemStack itemStack, Block block) {
	return 1.0f;
    }

    public int a(Entity entity) {
	return !(entity instanceof EntityEnderman) && !(entity instanceof EntityEnderDragon) ? 1 : 25;
    }

    public String getTextureFile() {
	return "/eloraam/world/worlditems1.png";
    }

    public int c() {
	return 30;
    }
}
