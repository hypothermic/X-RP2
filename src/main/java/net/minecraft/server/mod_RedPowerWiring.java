/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  forge.NetworkMod
 */
package net.minecraft.server;

import forge.NetworkMod;

public class mod_RedPowerWiring
        extends NetworkMod {
    public static mod_RedPowerWiring instance;
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
        mod_RedPowerWiring.initialize();
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
        RedPowerWiring.initialize();
    }

    static {
        initialized = false;
    }
}

