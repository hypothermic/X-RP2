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
 *  net.minecraft.server.ModLoader
 *  net.minecraft.server.PlayerInventory
 *  net.minecraft.server.World
 */
package net.minecraft.server;

import eloraam.core.CoreLib;
import eloraam.logic.ContainerCounter;
import eloraam.logic.TileLogicPointer;
import eloraam.logic.TileLogicStorage;
import eloraam.logic.TileSequencer;
import eloraam.logic.TileTimer;
import forge.IGuiHandler;
import forge.MinecraftForge;
import forge.NetworkMod;
import net.minecraft.server.BaseMod;
import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.IInventory;
import net.minecraft.server.ModLoader;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.RedPowerLogic;
import net.minecraft.server.World;
import net.minecraft.server.mod_RedPowerCore;

public class mod_RedPowerLogic
extends NetworkMod {
    public static mod_RedPowerLogic instance;
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
        mod_RedPowerLogic.initialize();
        mod_RedPowerLogic.init_guihandler();
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
        mod_RedPowerCore.initialize();
        RedPowerLogic.initialize();
        ModLoader.registerTileEntity(TileLogicPointer.class, (String)"RPLgPtr");
        ModLoader.registerTileEntity(TileSequencer.class, (String)"Sequencer");
        ModLoader.registerTileEntity(TileTimer.class, (String)"Timer");
    }

    public static void init_guihandler() {
        MinecraftForge.setGuiHandler((BaseMod)instance, (IGuiHandler)new IGuiHandler(){

            public Container getGuiElement(int n, EntityHuman entityHuman, World world, int n2, int n3, int n4) {
                switch (n) {
                    case 1: {
                        return new ContainerCounter((IInventory)entityHuman.inventory, (TileLogicStorage)CoreLib.getTileEntity((IBlockAccess)world, n2, n3, n4, TileLogicStorage.class));
                    }
                }
                return null;
            }
        });
    }

    static {
        initialized = false;
    }

}

