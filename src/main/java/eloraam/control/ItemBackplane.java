/* X-RP - decompiled with CFR */
package eloraam.control;

import eloraam.core.*;
import net.minecraft.server.*;

public class ItemBackplane extends ItemExtended {

    public ItemBackplane(int n) {
        super(n);
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

    protected boolean itemUseShared(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
        int n5 = world.getTypeId(n, n2, n3);
        int n6 = world.getData(n, n2, n3);
        int n7 = itemStack.getData();
        if (n5 == itemStack.id && n6 == 0 && n7 != 0) {
            TileBackplane tileBackplane = (TileBackplane) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileBackplane.class);
            if (tileBackplane == null) {
                return false;
            }
            int n8 = tileBackplane.Rotation;
            BlockMultipart.removeMultipart(world, n, n2, n3);
            if (!world.setRawTypeIdAndData(n, n2, n3, n5, n7)) {
                return false;
            }
            tileBackplane = (TileBackplane) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileBackplane.class);
            if (tileBackplane != null) {
                tileBackplane.Rotation = n8;
            }
            world.notify(n, n2, n3);
            CoreLib.placeNoise(world, n, n2, n3, itemStack.id);
            --itemStack.count;
            RedPowerLib.updateIndirectNeighbors(world, n, n2, n3, itemStack.id);
            return true;
        }
        if (n7 != 0) {
            return false;
        }
        WorldCoord worldCoord = new WorldCoord(n, n2, n3);
        worldCoord.step(n4);
        if (!world.mayPlace(itemStack.id, worldCoord.x, worldCoord.y, worldCoord.z, false, 1)) {
            return false;
        }
        if (!RedPowerLib.isSideNormal((IBlockAccess) world, worldCoord.x, worldCoord.y, worldCoord.z, 0)) {
            return false;
        }
        int n9 = -1;
        block0:
        for (int i = 0; i < 4; ++i) {
            WorldCoord worldCoord2 = worldCoord.copy();
            int n10 = CoreLib.rotToSide(i) ^ 1;
            worldCoord2.step(n10);
            TileCPU tileCPU = (TileCPU) CoreLib.getTileEntity((IBlockAccess) world, worldCoord2, TileCPU.class);
            if (tileCPU != null && tileCPU.Rotation == i) {
                n9 = i;
                break;
            }
            TileBackplane tileBackplane = (TileBackplane) CoreLib.getTileEntity((IBlockAccess) world, worldCoord2, TileBackplane.class);
            if (tileBackplane == null || tileBackplane.Rotation != i)
                continue;
            for (int j = 0; j < 6; ++j) {
                worldCoord2.step(n10);
                if (world.getTypeId(worldCoord2.x, worldCoord2.y, worldCoord2.z) != RedPowerControl.blockPeripheral.id || world.getData(worldCoord2.x, worldCoord2.y, worldCoord2.z) != 1)
                    continue;
                n9 = i;
                break block0;
            }
        }
        if (n9 < 0) {
            return false;
        }
        if (!world.setRawTypeIdAndData(worldCoord.x, worldCoord.y, worldCoord.z, itemStack.id, n7)) {
            return true;
        }
        TileBackplane tileBackplane = (TileBackplane) CoreLib.getTileEntity((IBlockAccess) world, worldCoord, TileBackplane.class);
        tileBackplane.Rotation = n9;
        CoreLib.placeNoise(world, worldCoord.x, worldCoord.y, worldCoord.z, itemStack.id);
        --itemStack.count;
        world.notify(worldCoord.x, worldCoord.y, worldCoord.z);
        RedPowerLib.updateIndirectNeighbors(world, worldCoord.x, worldCoord.y, worldCoord.z, itemStack.id);
        return true;
    }
}
