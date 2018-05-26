/* X-RP - decompiled with CFR */
package eloraam.base;

import eloraam.core.CoreLib;
import eloraam.core.IRotatable;
import forge.ITextureProvider;
import net.minecraft.server.Block;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.World;

public class ItemScrewdriver extends Item implements ITextureProvider {

    public ItemScrewdriver(int n) {
	super(n);
	this.setMaxDurability(200);
	this.e(1);
    }

    public boolean a(ItemStack itemStack, EntityLiving entityLiving, EntityLiving entityLiving2) {
	itemStack.damage(8, entityLiving2);
	return true;
    }

    public boolean onItemUseFirst(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
	boolean bl = false;
	if (entityHuman != null && entityHuman.isSneaking()) {
	    bl = true;
	}
	int n5 = world.getTypeId(n, n2, n3);
	int n6 = world.getData(n, n2, n3);
	if (n5 == Block.DIODE_OFF.id || n5 == Block.DIODE_ON.id) {
	    world.setData(n, n2, n3, n6 & 12 | n6 + 1 & 3);
	    itemStack.damage(1, (EntityLiving) entityHuman);
	    return true;
	}
	if (n5 == Block.DISPENSER.id) {
	    n6 = n6 & 3 ^ n6 >> 2;
	    world.setData(n, n2, n3, n6 += 2);
	    itemStack.damage(1, (EntityLiving) entityHuman);
	    return true;
	}
	if (n5 == Block.PISTON.id || n5 == Block.PISTON_STICKY.id) {
	    if (++n6 > 5) {
		n6 = 0;
	    }
	    world.setData(n, n2, n3, n6);
	    itemStack.damage(1, (EntityLiving) entityHuman);
	    return true;
	}
	IRotatable iRotatable = (IRotatable) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, IRotatable.class);
	if (iRotatable == null) {
	    return false;
	}
	MovingObjectPosition movingObjectPosition = CoreLib.retraceBlock(world, entityHuman, n, n2, n3);
	if (movingObjectPosition == null) {
	    return false;
	}
	int n7 = iRotatable.getPartMaxRotation(movingObjectPosition.subHit, bl);
	if (n7 == 0) {
	    return false;
	}
	int n8 = iRotatable.getPartRotation(movingObjectPosition.subHit, bl);
	if (++n8 > n7) {
	    n8 = 0;
	}
	iRotatable.setPartRotation(movingObjectPosition.subHit, bl, n8);
	itemStack.damage(1, (EntityLiving) entityHuman);
	return true;
    }

    public int a(Entity entity) {
	return 6;
    }

    public String getTextureFile() {
	return "/eloraam/base/items1.png";
    }
}
