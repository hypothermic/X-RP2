/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Block
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.*;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

import java.util.ArrayList;

public class MicroPlacementTube
        implements IMicroPlacement {
    private void blockUsed(World world, WorldCoord worldCoord, ItemStack itemStack) {
        --itemStack.count;
        CoreLib.placeNoise(world, worldCoord.x, worldCoord.y, worldCoord.z, itemStack.id);
        world.notify(worldCoord.x, worldCoord.y, worldCoord.z);
        RedPowerLib.updateIndirectNeighbors(world, worldCoord.x, worldCoord.y, worldCoord.z, itemStack.id);
    }

    private boolean initialPlace(ItemStack itemStack, EntityHuman entityHuman, World world, WorldCoord worldCoord, int n) {
        int n2 = itemStack.getData() >> 8;
        int n3 = itemStack.id;
        if (!world.mayPlace(n3, worldCoord.x, worldCoord.y, worldCoord.z, false, n)) {
            return false;
        }
        if (!world.setRawTypeIdAndData(worldCoord.x, worldCoord.y, worldCoord.z, n3, n2)) {
            return true;
        }
        this.blockUsed(world, worldCoord, itemStack);
        return true;
    }

    @Override
    public boolean onPlaceMicro(ItemStack itemStack, EntityHuman entityHuman, World world, WorldCoord worldCoord, int n) {
        worldCoord.step(n);
        int n2 = world.getTypeId(worldCoord.x, worldCoord.y, worldCoord.z);
        if (n2 != itemStack.id) {
            return this.initialPlace(itemStack, entityHuman, world, worldCoord, n);
        }
        TileCovered tileCovered = (TileCovered) CoreLib.getTileEntity((IBlockAccess) world, worldCoord, TileCovered.class);
        if (tileCovered == null) {
            return false;
        }
        int n3 = tileCovered.getExtendedID();
        if (n3 == 7 || n3 == 8 || n3 == 9 || n3 == 10 || n3 == 11) {
            return false;
        }
        if (!CoverLib.tryMakeCompatible(world, worldCoord, itemStack.id, itemStack.getData())) {
            return false;
        }
        this.blockUsed(world, worldCoord, itemStack);
        return true;
    }

    @Override
    public String getMicroName(int n, int n2) {
        if (n == 7) {
            return "tile.rppipe";
        }
        if (n == 8) {
            return "tile.rptube";
        }
        if (n == 9) {
            return "tile.rprstube";
        }
        if (n == 10) {
            return "tile.rprtube";
        }
        if (n == 11) {
            return "tile.rpmtube";
        }
        return null;
    }

    @Override
    public void addCreativeItems(int n, ArrayList arrayList) {
        if (n == 7) {
            arrayList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 1792));
        } else if (n == 8) {
            arrayList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 2048));
        } else if (n == 9) {
            arrayList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 2304));
        } else if (n == 10) {
            arrayList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 2560));
        } else if (n == 11) {
            arrayList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 2816));
        }
    }
}

