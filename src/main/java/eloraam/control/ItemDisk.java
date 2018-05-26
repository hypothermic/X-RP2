/* X-RP - decompiled with CFR */
package eloraam.control;

import eloraam.control.TileDiskDrive;
import eloraam.core.CoreLib;
import forge.ITextureProvider;
import java.util.ArrayList;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class ItemDisk extends Item implements ITextureProvider {

    public ItemDisk(int n) {
	super(n);
	this.setMaxDurability(0);
	this.a(true);
	this.e(1);
	this.setNoRepair();
    }

    public String a(ItemStack itemStack) {
	switch (itemStack.getData()) {
	case 0: {
	    return "item.disk";
	}
	case 1: {
	    return "item.disk.forth";
	}
	}
	return null;
    }

    public boolean onItemUseFirst(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
	TileDiskDrive tileDiskDrive = (TileDiskDrive) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileDiskDrive.class);
	if (tileDiskDrive == null) {
	    return false;
	}
	if (tileDiskDrive.setDisk(itemStack.cloneItemStack())) {
	    itemStack.count = 0;
	    return true;
	}
	return false;
    }

    public boolean i() {
	return true;
    }

    public int getIconFromDamage(int n) {
	return 3 + n;
    }

    public String getTextureFile() {
	return "/eloraam/control/control1.png";
    }

    public void addCreativeItems(ArrayList arrayList) {
	for (int i = 0; i <= 1; ++i) {
	    arrayList.add(new ItemStack((Item) this, 1, i));
	}
    }
}
