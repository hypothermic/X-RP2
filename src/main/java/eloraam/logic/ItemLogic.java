/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Block
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.StepSound
 *  net.minecraft.server.World
 *  org.bukkit.block.BlockState
 *  org.bukkit.craftbukkit.block.CraftBlockState
 *  org.bukkit.craftbukkit.event.CraftEventFactory
 *  org.bukkit.event.block.BlockPlaceEvent
 */
package eloraam.logic;

import eloraam.core.CoreLib;
import eloraam.core.ItemExtended;
import eloraam.core.RedPowerLib;
import net.minecraft.server.*;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockPlaceEvent;

public class ItemLogic
        extends ItemExtended {
    public ItemLogic(int n) {
        super(n);
        this.setMaxDurability(0);
        this.a(true);
    }

    @Override
    public int filterData(int n) {
        return n;
    }

    public void placeNoise(World world, int n, int n2, int n3, int n4) {
        Block block = Block.byId[n4];
        world.makeSound((double) ((float) n + 0.5f), (double) ((float) n2 + 0.5f), (double) ((float) n3 + 0.5f), "step.stone", (block.stepSound.getVolume1() + 1.0f) / 2.0f, block.stepSound.getVolume2() * 0.8f);
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

    protected boolean tryPlace(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4, int n5, int n6) {
        int n7 = itemStack.id;
        int n8 = itemStack.getData();
        if (!world.setRawTypeIdAndData(n, n2, n3, n7, n8 >> 8)) {
            return false;
        }
        TileLogic tileLogic = (TileLogic) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileLogic.class);
        if (tileLogic == null) {
            return false;
        }
        tileLogic.Rotation = n5 << 2 | n6;
        tileLogic.initSubType(n8 & 255);
        return true;
    }

    protected boolean itemUseShared(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
        int n5;
        int n6 = n--;
        int n7 = n2--;
        int n8 = n3--;
        switch (n4) {
            case 0: {
                break;
            }
            case 1: {
                ++n2;
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                ++n3;
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                ++n;
            }
        }
        int n9 = itemStack.id;
        if (!world.mayPlace(n9, n, n2, n3, false, n4)) {
            return false;
        }
        if (!RedPowerLib.isSideNormal((IBlockAccess) world, n, n2, n3, n4 ^ 1)) {
            return false;
        }
        int n10 = (int) Math.floor(entityHuman.yaw / 90.0f + 0.5f);
        int n11 = (int) Math.floor(entityHuman.pitch / 90.0f + 0.5f);
        n10 = n10 + 1 & 3;
        int n12 = n4 ^ 1;
        switch (n12) {
            case 0: {
                n5 = n10;
                break;
            }
            case 1: {
                n5 = n10 ^ (n10 & 1) << 1;
                break;
            }
            case 2: {
                n5 = (n10 & 1) <= 0 ? 1 - n10 & 3 : (n11 <= 0 ? 0 : 2);
                break;
            }
            case 3: {
                n5 = (n10 & 1) <= 0 ? n10 - 1 & 3 : (n11 <= 0 ? 0 : 2);
                break;
            }
            case 4: {
                n5 = (n10 & 1) != 0 ? n10 - 2 & 3 : (n11 <= 0 ? 0 : 2);
                break;
            }
            case 5: {
                n5 = (n10 & 1) != 0 ? 2 - n10 & 3 : (n11 <= 0 ? 0 : 2);
                break;
            }
            default: {
                n5 = 0;
            }
        }
        CraftBlockState craftBlockState = CraftBlockState.getBlockState((World) world, (int) n, (int) n2, (int) n3);
        world.suppressPhysics = true;
        world.setTypeIdAndData(n, n2, n3, n9, this.filterData(itemStack.getData()));
        BlockPlaceEvent blockPlaceEvent = CraftEventFactory.callBlockPlaceEvent((World) world, (EntityHuman) entityHuman, (BlockState) craftBlockState, (int) n6, (int) n7, (int) n8);
        craftBlockState.update(true);
        world.suppressPhysics = false;
        if (blockPlaceEvent.isCancelled() || !blockPlaceEvent.canBuild()) {
            return true;
        }
        if (!this.tryPlace(itemStack, entityHuman, world, n, n2, n3, n4, n12, n5)) {
            return true;
        }
        this.placeNoise(world, n, n2, n3, n9);
        --itemStack.count;
        world.notify(n, n2, n3);
        RedPowerLib.updateIndirectNeighbors(world, n, n2, n3, n9);
        return true;
    }
}

