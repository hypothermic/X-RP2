/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  forge.AchievementPage
 *  forge.IOreHandler
 *  forge.ITextureProvider
 *  forge.MinecraftForge
 *  net.minecraft.server.Achievement
 *  net.minecraft.server.AchievementList
 *  net.minecraft.server.Block
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.ModLoader
 */
package net.minecraft.server;

import eloraam.base.BlockAppliance;
import eloraam.base.BlockMicro;
import eloraam.base.ItemDrawplate;
import eloraam.base.ItemDyeIndigo;
import eloraam.base.ItemHandsaw;
import eloraam.base.ItemMicro;
import eloraam.base.ItemScrewdriver;
import eloraam.base.TileAdvBench;
import eloraam.base.TileAlloyFurnace;
import eloraam.core.AchieveLib;
import eloraam.core.Config;
import eloraam.core.CoreLib;
import eloraam.core.CoverLib;
import eloraam.core.CraftLib;
import eloraam.core.FluidClassBase;
import eloraam.core.ItemExtended;
import eloraam.core.ItemParts;
import eloraam.core.PipeLib;
import eloraam.core.ReflectLib;
import eloraam.core.TileCovered;
import forge.AchievementPage;
import forge.IOreHandler;
import forge.ITextureProvider;
import forge.MinecraftForge;
import net.minecraft.server.Achievement;
import net.minecraft.server.AchievementList;
import net.minecraft.server.Block;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.ModLoader;

public class RedPowerBase {
    private static boolean initialized = false;
    public static BlockAppliance blockAppliance;
    public static Item itemHandsawIron;
    public static Item itemHandsawDiamond;
    public static ItemParts itemLumar;
    public static ItemParts itemResource;
    public static ItemStack itemRuby;
    public static ItemStack itemEmerald;
    public static ItemStack itemSapphire;
    public static ItemStack itemIngotSilver;
    public static ItemStack itemIngotTin;
    public static ItemStack itemIngotCopper;
    public static ItemStack itemNikolite;
    public static ItemParts itemAlloy;
    public static ItemStack itemIngotRed;
    public static ItemStack itemIngotBlue;
    public static ItemStack itemIngotBrass;
    public static ItemStack itemBouleSilicon;
    public static ItemStack itemWaferSilicon;
    public static ItemStack itemWaferBlue;
    public static ItemStack itemWaferRed;
    public static ItemStack itemTinplate;
    public static ItemStack itemFineCopper;
    public static ItemStack itemFineIron;
    public static ItemStack itemCopperCoil;
    public static ItemStack itemMotor;
    public static ItemParts itemNugget;
    public static ItemStack itemNuggetIron;
    public static ItemStack itemNuggetSilver;
    public static ItemStack itemNuggetTin;
    public static ItemStack itemNuggetCopper;
    public static Item itemDyeIndigo;
    public static BlockMicro blockMicro;
    public static ItemScrewdriver itemScrewdriver;
    public static Item itemDrawplateDiamond;
    public static boolean reclaimRecipes;

    public static void initBaseItems() {
        itemLumar = new ItemParts(Config.getItemID("items.base.lumar.id"), "/eloraam/base/items1.png");
        for (int i = 0; i < 16; ++i) {
            itemLumar.addItem(i, 32 + i, "item.rplumar." + CoreLib.rawColorNames[i]);
            Config.addName("item.rplumar." + CoreLib.rawColorNames[i] + ".name", CoreLib.enColorNames[i] + " Lumar");
            ItemStack itemStack = new ItemStack(Item.INK_SACK, 1, 15 - i);
            ModLoader.addShapelessRecipe((ItemStack)new ItemStack((Item)itemLumar, 2, i), (Object[])new Object[]{Item.REDSTONE, itemStack, itemStack, Item.GLOWSTONE_DUST});
        }
        itemResource = new ItemParts(Config.getItemID("items.base.resource.id"), "/eloraam/base/items1.png");
        itemAlloy = new ItemParts(Config.getItemID("items.base.alloy.id"), "/eloraam/base/items1.png");
        itemResource.addItem(0, 48, "item.ruby");
        itemResource.addItem(1, 49, "item.greenEmerald");
        itemResource.addItem(2, 50, "item.sapphire");
        itemResource.addItem(3, 51, "item.ingotSilver");
        itemResource.addItem(4, 52, "item.ingotTin");
        itemResource.addItem(5, 53, "item.ingotCopper");
        itemResource.addItem(6, 54, "item.nikolite");
        itemAlloy.addItem(0, 64, "item.ingotRed");
        itemAlloy.addItem(1, 65, "item.ingotBlue");
        itemAlloy.addItem(2, 66, "item.ingotBrass");
        itemAlloy.addItem(3, 67, "item.bouleSilicon");
        itemAlloy.addItem(4, 68, "item.waferSilicon");
        itemAlloy.addItem(5, 69, "item.waferBlue");
        itemAlloy.addItem(6, 70, "item.waferRed");
        itemAlloy.addItem(7, 71, "item.tinplate");
        itemAlloy.addItem(8, 72, "item.finecopper");
        itemAlloy.addItem(9, 73, "item.fineiron");
        itemAlloy.addItem(10, 74, "item.coppercoil");
        itemAlloy.addItem(11, 75, "item.btmotor");
        itemRuby = new ItemStack((Item)itemResource, 1, 0);
        itemEmerald = new ItemStack((Item)itemResource, 1, 1);
        itemSapphire = new ItemStack((Item)itemResource, 1, 2);
        itemIngotSilver = new ItemStack((Item)itemResource, 1, 3);
        itemIngotTin = new ItemStack((Item)itemResource, 1, 4);
        itemIngotCopper = new ItemStack((Item)itemResource, 1, 5);
        itemNikolite = new ItemStack((Item)itemResource, 1, 6);
        itemIngotRed = new ItemStack((Item)itemAlloy, 1, 0);
        itemIngotBlue = new ItemStack((Item)itemAlloy, 1, 1);
        itemIngotBrass = new ItemStack((Item)itemAlloy, 1, 2);
        itemBouleSilicon = new ItemStack((Item)itemAlloy, 1, 3);
        itemWaferSilicon = new ItemStack((Item)itemAlloy, 1, 4);
        itemWaferBlue = new ItemStack((Item)itemAlloy, 1, 5);
        itemWaferRed = new ItemStack((Item)itemAlloy, 1, 6);
        itemTinplate = new ItemStack((Item)itemAlloy, 1, 7);
        itemFineCopper = new ItemStack((Item)itemAlloy, 1, 8);
        itemFineIron = new ItemStack((Item)itemAlloy, 1, 9);
        itemCopperCoil = new ItemStack((Item)itemAlloy, 1, 10);
        itemMotor = new ItemStack((Item)itemAlloy, 1, 11);
        MinecraftForge.registerOre((String)"gemRuby", (ItemStack)itemRuby);
        MinecraftForge.registerOre((String)"gemEmerald", (ItemStack)itemEmerald);
        MinecraftForge.registerOre((String)"gemSapphire", (ItemStack)itemSapphire);
        MinecraftForge.registerOre((String)"ingotTin", (ItemStack)itemIngotTin);
        MinecraftForge.registerOre((String)"ingotCopper", (ItemStack)itemIngotCopper);
        MinecraftForge.registerOre((String)"ingotSilver", (ItemStack)itemIngotSilver);
        MinecraftForge.registerOre((String)"ingotBrass", (ItemStack)itemIngotBrass);
        itemNugget = new ItemParts(Config.getItemID("items.base.nuggets.id"), "/eloraam/base/items1.png");
        itemNugget.addItem(0, 160, "item.nuggetIron");
        itemNugget.addItem(1, 161, "item.nuggetSilver");
        itemNugget.addItem(2, 162, "item.nuggetTin");
        itemNugget.addItem(3, 163, "item.nuggetCopper");
        itemNuggetIron = new ItemStack((Item)itemNugget, 1, 0);
        itemNuggetSilver = new ItemStack((Item)itemNugget, 1, 1);
        itemNuggetTin = new ItemStack((Item)itemNugget, 1, 2);
        itemNuggetCopper = new ItemStack((Item)itemNugget, 1, 3);
        itemDrawplateDiamond = new ItemDrawplate(Config.getItemID("items.base.drawplateDiamond.id"));
        itemDrawplateDiamond.a("drawplateDiamond").d(27).setMaxDurability(255);
        CraftLib.addDamageOnCraft(itemDrawplateDiamond);
    }

    public static void initOreDictionary() {
        MinecraftForge.registerOreHandler((IOreHandler)new IOreHandler(){

            public void registerOre(String string, ItemStack itemStack) {
                if (string.equals("ingotCopper")) {
                    CraftLib.addAlloyResult(RedPowerBase.itemIngotRed, new ItemStack[]{new ItemStack(Item.REDSTONE, 4), CoreLib.copyStack(itemStack, 1)});
                    for (ItemStack itemStack2 : MinecraftForge.getOreClass((String)"ingotTin")) {
                        CraftLib.addAlloyResult(CoreLib.copyStack(RedPowerBase.itemIngotBrass, 4), new ItemStack[]{itemStack2, CoreLib.copyStack(itemStack, 3)});
                    }
                    ModLoader.addShapelessRecipe((ItemStack)RedPowerBase.itemFineCopper, (Object[])new Object[]{itemStack, new ItemStack(RedPowerBase.itemDrawplateDiamond, 1, -1)});
                    ModLoader.addRecipe((ItemStack)CoreLib.copyStack(RedPowerBase.itemNuggetCopper, 9), (Object[])new Object[]{"I", Character.valueOf('I'), itemStack});
                }
                if (string.equals("ingotTin")) {
                    for (ItemStack itemStack2 : MinecraftForge.getOreClass((String)"ingotCopper")) {
                        CraftLib.addAlloyResult(CoreLib.copyStack(RedPowerBase.itemIngotBrass, 4), new ItemStack[]{itemStack, CoreLib.copyStack(itemStack2, 3)});
                    }
                    CraftLib.addAlloyResult(CoreLib.copyStack(RedPowerBase.itemTinplate, 4), new ItemStack[]{itemStack, new ItemStack(Item.IRON_INGOT, 2)});
                    ModLoader.addRecipe((ItemStack)CoreLib.copyStack(RedPowerBase.itemNuggetTin, 9), (Object[])new Object[]{"I", Character.valueOf('I'), itemStack});
                }
                if (string.equals("ingotSilver")) {
                    CraftLib.addAlloyResult(RedPowerBase.itemIngotBlue, new ItemStack[]{itemStack, CoreLib.copyStack(RedPowerBase.itemNikolite, 4)});
                    ModLoader.addRecipe((ItemStack)CoreLib.copyStack(RedPowerBase.itemNuggetSilver, 9), (Object[])new Object[]{"I", Character.valueOf('I'), itemStack});
                }
                if (string.equals("dyeBlue")) {
                    ModLoader.addShapelessRecipe((ItemStack)new ItemStack((Item)RedPowerBase.itemLumar, 2, 11), (Object[])new Object[]{Item.REDSTONE, itemStack, itemStack, Item.GLOWSTONE_DUST});
                }
            }
        });
    }

    public static void initIndigo() {
        itemDyeIndigo = new ItemDyeIndigo(Config.getItemID("items.base.dyeIndigo.id"));
        MinecraftForge.registerOre((String)"dyeBlue", (ItemStack)new ItemStack(itemDyeIndigo));
        ModLoader.addShapelessRecipe((ItemStack)new ItemStack(Block.WOOL.id, 1, 11), (Object[])new Object[]{itemDyeIndigo, Block.WOOL});
        ModLoader.addShapelessRecipe((ItemStack)new ItemStack(Item.INK_SACK, 2, 12), (Object[])new Object[]{itemDyeIndigo, new ItemStack(Item.INK_SACK, 1, 15)});
        ModLoader.addShapelessRecipe((ItemStack)new ItemStack(Item.INK_SACK, 2, 6), (Object[])new Object[]{itemDyeIndigo, new ItemStack(Item.INK_SACK, 1, 2)});
        ModLoader.addShapelessRecipe((ItemStack)new ItemStack(Item.INK_SACK, 2, 5), (Object[])new Object[]{itemDyeIndigo, new ItemStack(Item.INK_SACK, 1, 1)});
        ModLoader.addShapelessRecipe((ItemStack)new ItemStack(Item.INK_SACK, 3, 13), (Object[])new Object[]{itemDyeIndigo, new ItemStack(Item.INK_SACK, 1, 1), new ItemStack(Item.INK_SACK, 1, 9)});
        ModLoader.addShapelessRecipe((ItemStack)new ItemStack(Item.INK_SACK, 4, 13), (Object[])new Object[]{itemDyeIndigo, new ItemStack(Item.INK_SACK, 1, 1), new ItemStack(Item.INK_SACK, 1, 1), new ItemStack(Item.INK_SACK, 1, 15)});
    }

    public static void initAlloys() {
        CraftLib.addAlloyResult(itemIngotRed, new ItemStack[]{new ItemStack(Item.REDSTONE, 4), new ItemStack(Item.IRON_INGOT, 1)});
        if (reclaimRecipes) {
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 3), new ItemStack[]{new ItemStack(Block.RAILS, 8)});
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 3), new ItemStack[]{new ItemStack(Item.BUCKET, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 5), new ItemStack[]{new ItemStack(Item.MINECART, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 6), new ItemStack[]{new ItemStack(Item.IRON_DOOR, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 3), new ItemStack[]{new ItemStack(Block.IRON_FENCE, 8)});
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 2), new ItemStack[]{new ItemStack(Item.IRON_SWORD, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 3), new ItemStack[]{new ItemStack(Item.IRON_PICKAXE, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 3), new ItemStack[]{new ItemStack(Item.IRON_AXE, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 1), new ItemStack[]{new ItemStack(Item.IRON_SPADE, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 2), new ItemStack[]{new ItemStack(Item.IRON_HOE, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.GOLD_INGOT, 2), new ItemStack[]{new ItemStack(Item.GOLD_SWORD, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.GOLD_INGOT, 3), new ItemStack[]{new ItemStack(Item.GOLD_PICKAXE, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.GOLD_INGOT, 3), new ItemStack[]{new ItemStack(Item.GOLD_AXE, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.GOLD_INGOT, 1), new ItemStack[]{new ItemStack(Item.GOLD_SPADE, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.GOLD_INGOT, 2), new ItemStack[]{new ItemStack(Item.GOLD_HOE, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 5), new ItemStack[]{new ItemStack(Item.IRON_HELMET, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 8), new ItemStack[]{new ItemStack(Item.IRON_CHESTPLATE, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 7), new ItemStack[]{new ItemStack(Item.IRON_LEGGINGS, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 4), new ItemStack[]{new ItemStack(Item.IRON_BOOTS, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.GOLD_INGOT, 5), new ItemStack[]{new ItemStack(Item.GOLD_HELMET, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.GOLD_INGOT, 8), new ItemStack[]{new ItemStack(Item.GOLD_CHESTPLATE, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.GOLD_INGOT, 7), new ItemStack[]{new ItemStack(Item.GOLD_LEGGINGS, 1)});
            CraftLib.addAlloyResult(new ItemStack(Item.GOLD_INGOT, 4), new ItemStack[]{new ItemStack(Item.GOLD_BOOTS, 1)});
        }
        CraftLib.addAlloyResult(new ItemStack(Item.GOLD_INGOT, 1), new ItemStack[]{new ItemStack(Item.GOLD_NUGGET, 9)});
        CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 1), new ItemStack[]{CoreLib.copyStack(itemNuggetIron, 9)});
        CraftLib.addAlloyResult(itemIngotSilver, new ItemStack[]{CoreLib.copyStack(itemNuggetSilver, 9)});
        CraftLib.addAlloyResult(itemIngotCopper, new ItemStack[]{CoreLib.copyStack(itemNuggetCopper, 9)});
        CraftLib.addAlloyResult(itemIngotTin, new ItemStack[]{CoreLib.copyStack(itemNuggetTin, 9)});
        CraftLib.addAlloyResult(itemIngotCopper, new ItemStack[]{itemFineCopper});
        CraftLib.addAlloyResult(new ItemStack(Item.IRON_INGOT, 1), new ItemStack[]{itemFineIron});
        CraftLib.addAlloyResult(itemBouleSilicon, new ItemStack[]{new ItemStack(Item.COAL, 8, 0), new ItemStack(Block.SAND, 8)});
        CraftLib.addAlloyResult(itemBouleSilicon, new ItemStack[]{new ItemStack(Item.COAL, 8, 1), new ItemStack(Block.SAND, 8)});
        CraftLib.addAlloyResult(itemWaferBlue, new ItemStack[]{CoreLib.copyStack(itemWaferSilicon, 1), CoreLib.copyStack(itemNikolite, 4)});
        CraftLib.addAlloyResult(itemWaferRed, new ItemStack[]{CoreLib.copyStack(itemWaferSilicon, 1), new ItemStack(Item.REDSTONE, 4)});
    }

    public static void initMicroblocks() {
        blockMicro = new BlockMicro(Config.getBlockID("blocks.base.microblock.id"));
        blockMicro.a("rpwire");
        ModLoader.registerBlock((Block)blockMicro, ItemMicro.class);
        blockMicro.addTileEntityMapping(0, TileCovered.class);
        CoverLib.blockCoverPlate = blockMicro;
    }

    public static void initFluids() {
        FluidClassBase fluidClassBase = new FluidClassBase(1, Block.STATIONARY_WATER.id, Block.WATER.id, "/terrain.png", 205);
        FluidClassBase fluidClassBase2 = new FluidClassBase(2, Block.STATIONARY_LAVA.id, Block.LAVA.id, "/terrain.png", 237);
        PipeLib.registerFluidBlock(Block.STATIONARY_WATER.id, fluidClassBase);
        PipeLib.registerFluidBlock(Block.WATER.id, fluidClassBase);
        PipeLib.registerFluidBlock(Block.STATIONARY_LAVA.id, fluidClassBase2);
        PipeLib.registerFluidBlock(Block.LAVA.id, fluidClassBase2);
        PipeLib.registerFluid(1, fluidClassBase);
        PipeLib.registerFluid(2, fluidClassBase2);
        ReflectLib.callClassMethod("net.minecraft.server.BuildCraftEnergy", "initialize", new Class[0], new Object[0]);
        Block block = (Block)ReflectLib.getStaticField("net.minecraft.server.BuildCraftEnergy", "oilStill", Block.class);
        Block block2 = (Block)ReflectLib.getStaticField("net.minecraft.server.BuildCraftEnergy", "oilMoving", Block.class);
        if (block != null && block2 != null) {
            String string = "/terrain.png";
            if (block instanceof ITextureProvider) {
                string = ((ITextureProvider)block).getTextureFile();
            }
            FluidClassBase fluidClassBase3 = new FluidClassBase(256, block.id, block2.id, string, block.textureId);
            PipeLib.registerFluidBlock(block.id, fluidClassBase3);
            PipeLib.registerFluidBlock(block2.id, fluidClassBase3);
            PipeLib.registerFluid(256, fluidClassBase3);
        }
    }

    public static void initCoverMaterials() {
        CoverLib.addMaterial(0, 1, Block.COBBLESTONE, "cobble", "Cobblestone");
        CoverLib.addMaterial(1, 1, Block.STONE, "stone", "Stone");
        CoverLib.addMaterial(2, 0, Block.WOOD, "planks", "Wooden Plank");
        CoverLib.addMaterial(3, 1, Block.SANDSTONE, "sandstone", "Sandstone");
        CoverLib.addMaterial(4, 1, Block.MOSSY_COBBLESTONE, "moss", "Moss Stone");
        CoverLib.addMaterial(5, 1, Block.BRICK, "brick", "Brick");
        CoverLib.addMaterial(6, 2, Block.OBSIDIAN, "obsidian", "Obsidian");
        CoverLib.addMaterial(7, 1, true, Block.GLASS, "glass", "Glass");
        CoverLib.addMaterial(8, 0, Block.DIRT, "dirt", "Dirt");
        CoverLib.addMaterial(9, 0, Block.CLAY, "clay", "Clay");
        CoverLib.addMaterial(10, 0, Block.BOOKSHELF, "books", "Bookshelf");
        CoverLib.addMaterial(11, 0, Block.byId[87], "netherrack", "Netherrack");
        CoverLib.addMaterial(12, 0, Block.LOG, 0, "wood", "Wood");
        CoverLib.addMaterial(13, 0, Block.LOG, 1, "wood1", "Wood");
        CoverLib.addMaterial(14, 0, Block.LOG, 2, "wood2", "Wood");
        CoverLib.addMaterial(15, 0, Block.SOUL_SAND, "soul", "Soul Sand");
        CoverLib.addMaterial(16, 1, Block.STEP, "slab", "Polished Stone");
        CoverLib.addMaterial(17, 1, Block.IRON_BLOCK, "iron", "Iron");
        CoverLib.addMaterial(18, 1, Block.GOLD_BLOCK, "gold", "Gold");
        CoverLib.addMaterial(19, 2, Block.DIAMOND_BLOCK, "diamond", "Diamond");
        CoverLib.addMaterial(20, 1, Block.LAPIS_BLOCK, "lapis", "Lapis Lazuli");
        CoverLib.addMaterial(21, 0, Block.SNOW_BLOCK, "snow", "Snow");
        CoverLib.addMaterial(22, 0, Block.PUMPKIN, "pumpkin", "Pumpkin");
        CoverLib.addMaterial(23, 1, Block.SMOOTH_BRICK, 0, "stonebrick", "Stone Brick");
        CoverLib.addMaterial(24, 1, Block.SMOOTH_BRICK, 1, "stonebrick1", "Stone Brick");
        CoverLib.addMaterial(25, 1, Block.SMOOTH_BRICK, 2, "stonebrick2", "Stone Brick");
        CoverLib.addMaterial(26, 1, Block.NETHER_BRICK, "netherbrick", "Nether Brick");
        CoverLib.addMaterial(27, 1, Block.SMOOTH_BRICK, 3, "stonebrick3", "Stone Brick");
        CoverLib.addMaterial(28, 0, Block.WOOD, 1, "planks1", "Wooden Plank");
        CoverLib.addMaterial(29, 0, Block.WOOD, 2, "planks2", "Wooden Plank");
        CoverLib.addMaterial(30, 0, Block.WOOD, 3, "planks3", "Wooden Plank");
        CoverLib.addMaterial(31, 1, Block.SANDSTONE, 1, "sandstone1", "Sandstone");
        CoverLib.addMaterial(64, 1, Block.SANDSTONE, 2, "sandstone2", "Sandstone");
        CoverLib.addMaterial(65, 0, Block.LOG, 3, "wood3", "Wood");
        for (int i = 0; i < 16; ++i) {
            CoverLib.addMaterial(32 + i, 0, Block.WOOL, i, "wool." + CoreLib.rawColorNames[i], CoreLib.enColorNames[i] + " Wool");
        }
    }

    public static void initAchievements() {
        AchieveLib.registerAchievement(117027, "rpMakeAlloy", 0, 0, new ItemStack((Block)blockAppliance, 1, 0), (Object)AchievementList.j);
        AchieveLib.registerAchievement(117028, "rpMakeSaw", 4, 0, new ItemStack(itemHandsawDiamond), (Object)AchievementList.w);
        AchieveLib.registerAchievement(117029, "rpIngotRed", 2, 2, itemIngotRed, "rpMakeAlloy");
        AchieveLib.registerAchievement(117030, "rpIngotBlue", 2, 4, itemIngotBlue, "rpMakeAlloy");
        AchieveLib.registerAchievement(117031, "rpIngotBrass", 2, 6, itemIngotBrass, "rpMakeAlloy");
        AchieveLib.registerAchievement(117032, "rpAdvBench", -2, 0, new ItemStack((Block)blockAppliance, 1, 3), (Object)AchievementList.h);
        AchieveLib.addCraftingAchievement(new ItemStack((Block)blockAppliance, 1, 0), "rpMakeAlloy");
        AchieveLib.addCraftingAchievement(new ItemStack((Block)blockAppliance, 1, 3), "rpAdvBench");
        AchieveLib.addCraftingAchievement(new ItemStack(itemHandsawDiamond), "rpMakeSaw");
        AchieveLib.addAlloyAchievement(itemIngotRed, "rpIngotRed");
        AchieveLib.addAlloyAchievement(itemIngotBlue, "rpIngotBlue");
        AchieveLib.addAlloyAchievement(itemIngotBrass, "rpIngotBrass");
        MinecraftForge.registerAchievementPage((AchievementPage)AchieveLib.achievepage);
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        reclaimRecipes = Config.getInt("settings.base.reclaimRecipes") > 0;
        RedPowerBase.initBaseItems();
        RedPowerBase.initAlloys();
        RedPowerBase.initIndigo();
        RedPowerBase.initMicroblocks();
        RedPowerBase.initCoverMaterials();
        RedPowerBase.initOreDictionary();
        RedPowerBase.initFluids();
        blockAppliance = new BlockAppliance(Config.getBlockID("blocks.base.appliance.id"));
        ModLoader.registerBlock((Block)blockAppliance, ItemExtended.class);
        ModLoader.registerTileEntity(TileAlloyFurnace.class, (String)"RPAFurnace");
        blockAppliance.addTileEntityMapping(0, TileAlloyFurnace.class);
        blockAppliance.setItemName(0, "rpafurnace");
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockAppliance, 1, 0), (Object[])new Object[]{"BBB", "B B", "BBB", Character.valueOf('B'), Block.BRICK});
        ModLoader.registerTileEntity(TileAdvBench.class, (String)"RPAdvBench");
        blockAppliance.addTileEntityMapping(3, TileAdvBench.class);
        blockAppliance.setItemName(3, "rpabench");
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockAppliance, 1, 3), (Object[])new Object[]{"SSS", "WTW", "WCW", Character.valueOf('S'), Block.STONE, Character.valueOf('W'), Block.WOOD, Character.valueOf('T'), Block.WORKBENCH, Character.valueOf('C'), Block.CHEST});
        itemHandsawIron = new ItemHandsaw(Config.getItemID("items.base.handsawIron.id"), 0);
        itemHandsawDiamond = new ItemHandsaw(Config.getItemID("items.base.handsawDiamond.id"), 2);
        itemHandsawIron.a("handsawIron").d(17);
        itemHandsawIron.setMaxDurability(320);
        itemHandsawDiamond.a("handsawDiamond").d(18);
        itemHandsawDiamond.setMaxDurability(1280);
        ModLoader.addRecipe((ItemStack)new ItemStack(itemHandsawIron, 1), (Object[])new Object[]{"WWW", " II", " II", Character.valueOf('I'), Item.IRON_INGOT, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack)new ItemStack(itemHandsawDiamond, 1), (Object[])new Object[]{"WWW", " II", " DD", Character.valueOf('I'), Item.IRON_INGOT, Character.valueOf('D'), Item.DIAMOND, Character.valueOf('W'), Item.STICK});
        CoverLib.addSaw(itemHandsawIron, 0);
        CoverLib.addSaw(itemHandsawDiamond, 2);
        ModLoader.addShapelessRecipe((ItemStack)CoreLib.copyStack(itemWaferSilicon, 16), (Object[])new Object[]{itemBouleSilicon, new ItemStack(itemHandsawDiamond, 1, -1)});
        itemScrewdriver = new ItemScrewdriver(Config.getItemID("items.base.screwdriver.id"));
        itemScrewdriver.a("screwdriver").d(16);
        ModLoader.addRecipe((ItemStack)new ItemStack((Item)itemScrewdriver, 1), (Object[])new Object[]{"I ", " W", Character.valueOf('I'), Item.IRON_INGOT, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack)new ItemStack(itemDrawplateDiamond, 1), (Object[])new Object[]{" I ", "IDI", " I ", Character.valueOf('I'), new ItemStack((Block)blockMicro, 1, 5649), Character.valueOf('D'), new ItemStack((Block)blockMicro, 1, 4115)});
        ModLoader.addShapelessRecipe((ItemStack)itemFineIron, (Object[])new Object[]{Item.IRON_INGOT, new ItemStack(itemDrawplateDiamond, 1, -1)});
        ModLoader.addRecipe((ItemStack)CoreLib.copyStack(itemNuggetIron, 9), (Object[])new Object[]{"I", Character.valueOf('I'), Item.IRON_INGOT});
        ModLoader.addRecipe((ItemStack)new ItemStack(Item.IRON_INGOT, 1, 0), (Object[])new Object[]{"III", "III", "III", Character.valueOf('I'), itemNuggetIron});
        ModLoader.addRecipe((ItemStack)itemIngotSilver, (Object[])new Object[]{"III", "III", "III", Character.valueOf('I'), itemNuggetSilver});
        ModLoader.addRecipe((ItemStack)itemIngotTin, (Object[])new Object[]{"III", "III", "III", Character.valueOf('I'), itemNuggetTin});
        ModLoader.addRecipe((ItemStack)itemIngotCopper, (Object[])new Object[]{"III", "III", "III", Character.valueOf('I'), itemNuggetCopper});
        ModLoader.addRecipe((ItemStack)new ItemStack(Item.DIAMOND, 2), (Object[])new Object[]{"D", Character.valueOf('D'), new ItemStack((Block)blockMicro, 1, 4115)});
        ModLoader.addRecipe((ItemStack)new ItemStack(Item.DIAMOND, 1), (Object[])new Object[]{"D", Character.valueOf('D'), new ItemStack((Block)blockMicro, 1, 19)});
        ModLoader.addRecipe((ItemStack)new ItemStack(Item.IRON_INGOT, 2), (Object[])new Object[]{"I", Character.valueOf('I'), new ItemStack((Block)blockMicro, 1, 4113)});
        ModLoader.addRecipe((ItemStack)new ItemStack(Item.IRON_INGOT, 1), (Object[])new Object[]{"I", Character.valueOf('I'), new ItemStack((Block)blockMicro, 1, 17)});
        RedPowerBase.initAchievements();
        Config.saveConfig();
    }

    static {
        reclaimRecipes = true;
    }

}

