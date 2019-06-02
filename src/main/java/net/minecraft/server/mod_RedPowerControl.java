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
 *  net.minecraft.server.PlayerInventory
 *  net.minecraft.server.World
 */
package net.minecraft.server;

import eloraam.control.*;
import eloraam.core.CoreLib;
import eloraam.core.IRedbusConnectable;
import forge.IGuiHandler;
import forge.MinecraftForge;
import forge.NetworkMod;

public class mod_RedPowerControl
        extends NetworkMod {
    public static mod_RedPowerControl instance;
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
        mod_RedPowerControl.initialize();
        mod_RedPowerControl.init_guihandler();
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
        RedPowerControl.initialize();
    }

    public static void init_guihandler() {
        MinecraftForge.setGuiHandler((BaseMod) instance, (IGuiHandler) new IGuiHandler() {

            public Container getGuiElement(int n, EntityHuman entityHuman, World world, int n2, int n3, int n4) {
                switch (n) {
                    case 1: {
                        return new ContainerDisplay((IInventory) entityHuman.inventory, (TileDisplay) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileDisplay.class));
                    }
                    case 2: {
                        return new ContainerBusId((IInventory) entityHuman.inventory, (IRedbusConnectable) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, IRedbusConnectable.class));
                    }
                    case 3: {
                        return new ContainerCPU((IInventory) entityHuman.inventory, (TileCPU) CoreLib.getTileEntity((IBlockAccess) world, n2, n3, n4, TileCPU.class));
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

