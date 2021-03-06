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
import eloraam.machine.*;
import forge.IGuiHandler;
import forge.MinecraftForge;
import forge.NetworkMod;

public class mod_RedPowerMachine
        extends NetworkMod {
    public static mod_RedPowerMachine instance;
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
        mod_RedPowerMachine.initialize();
        mod_RedPowerMachine.init_guihandler();
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
        mod_RedPowerWiring.initialize();
        mod_RedPowerWorld.initialize();
        RedPowerMachine.initialize();
        ModLoader.registerTileEntity(TilePipe.class, (String) "RPPipe");
        ModLoader.registerTileEntity(TilePump.class, (String) "RPPump");
        ModLoader.registerTileEntity(TileTube.class, (String) "RPTube");
        ModLoader.registerTileEntity(TileRedstoneTube.class, (String) "RPRSTube");
        ModLoader.registerTileEntity(TileRestrictTube.class, (String) "RPRTube");
        ModLoader.registerTileEntity(TileMagTube.class, (String) "RPMTube");
        ModLoader.registerTileEntity(TileFrameMoving.class, (String) "RPMFrame");
        ModLoader.registerTileEntity(TileAccel.class, (String) "RPAccel");
    }

    public static void init_guihandler() {
        MinecraftForge.setGuiHandler((BaseMod) instance, (IGuiHandler) new IGuiHandler() {

            public Container getGuiElement(int n, EntityHuman entityHuman, World world, int n2, int n3, int n4) {
                switch (n) {
                    case 1: {
                        return new ContainerDeploy((IInventory) entityHuman.inventory, (TileDeploy) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileDeploy.class));
                    }
                    case 2: {
                        return new ContainerFilter((IInventory) entityHuman.inventory, (TileFilter) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileFilter.class));
                    }
                    case 3: {
                        return new ContainerBlueFurnace(entityHuman.inventory, (TileBlueFurnace) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileBlueFurnace.class));
                    }
                    case 4: {
                        return new ContainerBufferChest((IInventory) entityHuman.inventory, (TileBufferChest) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileBufferChest.class));
                    }
                    case 5: {
                        return new ContainerSorter((IInventory) entityHuman.inventory, (TileSorter) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileSorter.class));
                    }
                    case 6: {
                        return new ContainerItemDetect((IInventory) entityHuman.inventory, (TileItemDetect) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileItemDetect.class));
                    }
                    case 7: {
                        return new ContainerRetriever((IInventory) entityHuman.inventory, (TileRetriever) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileRetriever.class));
                    }
                    case 8: {
                        return new ContainerBatteryBox((IInventory) entityHuman.inventory, (TileBatteryBox) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileBatteryBox.class));
                    }
                    case 9: {
                        return new ContainerRegulator((IInventory) entityHuman.inventory, (TileRegulator) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileRegulator.class));
                    }
                    case 10: {
                        return new ContainerBlueAlloyFurnace(entityHuman.inventory, (TileBlueAlloyFurnace) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileBlueAlloyFurnace.class));
                    }
                    case 11: {
                        return new ContainerAssemble((IInventory) entityHuman.inventory, (TileAssemble) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileAssemble.class));
                    }
                    case 12: {
                        return new ContainerEject((IInventory) entityHuman.inventory, (TileEjectBase) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileEjectBase.class));
                    }
                    case 13: {
                        return new ContainerEject((IInventory) entityHuman.inventory, (TileEjectBase) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileRelay.class));
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

