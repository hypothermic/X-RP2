/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Entity
 *  net.minecraft.server.EntityPlayer
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemInWorldManager
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.ModLoader
 *  net.minecraft.server.PlayerInventory
 *  net.minecraft.server.Statistic
 *  net.minecraft.server.World
 */
package eloraam.machine;

import net.minecraft.server.*;

public class EntityPlayerFake
        extends EntityPlayer {
    public EntityPlayerFake(String string, World world) {
        super(ModLoader.getMinecraftServerInstance(), world, string, new ItemInWorldManager(world));
        for (int i = 9; i < 36; ++i) {
            this.inventory.setItem(i, new ItemStack(Item.STICK));
        }
    }

    public void func_6420_o() {
    }

    public void a(Statistic statistic, int n) {
    }

    public void mount(Entity entity) {
    }
}

