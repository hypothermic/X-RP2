/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  forge.IOreHandler
 *  forge.MinecraftForge
 *  net.minecraft.server.Block
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.ModLoader
 */
package net.minecraft.server;

import eloraam.base.BlockMicro;
import eloraam.core.Config;
import eloraam.core.CoreLib;
import eloraam.core.CoverLib;
import eloraam.core.CraftLib;
import eloraam.core.IMicroPlacement;
import eloraam.core.RedPowerLib;
import eloraam.core.TileCovered;
import eloraam.wiring.MicroPlacementJacket;
import eloraam.wiring.MicroPlacementWire;
import eloraam.wiring.TileBluewire;
import eloraam.wiring.TileCable;
import eloraam.wiring.TileInsulatedWire;
import eloraam.wiring.TileRedwire;
import forge.IOreHandler;
import forge.MinecraftForge;
import net.minecraft.server.Block;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.ModLoader;
import net.minecraft.server.RedPowerBase;

public class RedPowerWiring {
    private static boolean initialized = false;

    private static void initJacketRecipes() {
        CoverLib.addMaterialHandler(new CoverLib.IMaterialHandler(){

            @Override
            public void addMaterial(int n) {
                if (CoverLib.isTransparent(n)) {
                    return;
                }
                String string = CoverLib.getName(n);
                String string2 = CoverLib.getDesc(n);
                Config.addName("tile.rparmwire." + string + ".name", string2 + " Jacketed Wire");
                Config.addName("tile.rparmcable." + string + ".name", string2 + " Jacketed Cable");
                Config.addName("tile.rparmbwire." + string + ".name", string2 + " Jacketed Bluewire");
                ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 4, 16384 + n), (Object[])new Object[]{"SSS", "SRS", "SSS", Character.valueOf('S'), new ItemStack((Block)RedPowerBase.blockMicro, 1, n), Character.valueOf('R'), RedPowerBase.itemIngotRed});
                ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 1, 16640 + n), (Object[])new Object[]{"SSS", "SCS", "SSS", Character.valueOf('S'), new ItemStack((Block)RedPowerBase.blockMicro, 1, n), Character.valueOf('C'), new ItemStack((Block)RedPowerBase.blockMicro, 1, 768)});
                ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 4, 16896 + n), (Object[])new Object[]{"SSS", "SBS", "SSS", Character.valueOf('S'), new ItemStack((Block)RedPowerBase.blockMicro, 1, n), Character.valueOf('B'), RedPowerBase.itemIngotBlue});
                CraftLib.addAlloyResult(CoreLib.copyStack(RedPowerBase.itemIngotRed, 1), new ItemStack[]{new ItemStack((Block)RedPowerBase.blockMicro, 4, 16384 + n)});
                CraftLib.addAlloyResult(CoreLib.copyStack(RedPowerBase.itemIngotRed, 5), new ItemStack[]{new ItemStack((Block)RedPowerBase.blockMicro, 8, 16640 + n)});
                CraftLib.addAlloyResult(CoreLib.copyStack(RedPowerBase.itemIngotBlue, 1), new ItemStack[]{new ItemStack((Block)RedPowerBase.blockMicro, 4, 16896 + n)});
            }
        });
    }

    private static void initOreDictionary() {
        MinecraftForge.registerOreHandler((IOreHandler)new IOreHandler(){

            public void registerOre(String string, ItemStack itemStack) {
                if (string.equals("dyeBlue")) {
                    for (int i = 0; i < 16; ++i) {
                        if (i == 11) continue;
                        ModLoader.addShapelessRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 1, 523), (Object[])new Object[]{new ItemStack((Block)RedPowerBase.blockMicro, 1, 512 + i), itemStack});
                        ModLoader.addShapelessRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 1, 780), (Object[])new Object[]{new ItemStack((Block)RedPowerBase.blockMicro, 1, 769 + i), itemStack});
                    }
                    ModLoader.addShapelessRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 1, 780), (Object[])new Object[]{new ItemStack((Block)RedPowerBase.blockMicro, 1, 768), itemStack, Item.PAPER});
                }
            }
        });
    }

    public static void initialize() {
        int n;
        int n2;
        if (initialized) {
            return;
        }
        initialized = true;
        RedPowerWiring.initJacketRecipes();
        RedPowerWiring.initOreDictionary();
        ModLoader.registerTileEntity(TileRedwire.class, (String)"Redwire");
        ModLoader.registerTileEntity(TileInsulatedWire.class, (String)"InsRedwire");
        ModLoader.registerTileEntity(TileCable.class, (String)"RedCable");
        ModLoader.registerTileEntity(TileCovered.class, (String)"Covers");
        ModLoader.registerTileEntity(TileBluewire.class, (String)"Bluewire");
        IMicroPlacement iMicroPlacement = new MicroPlacementWire();
        RedPowerBase.blockMicro.registerPlacement(1, iMicroPlacement);
        RedPowerBase.blockMicro.registerPlacement(2, iMicroPlacement);
        RedPowerBase.blockMicro.registerPlacement(3, iMicroPlacement);
        RedPowerBase.blockMicro.registerPlacement(5, iMicroPlacement);
        iMicroPlacement = new MicroPlacementJacket();
        RedPowerBase.blockMicro.registerPlacement(64, iMicroPlacement);
        RedPowerBase.blockMicro.registerPlacement(65, iMicroPlacement);
        RedPowerBase.blockMicro.registerPlacement(66, iMicroPlacement);
        RedPowerBase.blockMicro.addTileEntityMapping(1, TileRedwire.class);
        RedPowerBase.blockMicro.addTileEntityMapping(2, TileInsulatedWire.class);
        RedPowerBase.blockMicro.addTileEntityMapping(3, TileCable.class);
        RedPowerBase.blockMicro.addTileEntityMapping(5, TileBluewire.class);
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 12, 256), (Object[])new Object[]{"R", "R", "R", Character.valueOf('R'), RedPowerBase.itemIngotRed});
        CraftLib.addAlloyResult(RedPowerBase.itemIngotRed, new ItemStack[]{new ItemStack((Block)RedPowerBase.blockMicro, 4, 256)});
        CraftLib.addAlloyResult(CoreLib.copyStack(RedPowerBase.itemIngotRed, 5), new ItemStack[]{new ItemStack((Block)RedPowerBase.blockMicro, 8, 768)});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 12, 1280), (Object[])new Object[]{"WBW", "WBW", "WBW", Character.valueOf('B'), RedPowerBase.itemIngotBlue, Character.valueOf('W'), Block.WOOL});
        CraftLib.addAlloyResult(RedPowerBase.itemIngotBlue, new ItemStack[]{new ItemStack((Block)RedPowerBase.blockMicro, 4, 1280)});
        for (n2 = 0; n2 < 16; ++n2) {
            Config.addName("tile.rpinsulated." + CoreLib.rawColorNames[n2] + ".name", CoreLib.enColorNames[n2] + " Insulated Wire");
            Config.addName("tile.rpcable." + CoreLib.rawColorNames[n2] + ".name", CoreLib.enColorNames[n2] + " Bundled Cable");
            ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 12, 512 + n2), (Object[])new Object[]{"WRW", "WRW", "WRW", Character.valueOf('R'), RedPowerBase.itemIngotRed, Character.valueOf('W'), new ItemStack(Block.WOOL, 1, n2)});
            for (n = 0; n < 16; ++n) {
                if (n2 == n) continue;
                ModLoader.addShapelessRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 1, 512 + n2), (Object[])new Object[]{new ItemStack((Block)RedPowerBase.blockMicro, 1, 512 + n), new ItemStack(Item.INK_SACK, 1, 15 - n2)});
                ModLoader.addShapelessRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 1, 769 + n2), (Object[])new Object[]{new ItemStack((Block)RedPowerBase.blockMicro, 1, 769 + n), new ItemStack(Item.INK_SACK, 1, 15 - n2)});
            }
            CraftLib.addAlloyResult(RedPowerBase.itemIngotRed, new ItemStack[]{new ItemStack((Block)RedPowerBase.blockMicro, 4, 512 + n2)});
            ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 2, 768), (Object[])new Object[]{"SWS", "WWW", "SWS", Character.valueOf('W'), new ItemStack((Block)RedPowerBase.blockMicro, 1, 512 + n2), Character.valueOf('S'), Item.STRING});
            ModLoader.addShapelessRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 1, 769 + n2), (Object[])new Object[]{new ItemStack((Block)RedPowerBase.blockMicro, 1, 768), new ItemStack(Item.INK_SACK, 1, 15 - n2), Item.PAPER});
            CraftLib.addAlloyResult(CoreLib.copyStack(RedPowerBase.itemIngotRed, 5), new ItemStack[]{new ItemStack((Block)RedPowerBase.blockMicro, 8, 769 + n2)});
        }
        RedPowerLib.addCompatibleMapping(0, 1);
        for (n2 = 0; n2 < 16; ++n2) {
            RedPowerLib.addCompatibleMapping(0, 2 + n2);
            RedPowerLib.addCompatibleMapping(1, 2 + n2);
            RedPowerLib.addCompatibleMapping(65, 2 + n2);
            for (n = 0; n < 16; ++n) {
                RedPowerLib.addCompatibleMapping(19 + n, 2 + n2);
            }
            RedPowerLib.addCompatibleMapping(18, 2 + n2);
            RedPowerLib.addCompatibleMapping(18, 19 + n2);
        }
        RedPowerLib.addCompatibleMapping(0, 65);
        RedPowerLib.addCompatibleMapping(1, 65);
        RedPowerLib.addCompatibleMapping(64, 65);
        Config.saveConfig();
    }

}

