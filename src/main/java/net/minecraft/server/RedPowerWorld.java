/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  forge.IBonemealHandler
 *  forge.IOreHandler
 *  forge.MinecraftForge
 *  net.minecraft.server.BiomeBase
 *  net.minecraft.server.Block
 *  net.minecraft.server.Enchantment
 *  net.minecraft.server.FurnaceRecipes
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.ModLoader
 *  net.minecraft.server.World
 *  net.minecraft.server.WorldChunkManager
 *  net.minecraft.server.WorldGenFlowers
 */
package net.minecraft.server;

import eloraam.base.ItemHandsaw;
import eloraam.core.*;
import eloraam.world.*;
import forge.IBonemealHandler;
import forge.IOreHandler;
import forge.MinecraftForge;

import java.util.Random;

public class RedPowerWorld {
    private static boolean initialized = false;
    public static BlockCustomFlower blockPlants;
    public static BlockCustomOre blockOres;
    public static BlockCustomLeaves blockLeaves;
    public static BlockCustomLog blockLogs;
    public static BlockCustomStone blockStone;
    public static BlockCustomCrops blockCrops;
    public static BlockStorage blockStorage;
    public static ItemStack itemOreRuby;
    public static ItemStack itemOreEmerald;
    public static ItemStack itemOreSapphire;
    public static ItemStack itemMarble;
    public static ItemSickle itemSickleWood;
    public static ItemSickle itemSickleStone;
    public static ItemSickle itemSickleIron;
    public static ItemSickle itemSickleDiamond;
    public static ItemSickle itemSickleGold;
    public static ItemSickle itemSickleRuby;
    public static ItemSickle itemSickleEmerald;
    public static ItemSickle itemSickleSapphire;
    public static ItemCustomPickaxe itemPickaxeRuby;
    public static ItemCustomPickaxe itemPickaxeEmerald;
    public static ItemCustomPickaxe itemPickaxeSapphire;
    public static ItemCustomShovel itemShovelRuby;
    public static ItemCustomShovel itemShovelEmerald;
    public static ItemCustomShovel itemShovelSapphire;
    public static ItemCustomAxe itemAxeRuby;
    public static ItemCustomAxe itemAxeEmerald;
    public static ItemCustomAxe itemAxeSapphire;
    public static ItemCustomSword itemSwordRuby;
    public static ItemCustomSword itemSwordEmerald;
    public static ItemCustomSword itemSwordSapphire;
    public static ItemAthame itemAthame;
    public static ItemCustomHoe itemHoeRuby;
    public static ItemCustomHoe itemHoeEmerald;
    public static ItemCustomHoe itemHoeSapphire;
    public static ItemCustomSeeds itemSeeds;
    public static Item itemHandsawRuby;
    public static Item itemHandsawEmerald;
    public static Item itemHandsawSapphire;
    public static Item itemBrushDry;
    public static Item itemPaintCanEmpty;
    public static Item[] itemBrushPaint;
    public static Item[] itemPaintCanPaint;
    public static Item itemWoolCard;
    public static Enchantment enchantDisjunction;
    public static final String blockTextureFile = "/eloraam/world/world1.png";
    public static final String itemTextureFile = "/eloraam/world/worlditems1.png";

    public static void setupBonemealHandler() {
        MinecraftForge.registerBonemealHandler((IBonemealHandler) new IBonemealHandler() {

            public boolean onUseBonemeal(World world, int n, int n2, int n3, int n4) {
                if (n == RedPowerWorld.blockCrops.id) {
                    int n5 = world.getData(n2, n3, n4);
                    if (n5 == 4 || n5 == 5) {
                        return false;
                    }
                    if (CoreProxy.isClient(world)) {
                        return true;
                    }
                    return RedPowerWorld.blockCrops.fertilize(world, n2, n3, n4);
                }
                return false;
            }
        });
    }

    public static void initOreDictionary() {
        MinecraftForge.registerOreHandler((IOreHandler) new IOreHandler() {

            public void registerOre(String string, ItemStack itemStack) {
                if (string.equals("dyeBlue")) {
                    ModLoader.addShapelessRecipe((ItemStack) new ItemStack(RedPowerWorld.itemPaintCanPaint[11]), (Object[]) new Object[]{RedPowerWorld.itemPaintCanEmpty, itemStack, new ItemStack((Item) RedPowerWorld.itemSeeds, 1, 0), new ItemStack((Item) RedPowerWorld.itemSeeds, 1, 0)});
                }
                if (string.equals("ingotSilver")) {
                    ModLoader.addRecipe((ItemStack) new ItemStack((Item) RedPowerWorld.itemAthame, 1), (Object[]) new Object[]{"S", "W", Character.valueOf('S'), itemStack, Character.valueOf('W'), Item.STICK});
                }
            }
        });
    }

    public static void initialize() {
        int n;
        if (initialized) {
            return;
        }
        initialized = true;
        RedPowerWorld.setupBonemealHandler();
        blockPlants = new BlockCustomFlower(Config.getBlockID("blocks.world.plants.id"), 1);
        blockPlants.a("indigo");
        ModLoader.registerBlock((Block) blockPlants, ItemCustomFlower.class);
        MinecraftForge.addGrassPlant((int) RedPowerWorld.blockPlants.id, (int) 0, (int) 10);
        ModLoader.addShapelessRecipe((ItemStack) new ItemStack(RedPowerBase.itemDyeIndigo, 2, 0), (Object[]) new Object[]{blockPlants});
        itemSeeds = new ItemCustomSeeds(Config.getItemID("items.world.seeds.id"));
        MinecraftForge.addGrassSeed((int) RedPowerWorld.itemSeeds.id, (int) 0, (int) 1, (int) 5);
        blockCrops = new BlockCustomCrops(Config.getBlockID("blocks.world.crops.id"));
        ModLoader.registerBlock((Block) blockCrops);
        itemPickaxeRuby = new ItemCustomPickaxe(Config.getItemID("items.world.pickaxeRuby.id"));
        itemPickaxeRuby.a("pickaxeRuby");
        itemPickaxeRuby.a(0, 4);
        itemPickaxeRuby.setMaxDurability(500);
        RedPowerWorld.itemPickaxeRuby.a = 8.0f;
        itemPickaxeEmerald = new ItemCustomPickaxe(Config.getItemID("items.world.pickaxeEmerald.id"));
        itemPickaxeEmerald.a("pickaxeEmerald");
        itemPickaxeEmerald.a(1, 4);
        itemPickaxeEmerald.setMaxDurability(500);
        RedPowerWorld.itemPickaxeEmerald.a = 8.0f;
        itemPickaxeSapphire = new ItemCustomPickaxe(Config.getItemID("items.world.pickaxeSapphire.id"));
        itemPickaxeSapphire.a("pickaxeSapphire");
        itemPickaxeSapphire.a(2, 4);
        itemPickaxeSapphire.setMaxDurability(500);
        RedPowerWorld.itemPickaxeSapphire.a = 8.0f;
        MinecraftForge.setToolClass((Item) itemPickaxeRuby, (String) "pickaxe", (int) 2);
        MinecraftForge.setToolClass((Item) itemPickaxeEmerald, (String) "pickaxe", (int) 2);
        MinecraftForge.setToolClass((Item) itemPickaxeSapphire, (String) "pickaxe", (int) 2);
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemPickaxeRuby, 1), (Object[]) new Object[]{"GGG", " W ", " W ", Character.valueOf('G'), RedPowerBase.itemRuby, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemPickaxeEmerald, 1), (Object[]) new Object[]{"GGG", " W ", " W ", Character.valueOf('G'), RedPowerBase.itemEmerald, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemPickaxeSapphire, 1), (Object[]) new Object[]{"GGG", " W ", " W ", Character.valueOf('G'), RedPowerBase.itemSapphire, Character.valueOf('W'), Item.STICK});
        itemShovelRuby = new ItemCustomShovel(Config.getItemID("items.world.shovelRuby.id"));
        itemShovelRuby.a("shovelRuby");
        itemShovelRuby.a(0, 3);
        itemShovelRuby.setMaxDurability(500);
        RedPowerWorld.itemShovelRuby.a = 8.0f;
        itemShovelEmerald = new ItemCustomShovel(Config.getItemID("items.world.shovelEmerald.id"));
        itemShovelEmerald.a("shovelEmerald");
        itemShovelEmerald.a(1, 3);
        itemShovelEmerald.setMaxDurability(500);
        RedPowerWorld.itemShovelEmerald.a = 8.0f;
        itemShovelSapphire = new ItemCustomShovel(Config.getItemID("items.world.shovelSapphire.id"));
        itemShovelSapphire.a("shovelSapphire");
        itemShovelSapphire.a(2, 3);
        itemShovelSapphire.setMaxDurability(500);
        RedPowerWorld.itemShovelSapphire.a = 8.0f;
        MinecraftForge.setToolClass((Item) itemShovelRuby, (String) "shovel", (int) 2);
        MinecraftForge.setToolClass((Item) itemShovelEmerald, (String) "shovel", (int) 2);
        MinecraftForge.setToolClass((Item) itemShovelSapphire, (String) "shovel", (int) 2);
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemShovelRuby, 1), (Object[]) new Object[]{"G", "W", "W", Character.valueOf('G'), RedPowerBase.itemRuby, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemShovelEmerald, 1), (Object[]) new Object[]{"G", "W", "W", Character.valueOf('G'), RedPowerBase.itemEmerald, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemShovelSapphire, 1), (Object[]) new Object[]{"G", "W", "W", Character.valueOf('G'), RedPowerBase.itemSapphire, Character.valueOf('W'), Item.STICK});
        itemAxeRuby = new ItemCustomAxe(Config.getItemID("items.world.axeRuby.id"));
        itemAxeRuby.a("axeRuby");
        itemAxeRuby.a(0, 5);
        itemAxeRuby.setMaxDurability(500);
        RedPowerWorld.itemAxeRuby.a = 8.0f;
        itemAxeEmerald = new ItemCustomAxe(Config.getItemID("items.world.axeEmerald.id"));
        itemAxeEmerald.a("axeEmerald");
        itemAxeEmerald.a(1, 5);
        itemAxeEmerald.setMaxDurability(500);
        RedPowerWorld.itemAxeEmerald.a = 8.0f;
        itemAxeSapphire = new ItemCustomAxe(Config.getItemID("items.world.axeSapphire.id"));
        itemAxeSapphire.a("axeSapphire");
        itemAxeSapphire.a(2, 5);
        itemAxeSapphire.setMaxDurability(500);
        RedPowerWorld.itemAxeSapphire.a = 8.0f;
        MinecraftForge.setToolClass((Item) itemAxeRuby, (String) "axe", (int) 2);
        MinecraftForge.setToolClass((Item) itemAxeEmerald, (String) "axe", (int) 2);
        MinecraftForge.setToolClass((Item) itemAxeSapphire, (String) "axe", (int) 2);
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemAxeRuby, 1), (Object[]) new Object[]{"GG", "GW", " W", Character.valueOf('G'), RedPowerBase.itemRuby, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemAxeEmerald, 1), (Object[]) new Object[]{"GG", "GW", " W", Character.valueOf('G'), RedPowerBase.itemEmerald, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemAxeSapphire, 1), (Object[]) new Object[]{"GG", "GW", " W", Character.valueOf('G'), RedPowerBase.itemSapphire, Character.valueOf('W'), Item.STICK});
        itemSwordRuby = new ItemCustomSword(Config.getItemID("items.world.swordRuby.id"));
        itemSwordRuby.a("swordRuby");
        itemSwordRuby.a(0, 2);
        itemSwordRuby.setMaxDurability(500);
        itemSwordEmerald = new ItemCustomSword(Config.getItemID("items.world.swordEmerald.id"));
        itemSwordEmerald.a("swordEmerald");
        itemSwordEmerald.a(1, 2);
        itemSwordEmerald.setMaxDurability(500);
        itemSwordSapphire = new ItemCustomSword(Config.getItemID("items.world.swordSapphire.id"));
        itemSwordSapphire.a("swordSapphire");
        itemSwordSapphire.a(2, 2);
        itemSwordSapphire.setMaxDurability(500);
        itemAthame = new ItemAthame(Config.getItemID("items.world.athame.id"));
        itemAthame.a("athame");
        MinecraftForge.setToolClass((Item) itemSwordRuby, (String) "sword", (int) 2);
        MinecraftForge.setToolClass((Item) itemSwordEmerald, (String) "sword", (int) 2);
        MinecraftForge.setToolClass((Item) itemSwordSapphire, (String) "sword", (int) 2);
        MinecraftForge.setToolClass((Item) itemAthame, (String) "sword", (int) 0);
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemSwordRuby, 1), (Object[]) new Object[]{"G", "G", "W", Character.valueOf('G'), RedPowerBase.itemRuby, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemSwordEmerald, 1), (Object[]) new Object[]{"G", "G", "W", Character.valueOf('G'), RedPowerBase.itemEmerald, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemSwordSapphire, 1), (Object[]) new Object[]{"G", "G", "W", Character.valueOf('G'), RedPowerBase.itemSapphire, Character.valueOf('W'), Item.STICK});
        itemHoeRuby = new ItemCustomHoe(Config.getItemID("items.world.hoeRuby.id"));
        itemHoeRuby.a("hoeRuby");
        itemHoeRuby.a(0, 6);
        itemHoeRuby.setMaxDurability(500);
        itemHoeEmerald = new ItemCustomHoe(Config.getItemID("items.world.hoeEmerald.id"));
        itemHoeEmerald.a("hoeEmerald");
        itemHoeEmerald.a(1, 6);
        itemHoeEmerald.setMaxDurability(500);
        itemHoeSapphire = new ItemCustomHoe(Config.getItemID("items.world.hoeSapphire.id"));
        itemHoeSapphire.a("hoeSapphire");
        itemHoeSapphire.a(2, 6);
        itemHoeSapphire.setMaxDurability(500);
        MinecraftForge.setToolClass((Item) itemHoeRuby, (String) "hoe", (int) 2);
        MinecraftForge.setToolClass((Item) itemHoeEmerald, (String) "hoe", (int) 2);
        MinecraftForge.setToolClass((Item) itemHoeSapphire, (String) "hoe", (int) 2);
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemHoeRuby, 1), (Object[]) new Object[]{"GG", " W", " W", Character.valueOf('G'), RedPowerBase.itemRuby, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemHoeEmerald, 1), (Object[]) new Object[]{"GG", " W", " W", Character.valueOf('G'), RedPowerBase.itemEmerald, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemHoeSapphire, 1), (Object[]) new Object[]{"GG", " W", " W", Character.valueOf('G'), RedPowerBase.itemSapphire, Character.valueOf('W'), Item.STICK});
        itemSickleWood = new ItemSickle(Config.getItemID("items.world.sickleWood.id"));
        itemSickleWood.a("sickleWood");
        itemSickleWood.a(0, 1);
        itemSickleWood.setMaxDurability(59);
        itemSickleStone = new ItemSickle(Config.getItemID("items.world.sickleStone.id"));
        itemSickleStone.a("sickleStone");
        itemSickleStone.a(1, 1);
        itemSickleStone.setMaxDurability(131);
        itemSickleIron = new ItemSickle(Config.getItemID("items.world.sickleIron.id"));
        itemSickleIron.a("sickleIron");
        itemSickleIron.a(2, 1);
        itemSickleIron.setMaxDurability(250);
        itemSickleDiamond = new ItemSickle(Config.getItemID("items.world.sickleDiamond.id"));
        itemSickleDiamond.a("sickleDiamond");
        itemSickleDiamond.a(3, 1);
        itemSickleDiamond.setMaxDurability(1561);
        itemSickleGold = new ItemSickle(Config.getItemID("items.world.sickleGold.id"));
        itemSickleGold.a("sickleGold");
        itemSickleGold.a(4, 1);
        itemSickleGold.setMaxDurability(32);
        itemSickleRuby = new ItemSickle(Config.getItemID("items.world.sickleRuby.id"));
        itemSickleRuby.a("sickleRuby");
        itemSickleRuby.a(5, 1);
        itemSickleRuby.setMaxDurability(500);
        itemSickleEmerald = new ItemSickle(Config.getItemID("items.world.sickleEmerald.id"));
        itemSickleEmerald.a("sickleEmerald");
        itemSickleEmerald.a(6, 1);
        itemSickleEmerald.setMaxDurability(500);
        itemSickleSapphire = new ItemSickle(Config.getItemID("items.world.sickleSapphire.id"));
        itemSickleSapphire.a("sickleSapphire");
        itemSickleSapphire.a(7, 1);
        itemSickleSapphire.setMaxDurability(500);
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemSickleWood, 1), (Object[]) new Object[]{" I ", "  I", "WI ", Character.valueOf('I'), Block.WOOD, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemSickleStone, 1), (Object[]) new Object[]{" I ", "  I", "WI ", Character.valueOf('I'), Block.COBBLESTONE, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemSickleIron, 1), (Object[]) new Object[]{" I ", "  I", "WI ", Character.valueOf('I'), Item.IRON_INGOT, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemSickleDiamond, 1), (Object[]) new Object[]{" I ", "  I", "WI ", Character.valueOf('I'), Item.DIAMOND, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemSickleGold, 1), (Object[]) new Object[]{" I ", "  I", "WI ", Character.valueOf('I'), Item.GOLD_INGOT, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemSickleRuby, 1), (Object[]) new Object[]{" I ", "  I", "WI ", Character.valueOf('I'), RedPowerBase.itemRuby, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemSickleEmerald, 1), (Object[]) new Object[]{" I ", "  I", "WI ", Character.valueOf('I'), RedPowerBase.itemEmerald, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemSickleSapphire, 1), (Object[]) new Object[]{" I ", "  I", "WI ", Character.valueOf('I'), RedPowerBase.itemSapphire, Character.valueOf('W'), Item.STICK});
        blockStone = new BlockCustomStone(Config.getBlockID("blocks.world.stone.id"));
        blockStone.a("rpstone");
        ModLoader.registerBlock((Block) blockStone, ItemCustomStone.class);
        itemMarble = new ItemStack((Block) blockStone, 0);
        MinecraftForge.setBlockHarvestLevel((Block) blockStone, (String) "pickaxe", (int) 0);
        CoverLib.addMaterial(48, 1, blockStone, 0, "marble", "Marble");
        CoverLib.addMaterial(49, 1, blockStone, 1, "basalt", "Basalt");
        CoverLib.addMaterial(50, 1, blockStone, 2, "marbleBrick", "Marble Brick");
        CoverLib.addMaterial(51, 1, blockStone, 3, "basaltCobble", "Basalt Cobblestone");
        CoverLib.addMaterial(52, 1, blockStone, 4, "basaltBrick", "Basalt Brick");
        blockOres = new BlockCustomOre(Config.getBlockID("blocks.world.ores.id"));
        blockOres.a("rpores");
        ModLoader.registerBlock((Block) blockOres, ItemCustomOre.class);
        itemOreRuby = new ItemStack((Block) blockOres, 1, 0);
        itemOreEmerald = new ItemStack((Block) blockOres, 1, 1);
        itemOreSapphire = new ItemStack((Block) blockOres, 1, 2);
        MinecraftForge.setBlockHarvestLevel((Block) blockOres, (int) 0, (String) "pickaxe", (int) 2);
        MinecraftForge.setBlockHarvestLevel((Block) blockOres, (int) 1, (String) "pickaxe", (int) 2);
        MinecraftForge.setBlockHarvestLevel((Block) blockOres, (int) 2, (String) "pickaxe", (int) 2);
        MinecraftForge.setBlockHarvestLevel((Block) blockOres, (int) 3, (String) "pickaxe", (int) 1);
        MinecraftForge.setBlockHarvestLevel((Block) blockOres, (int) 4, (String) "pickaxe", (int) 0);
        MinecraftForge.setBlockHarvestLevel((Block) blockOres, (int) 5, (String) "pickaxe", (int) 0);
        MinecraftForge.setBlockHarvestLevel((Block) blockOres, (int) 6, (String) "pickaxe", (int) 2);
        MinecraftForge.setBlockHarvestLevel((Block) blockOres, (int) 7, (String) "pickaxe", (int) 2);
        FurnaceRecipes.getInstance().addSmelting(RedPowerWorld.blockOres.id, 3, RedPowerBase.itemIngotSilver);
        FurnaceRecipes.getInstance().addSmelting(RedPowerWorld.blockOres.id, 4, RedPowerBase.itemIngotTin);
        FurnaceRecipes.getInstance().addSmelting(RedPowerWorld.blockOres.id, 5, RedPowerBase.itemIngotCopper);
        MinecraftForge.registerOre((String) "oreSilver", (ItemStack) new ItemStack((Block) blockOres, 1, 3));
        MinecraftForge.registerOre((String) "oreTin", (ItemStack) new ItemStack((Block) blockOres, 1, 4));
        MinecraftForge.registerOre((String) "oreCopper", (ItemStack) new ItemStack((Block) blockOres, 1, 5));
        itemHandsawRuby = new ItemHandsaw(Config.getItemID("items.world.handsawRuby.id"), 1);
        itemHandsawEmerald = new ItemHandsaw(Config.getItemID("items.world.handsawEmerald.id"), 1);
        itemHandsawSapphire = new ItemHandsaw(Config.getItemID("items.world.handsawSapphire.id"), 1);
        itemHandsawRuby.a("handsawRuby").d(19);
        itemHandsawEmerald.a("handsawEmerald").d(20);
        itemHandsawSapphire.a("handsawSapphire").d(21);
        itemHandsawRuby.setMaxDurability(640);
        itemHandsawEmerald.setMaxDurability(640);
        itemHandsawSapphire.setMaxDurability(640);
        ModLoader.addRecipe((ItemStack) new ItemStack(itemHandsawRuby, 1), (Object[]) new Object[]{"WWW", " II", " GG", Character.valueOf('I'), Item.IRON_INGOT, Character.valueOf('G'), RedPowerBase.itemRuby, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack(itemHandsawEmerald, 1), (Object[]) new Object[]{"WWW", " II", " GG", Character.valueOf('I'), Item.IRON_INGOT, Character.valueOf('G'), RedPowerBase.itemEmerald, Character.valueOf('W'), Item.STICK});
        ModLoader.addRecipe((ItemStack) new ItemStack(itemHandsawSapphire, 1), (Object[]) new Object[]{"WWW", " II", " GG", Character.valueOf('I'), Item.IRON_INGOT, Character.valueOf('G'), RedPowerBase.itemSapphire, Character.valueOf('W'), Item.STICK});
        CoverLib.addSaw(itemHandsawRuby, 1);
        CoverLib.addSaw(itemHandsawEmerald, 1);
        CoverLib.addSaw(itemHandsawSapphire, 1);
        itemWoolCard = new ItemWoolCard(Config.getItemID("items.world.woolcard.id"));
        itemWoolCard.a("woolcard").d(28);
        itemWoolCard.setMaxDurability(63);
        CraftLib.addDamageOnCraft(itemWoolCard);
        ModLoader.addRecipe((ItemStack) new ItemStack(itemWoolCard, 1), (Object[]) new Object[]{"W", "P", "S", Character.valueOf('W'), RedPowerBase.itemFineIron, Character.valueOf('P'), Block.WOOD, Character.valueOf('S'), Item.STICK});
        ModLoader.addShapelessRecipe((ItemStack) new ItemStack(Item.STRING, 4), (Object[]) new Object[]{new ItemStack(itemWoolCard, 1, -1), new ItemStack(Block.WOOL, 1, -1)});
        blockLeaves = new BlockCustomLeaves(Config.getBlockID("blocks.world.leaves.id"));
        blockLeaves.a("rpleaves");
        ModLoader.registerBlock((Block) blockLeaves);
        blockLogs = new BlockCustomLog(Config.getBlockID("blocks.world.log.id"));
        blockLogs.a("rplog");
        ModLoader.registerBlock((Block) blockLogs);
        MinecraftForge.setBlockHarvestLevel((Block) blockLogs, (String) "axe", (int) 0);
        MinecraftForge.registerOre((String) "woodRubber", (ItemStack) new ItemStack((Block) blockLogs));
        ModLoader.addRecipe((ItemStack) new ItemStack(Item.STICK, 8), (Object[]) new Object[]{"W", Character.valueOf('W'), blockLogs});
        FurnaceRecipes.getInstance().registerRecipe(RedPowerWorld.blockLogs.id, new ItemStack(Item.COAL, 1, 1));
        CoverLib.addMaterial(53, 0, blockLogs, 0, "rplog", "Rubberwood");
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockStone, 4, 2), (Object[]) new Object[]{"SS", "SS", Character.valueOf('S'), new ItemStack((Block) blockStone, 1, 0)});
        FurnaceRecipes.getInstance().addSmelting(RedPowerWorld.blockStone.id, 3, new ItemStack((Block) blockStone, 1, 1));
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockStone, 4, 4), (Object[]) new Object[]{"SS", "SS", Character.valueOf('S'), new ItemStack((Block) blockStone, 1, 1)});
        blockStorage = new BlockStorage(Config.getBlockID("blocks.world.storage.id"));
        ModLoader.registerBlock((Block) blockStorage, ItemStorage.class);
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockStorage, 1, 0), (Object[]) new Object[]{"GGG", "GGG", "GGG", Character.valueOf('G'), RedPowerBase.itemRuby});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockStorage, 1, 1), (Object[]) new Object[]{"GGG", "GGG", "GGG", Character.valueOf('G'), RedPowerBase.itemEmerald});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockStorage, 1, 2), (Object[]) new Object[]{"GGG", "GGG", "GGG", Character.valueOf('G'), RedPowerBase.itemSapphire});
        ModLoader.addRecipe((ItemStack) CoreLib.copyStack(RedPowerBase.itemRuby, 9), (Object[]) new Object[]{"G", Character.valueOf('G'), new ItemStack((Block) blockStorage, 1, 0)});
        ModLoader.addRecipe((ItemStack) CoreLib.copyStack(RedPowerBase.itemEmerald, 9), (Object[]) new Object[]{"G", Character.valueOf('G'), new ItemStack((Block) blockStorage, 1, 1)});
        ModLoader.addRecipe((ItemStack) CoreLib.copyStack(RedPowerBase.itemSapphire, 9), (Object[]) new Object[]{"G", Character.valueOf('G'), new ItemStack((Block) blockStorage, 1, 2)});
        MinecraftForge.setBlockHarvestLevel((Block) blockStorage, (int) 0, (String) "pickaxe", (int) 2);
        MinecraftForge.setBlockHarvestLevel((Block) blockStorage, (int) 1, (String) "pickaxe", (int) 2);
        MinecraftForge.setBlockHarvestLevel((Block) blockStorage, (int) 2, (String) "pickaxe", (int) 2);
        CoverLib.addMaterial(54, 2, blockStorage, 0, "rubyBlock", "Ruby Block");
        CoverLib.addMaterial(55, 2, blockStorage, 1, "emeraldBlock", "Emerald Block");
        CoverLib.addMaterial(56, 2, blockStorage, 2, "sapphireBlock", "Sapphire Block");
        itemBrushDry = new ItemTextured(Config.getItemID("items.world.paintbrush.dry.id"), 22, "/eloraam/base/items1.png");
        itemBrushDry.a("paintbrush.dry");
        ModLoader.addRecipe((ItemStack) new ItemStack(itemBrushDry), (Object[]) new Object[]{"W ", " S", Character.valueOf('S'), Item.STICK, Character.valueOf('W'), Block.WOOL});
        itemPaintCanEmpty = new ItemTextured(Config.getItemID("items.world.paintcan.empty.id"), 23, "/eloraam/base/items1.png");
        itemPaintCanEmpty.a("paintcan.empty");
        ModLoader.addRecipe((ItemStack) new ItemStack(itemPaintCanEmpty), (Object[]) new Object[]{"T T", "T T", "TTT", Character.valueOf('T'), RedPowerBase.itemTinplate});
        for (n = 0; n < 16; ++n) {
            RedPowerWorld.itemPaintCanPaint[n] = new ItemPaintCan(Config.getItemID("items.world.paintcan." + CoreLib.rawColorNames[n] + ".id"), n);
            itemPaintCanPaint[n].a("paintcan." + CoreLib.rawColorNames[n]);
            Config.addName("item.paintcan." + CoreLib.rawColorNames[n] + ".name", CoreLib.enColorNames[n] + " Paint");
            ModLoader.addShapelessRecipe((ItemStack) new ItemStack(itemPaintCanPaint[n]), (Object[]) new Object[]{itemPaintCanEmpty, new ItemStack(Item.INK_SACK, 1, 15 - n), new ItemStack((Item) itemSeeds, 1, 0), new ItemStack((Item) itemSeeds, 1, 0)});
            CraftLib.addDamageContainer(itemPaintCanPaint[n], itemPaintCanEmpty);
            RedPowerWorld.itemBrushPaint[n] = new ItemPaintBrush(Config.getItemID("items.world.paintbrush." + CoreLib.rawColorNames[n] + ".id"), n);
            itemBrushPaint[n].a("paintbrush." + CoreLib.rawColorNames[n]);
            Config.addName("item.paintbrush." + CoreLib.rawColorNames[n] + ".name", CoreLib.enColorNames[n] + " Paint Brush");
            ModLoader.addShapelessRecipe((ItemStack) new ItemStack(itemBrushPaint[n]), (Object[]) new Object[]{new ItemStack(itemPaintCanPaint[n], 1, -1), itemBrushDry});
        }
        if (Config.getInt("settings.world.tweaks.spreadmoss") > 0) {
            n = Block.MOSSY_COBBLESTONE.id;
            Block.byId[n] = null;
            new BlockCobbleMossifier(n);
            n = Block.SMOOTH_BRICK.id;
            Block.byId[n] = null;
            new BlockBrickMossifier(n);
        }
        if (Config.getInt("settings.world.tweaks.craftcircle") > 0) {
            ModLoader.addRecipe((ItemStack) new ItemStack(Block.SMOOTH_BRICK, 4, 3), (Object[]) new Object[]{"BB", "BB", Character.valueOf('B'), new ItemStack(Block.SMOOTH_BRICK, 1, 0)});
        }
        enchantDisjunction = new EnchantmentDisjunction(Config.getInt("enchant.disjunction.id"), 10);
        RedPowerWorld.initOreDictionary();
        Config.saveConfig();
    }

    public static void GenerateSurface(World world, Random random, int n, int n2) {
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        Random random2 = new Random(Integer.valueOf(n).hashCode() * 31 + Integer.valueOf(n2).hashCode());
        if (Config.getInt("settings.world.generate.ruby") > 0) {
            for (n6 = 0; n6 < 2; ++n6) {
                n3 = n + random2.nextInt(16);
                n7 = random2.nextInt(48);
                n5 = n2 + random2.nextInt(16);
                new WorldGenCustomOre(RedPowerWorld.blockOres.id, 0, 7).a(world, random2, n3, n7, n5);
            }
        }
        if (Config.getInt("settings.world.generate.emerald") > 0) {
            for (n6 = 0; n6 < 2; ++n6) {
                n3 = n + random2.nextInt(16);
                n7 = random2.nextInt(48);
                n5 = n2 + random2.nextInt(16);
                new WorldGenCustomOre(RedPowerWorld.blockOres.id, 1, 7).a(world, random2, n3, n7, n5);
            }
        }
        if (Config.getInt("settings.world.generate.sapphire") > 0) {
            for (n6 = 0; n6 < 2; ++n6) {
                n3 = n + random2.nextInt(16);
                n7 = random2.nextInt(48);
                n5 = n2 + random2.nextInt(16);
                new WorldGenCustomOre(RedPowerWorld.blockOres.id, 2, 7).a(world, random2, n3, n7, n5);
            }
        }
        if (Config.getInt("settings.world.generate.silver") > 0) {
            for (n6 = 0; n6 < 4; ++n6) {
                n3 = n + random2.nextInt(16);
                n7 = random2.nextInt(32);
                n5 = n2 + random2.nextInt(16);
                new WorldGenCustomOre(RedPowerWorld.blockOres.id, 3, 8).a(world, random2, n3, n7, n5);
            }
        }
        if (Config.getInt("settings.world.generate.tin") > 0) {
            for (n6 = 0; n6 < 10; ++n6) {
                n3 = n + random2.nextInt(16);
                n7 = random2.nextInt(48);
                n5 = n2 + random2.nextInt(16);
                new WorldGenCustomOre(RedPowerWorld.blockOres.id, 4, 8).a(world, random2, n3, n7, n5);
            }
        }
        if (Config.getInt("settings.world.generate.copper") > 0) {
            for (n6 = 0; n6 < 20; ++n6) {
                n3 = n + random2.nextInt(16);
                n7 = random2.nextInt(64);
                n5 = n2 + random2.nextInt(16);
                new WorldGenCustomOre(RedPowerWorld.blockOres.id, 5, 8).a(world, random2, n3, n7, n5);
            }
        }
        if (Config.getInt("settings.world.generate.tungsten") > 0) {
            for (n6 = 0; n6 < 1; ++n6) {
                n3 = n + random2.nextInt(16);
                n7 = random2.nextInt(16);
                n5 = n2 + random2.nextInt(16);
                new WorldGenCustomOre(RedPowerWorld.blockOres.id, 6, 4).a(world, random2, n3, n7, n5);
            }
        }
        if (Config.getInt("settings.world.generate.nikolite") > 0) {
            for (n6 = 0; n6 < 4; ++n6) {
                n3 = n + random2.nextInt(16);
                n7 = random2.nextInt(16);
                n5 = n2 + random2.nextInt(16);
                new WorldGenCustomOre(RedPowerWorld.blockOres.id, 7, 10).a(world, random2, n3, n7, n5);
            }
        }
        if (Config.getInt("settings.world.generate.marble") > 0) {
            for (n6 = 0; n6 < 4; ++n6) {
                n3 = n + random2.nextInt(16);
                n7 = 32 + random2.nextInt(32);
                n5 = n2 + random2.nextInt(16);
                new WorldGenMarble(RedPowerWorld.blockStone.id, 0, random2.nextInt(4096)).a(world, random2, n3, n7, n5);
            }
        }
        if (Config.getInt("settings.world.generate.volcano") > 0) {
            n6 = Math.max(1, random2.nextInt(10) - 6);
            n6 *= n6;
            for (n3 = 0; n3 < n6; ++n3) {
                n7 = n + random2.nextInt(16);
                n5 = random2.nextInt(32);
                n4 = n2 + random2.nextInt(16);
                new WorldGenVolcano(RedPowerWorld.blockStone.id, 1, random2.nextInt(65536)).a(world, random2, n7, n5, n4);
            }
        }
        BiomeBase biomeBase = world.getWorldChunkManager().getBiome(n + 16, n2 + 16);
        if (Config.getInt("settings.world.generate.indigo") > 0) {
            n3 = 0;
            if (biomeBase == BiomeBase.JUNGLE) {
                n3 = 1;
            } else if (biomeBase == BiomeBase.JUNGLE_HILLS) {
                n3 = 1;
            } else if (biomeBase == BiomeBase.FOREST) {
                n3 = 1;
            } else if (biomeBase == BiomeBase.PLAINS) {
                n3 = 4;
            }
            for (n7 = 0; n7 < n3; ++n7) {
                n5 = n + random2.nextInt(16) + 8;
                n4 = random2.nextInt(128);
                int n8 = n2 + random2.nextInt(16) + 8;
                new WorldGenFlowers(RedPowerWorld.blockPlants.id).a(world, random2, n5, n4, n8);
            }
        }
        if (Config.getInt("settings.world.generate.rubbertree") > 0 && (biomeBase == BiomeBase.JUNGLE || biomeBase == BiomeBase.JUNGLE_HILLS)) {
            for (n3 = 0; n3 < 6; ++n3) {
                n7 = n + random2.nextInt(16) + 8;
                n5 = n2 + random2.nextInt(16) + 8;
                n4 = world.getHighestBlockYAt(n7, n5);
                new WorldGenRubberTree().a(world, world.random, n7, n4, n5);
            }
        }
    }

    static {
        itemBrushPaint = new Item[16];
        itemPaintCanPaint = new Item[16];
    }

}

