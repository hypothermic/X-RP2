/* X-RP - decompiled with CFR */
package eloraam.world;

import eloraam.core.CoreLib;
import eloraam.core.IPaintable;
import forge.ITextureProvider;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.RedPowerWorld;
import net.minecraft.server.World;

public class ItemPaintBrush extends Item implements ITextureProvider {

    int color;

    public ItemPaintBrush(int n, int n2) {
	super(n);
	this.color = n2;
	this.d(112 + n2);
	this.e(1);
	this.setMaxDurability(15);
	this.setNoRepair();
    }

    private boolean itemUseShared(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
	IPaintable iPaintable = (IPaintable) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, IPaintable.class);
	if (iPaintable == null) {
	    return false;
	}
	MovingObjectPosition movingObjectPosition = CoreLib.retraceBlock(world, entityHuman, n, n2, n3);
	if (movingObjectPosition == null) {
	    return false;
	}
	if (!iPaintable.tryPaint(movingObjectPosition.subHit, movingObjectPosition.face, this.color + 1)) {
	    return false;
	}
	itemStack.damage(1, (EntityLiving) entityHuman);
	if (itemStack.count == 0) {
	    itemStack.count = 1;
	    itemStack.id = RedPowerWorld.itemBrushDry.id;
	}
	return true;
    }

    public boolean interactWith(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
	if (entityHuman.isSneaking()) {
	    return false;
	}
	return this.itemUseShared(itemStack, entityHuman, world, n, n2, n3, n4);
    }

    public boolean onItemUseFirst(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
	if (!entityHuman.isSneaking()) {
	    return false;
	}
	return this.itemUseShared(itemStack, entityHuman, world, n, n2, n3, n4);
    }

    public String getTextureFile() {
	return "/eloraam/base/items1.png";
    }
}
