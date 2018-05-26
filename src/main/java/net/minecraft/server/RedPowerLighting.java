/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.Block
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.ModLoader
 */
package net.minecraft.server;

import eloraam.core.Config;
import eloraam.core.CoreLib;
import eloraam.core.ItemParts;
import eloraam.core.ReflectLib;
import eloraam.lighting.BlockLamp;
import eloraam.lighting.ItemLamp;
import net.minecraft.server.Block;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.ModLoader;
import net.minecraft.server.RedPowerBase;

public class RedPowerLighting {
    private static boolean initialized = false;
    public static BlockLamp blockLampOff;
    public static BlockLamp blockLampOn;
    public static final String textureFile = "/eloraam/lighting/lighting1.png";

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        blockLampOff = new BlockLamp(Config.getBlockID("blocks.lighting.lampOff.id"), false);
        blockLampOn = new BlockLamp(Config.getBlockID("blocks.lighting.lampOn.id"), true);
        blockLampOn.a(1.0f);
        blockLampOff.a("rplampoff");
        blockLampOn.a("rplampon");
        ModLoader.registerBlock((Block)blockLampOn);
        ModLoader.registerBlock((Block)blockLampOff, ItemLamp.class);
        ReflectLib.callClassMethod("TMIItemInfo", "hideItem", new Class[]{Integer.TYPE}, new Object[]{RedPowerLighting.blockLampOn.id});
        for (int i = 0; i < 16; ++i) {
            Config.addName("tile.rplamp." + CoreLib.rawColorNames[i] + ".name", CoreLib.enColorNames[i] + " Lamp");
            ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockLampOff, 1, i), (Object[])new Object[]{"GLG", "GLG", "GRG", Character.valueOf('G'), Block.GLASS, Character.valueOf('L'), new ItemStack((Item)RedPowerBase.itemLumar, 1, i), Character.valueOf('R'), Item.REDSTONE});
        }
        Config.saveConfig();
    }
}

