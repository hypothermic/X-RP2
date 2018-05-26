/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  forge.ITextureProvider
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.BluePowerConductor;
import eloraam.core.CoreLib;
import eloraam.core.CoreProxy;
import eloraam.core.IBluePowerConnectable;
import forge.ITextureProvider;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class ItemVoltmeter
extends Item
implements ITextureProvider {
    public ItemVoltmeter(int n) {
        super(n);
        this.d(24);
        this.e(1);
    }

    private boolean itemUseShared(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
        IBluePowerConnectable iBluePowerConnectable = (IBluePowerConnectable)CoreLib.getTileEntity((IBlockAccess)world, n, n2, n3, IBluePowerConnectable.class);
        if (iBluePowerConnectable == null) {
            return false;
        }
        BluePowerConductor bluePowerConductor = iBluePowerConnectable.getBlueConductor();
        CoreProxy.writeChat(entityHuman, String.format("Reading %.2fV %.2fA", bluePowerConductor.getVoltage(), bluePowerConductor.Itot));
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

