/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  forge.IGuiHandler
 *  forge.MinecraftForge
 *  forge.NetworkMod
 *  net.minecraft.server.BaseMod
 *  net.minecraft.server.Container
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.IInventory
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.PlayerInventory
 *  net.minecraft.server.World
 */
package net.minecraft.server;

import eloraam.base.ContainerAdvBench;
import eloraam.base.ContainerAlloyFurnace;
import eloraam.base.TileAdvBench;
import eloraam.base.TileAlloyFurnace;
import eloraam.core.AchieveLib;
import eloraam.core.CoreLib;
import forge.IGuiHandler;
import forge.MinecraftForge;
import forge.NetworkMod;
import net.minecraft.server.BaseMod;
import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.RedPowerBase;
import net.minecraft.server.RedPowerCore;
import net.minecraft.server.World;

public class mod_RedPowerCore
extends NetworkMod {
    public static mod_RedPowerCore instance;
    private static boolean initialized;

    public boolean clientSideRequired() {
        return true;
    }

    public boolean serverSideRequired() {
        return true;
    }

    public void modsLoaded() {
        super.modsLoaded();
        instance = this;
        mod_RedPowerCore.initialize();
        RedPowerCore.customBlockModel = 0;
        mod_RedPowerCore.init_guihandler();
    }

    public void load() {
    }

    public String getVersion() {
        return "2.0pr5b2";
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        RedPowerCore.initialize();
        RedPowerBase.initialize();
    }

    public static void init_guihandler() {
        MinecraftForge.setGuiHandler((BaseMod)instance, (IGuiHandler)new IGuiHandler(){

            public Container getGuiElement(int n, EntityHuman entityHuman, World world, int n2, int n3, int n4) {
                switch (n) {
                    case 1: {
                        return new ContainerAlloyFurnace(entityHuman.inventory, (TileAlloyFurnace)CoreLib.getTileEntity((IBlockAccess)world, n2, n3, n4, TileAlloyFurnace.class));
                    }
                    case 2: {
                        return new ContainerAdvBench(entityHuman.inventory, (TileAdvBench)CoreLib.getTileEntity((IBlockAccess)world, n2, n3, n4, TileAdvBench.class));
                    }
                }
                return null;
            }
        });
    }

    public void takenFromCrafting(EntityHuman entityHuman, ItemStack itemStack, IInventory iInventory) {
        AchieveLib.onCrafting(entityHuman, itemStack);
    }

    public void takenFromFurnace(EntityHuman entityHuman, ItemStack itemStack) {
        AchieveLib.onFurnace(entityHuman, itemStack);
    }

    static {
        initialized = false;
    }

}

