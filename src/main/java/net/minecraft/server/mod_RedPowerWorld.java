/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  forge.NetworkMod
 *  net.minecraft.server.World
 */
package net.minecraft.server;

import forge.NetworkMod;
import java.util.Random;
import net.minecraft.server.RedPowerWorld;
import net.minecraft.server.World;
import net.minecraft.server.mod_RedPowerCore;

public class mod_RedPowerWorld
extends NetworkMod {
    public static mod_RedPowerWorld instance;
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
        mod_RedPowerWorld.initialize();
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
        RedPowerWorld.initialize();
    }

    public void generateSurface(World world, Random random, int n, int n2) {
        RedPowerWorld.GenerateSurface(world, random, n, n2);
    }

    static {
        initialized = false;
    }
}

