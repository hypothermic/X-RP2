/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Block
 *  net.minecraft.server.BlockDeadBush
 *  net.minecraft.server.BlockLongGrass
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.EntityLiving
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.StepSound
 *  net.minecraft.server.World
 *  org.bukkit.block.BlockState
 *  org.bukkit.craftbukkit.block.CraftBlockState
 *  org.bukkit.craftbukkit.event.CraftEventFactory
 *  org.bukkit.event.block.BlockPlaceEvent
 */
package eloraam.machine;

import eloraam.core.ItemExtended;
import net.minecraft.server.*;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockPlaceEvent;

public class ItemMachinePanel
        extends ItemExtended {
    public ItemMachinePanel(int n) {
        super(n);
    }

    public boolean interactWith(ItemStack itemStack, EntityHuman entityHuman, World world, int n, int n2, int n3, int n4) {
        int n5 = n;
        int n6 = n2;
        int n7 = n3;
        int n8 = world.getTypeId(n, n2, n3);
        int n9 = this.a();
        if (n8 == Block.SNOW.id) {
            n4 = 1;
        } else if (n8 != Block.VINE.id && n8 != Block.LONG_GRASS.id && n8 != Block.DEAD_BUSH.id) {
            switch (n4) {
                case 0: {
                    --n2;
                    break;
                }
                case 1: {
                    ++n2;
                    break;
                }
                case 2: {
                    --n3;
                    break;
                }
                case 3: {
                    ++n3;
                    break;
                }
                case 4: {
                    --n;
                    break;
                }
                default: {
                    ++n;
                }
            }
        }
        if (itemStack.count == 0) {
            return false;
        }
        if (!entityHuman.d(n, n2, n3)) {
            return false;
        }
        if (n2 >= world.getHeight() - 1) {
            return false;
        }
        if (!world.mayPlace(n9, n, n2, n3, false, n4)) {
            return false;
        }
        if (itemStack.getData() == 0 && !world.isBlockSolidOnSide(n, n2 - 1, n3, 1)) {
            return false;
        }
        Block block = Block.byId[n9];
        CraftBlockState craftBlockState = CraftBlockState.getBlockState((World) world, (int) n, (int) n2, (int) n3);
        world.suppressPhysics = true;
        world.setTypeIdAndData(n, n2, n3, n9, this.filterData(itemStack.getData()));
        BlockPlaceEvent blockPlaceEvent = CraftEventFactory.callBlockPlaceEvent((World) world, (EntityHuman) entityHuman, (BlockState) craftBlockState, (int) n5, (int) n6, (int) n7);
        craftBlockState.update(true);
        world.suppressPhysics = false;
        if (blockPlaceEvent.isCancelled() || !blockPlaceEvent.canBuild()) {
            return true;
        }
        if (world.setTypeIdAndData(n, n2, n3, n9, this.filterData(itemStack.getData()))) {
            if (world.getTypeId(n, n2, n3) == n9) {
                Block.byId[n9].postPlace(world, n, n2, n3, n4);
                Block.byId[n9].postPlace(world, n, n2, n3, (EntityLiving) entityHuman);
            }
            world.makeSound((double) ((float) n + 0.5f), (double) ((float) n2 + 0.5f), (double) ((float) n3 + 0.5f), block.stepSound.getName(), (block.stepSound.getVolume1() + 1.0f) / 2.0f, block.stepSound.getVolume2() * 0.8f);
            --itemStack.count;
        }
        return true;
    }
}

