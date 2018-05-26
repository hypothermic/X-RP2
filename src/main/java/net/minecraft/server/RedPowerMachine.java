/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  forge.IOreHandler
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
import eloraam.core.AchieveLib;
import eloraam.core.Config;
import eloraam.core.IMicroPlacement;
import eloraam.core.ItemTextured;
import eloraam.machine.BlockFrame;
import eloraam.machine.BlockMachine;
import eloraam.machine.BlockMachinePanel;
import eloraam.machine.FakePlayer;
import eloraam.machine.ItemBattery;
import eloraam.machine.ItemMachine;
import eloraam.machine.ItemMachinePanel;
import eloraam.machine.ItemSonicDriver;
import eloraam.machine.ItemVoltmeter;
import eloraam.machine.MicroPlacementTube;
import eloraam.machine.TileAccel;
import eloraam.machine.TileAssemble;
import eloraam.machine.TileBatteryBox;
import eloraam.machine.TileBlueAlloyFurnace;
import eloraam.machine.TileBlueFurnace;
import eloraam.machine.TileBreaker;
import eloraam.machine.TileBufferChest;
import eloraam.machine.TileDeploy;
import eloraam.machine.TileEject;
import eloraam.machine.TileFilter;
import eloraam.machine.TileFrame;
import eloraam.machine.TileFrameMoving;
import eloraam.machine.TileGrate;
import eloraam.machine.TileIgniter;
import eloraam.machine.TileItemDetect;
import eloraam.machine.TileMagTube;
import eloraam.machine.TileMotor;
import eloraam.machine.TilePipe;
import eloraam.machine.TilePump;
import eloraam.machine.TileRedstoneTube;
import eloraam.machine.TileRegulator;
import eloraam.machine.TileRelay;
import eloraam.machine.TileRestrictTube;
import eloraam.machine.TileRetriever;
import eloraam.machine.TileSolarPanel;
import eloraam.machine.TileSorter;
import eloraam.machine.TileThermopile;
import eloraam.machine.TileTranspose;
import eloraam.machine.TileTube;
import forge.IOreHandler;
import forge.MinecraftForge;
import java.io.PrintStream;
import java.util.HashSet;
import net.minecraft.server.Achievement;
import net.minecraft.server.AchievementList;
import net.minecraft.server.Block;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.ModLoader;
import net.minecraft.server.RedPowerBase;

public class RedPowerMachine {
    private static boolean initialized = false;
    public static BlockMachine blockMachine;
    public static BlockFrame blockFrame;
    public static BlockMachinePanel blockMachinePanel;
    public static ItemVoltmeter itemVoltmeter;
    public static ItemSonicDriver itemSonicDriver;
    public static Item itemBatteryEmpty;
    public static Item itemBatteryPowered;
    public static final String textureFile = "/eloraam/machine/machine1.png";
    public static boolean FrameAlwaysCrate;
    public static int FrameLinkSize;
    public static final HashSet<Integer> breakerBlacklist;
    public static final HashSet<Integer> deployerBlacklist;
    public static int tubeBufferLength;
    public static String tubeBufferFull;

    public static void initOreDictionary() {
        MinecraftForge.registerOreHandler((IOreHandler)new IOreHandler(){

            public void registerOre(String string, ItemStack itemStack) {
                if (string.equals("ingotCopper")) {
                    ModLoader.addRecipe((ItemStack)new ItemStack((Item)RedPowerMachine.itemVoltmeter), (Object[])new Object[]{"WWW", "WNW", "CCC", Character.valueOf('W'), Block.WOOD, Character.valueOf('N'), RedPowerBase.itemNikolite, Character.valueOf('C'), itemStack});
                    for (ItemStack itemStack2 : MinecraftForge.getOreClass((String)"ingotTin")) {
                        ModLoader.addRecipe((ItemStack)new ItemStack(RedPowerMachine.itemBatteryEmpty, 1), (Object[])new Object[]{"NCN", "NTN", "NCN", Character.valueOf('N'), RedPowerBase.itemNikolite, Character.valueOf('C'), itemStack, Character.valueOf('T'), itemStack2});
                    }
                    ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerMachine.blockMachine, 1, 11), (Object[])new Object[]{"CIC", "WBW", "CIC", Character.valueOf('I'), Item.IRON_INGOT, Character.valueOf('B'), RedPowerBase.itemIngotBlue, Character.valueOf('W'), RedPowerBase.itemWaferBlue, Character.valueOf('C'), itemStack});
                }
                if (string.equals("ingotTin")) {
                    for (ItemStack itemStack2 : MinecraftForge.getOreClass((String)"ingotCopper")) {
                        ModLoader.addRecipe((ItemStack)new ItemStack(RedPowerMachine.itemBatteryEmpty, 1), (Object[])new Object[]{"NCN", "NTN", "NCN", Character.valueOf('N'), RedPowerBase.itemNikolite, Character.valueOf('C'), itemStack2, Character.valueOf('T'), itemStack});
                    }
                }
            }
        });
    }

    public static void initAchievements() {
        AchieveLib.registerAchievement(117283, "rpTranspose", -2, 2, new ItemStack((Block)blockMachine, 1, 2), (Object)AchievementList.k);
        AchieveLib.registerAchievement(117284, "rpBreaker", -2, 4, new ItemStack((Block)blockMachine, 1, 1), (Object)AchievementList.k);
        AchieveLib.registerAchievement(117285, "rpDeploy", -2, 6, new ItemStack((Block)blockMachine, 1, 0), (Object)AchievementList.k);
        AchieveLib.addCraftingAchievement(new ItemStack((Block)blockMachine, 1, 2), "rpTranspose");
        AchieveLib.addCraftingAchievement(new ItemStack((Block)blockMachine, 1, 1), "rpBreaker");
        AchieveLib.addCraftingAchievement(new ItemStack((Block)blockMachine, 1, 0), "rpDeploy");
        AchieveLib.registerAchievement(117286, "rpFrames", 4, 4, new ItemStack((Block)blockMachine, 1, 7), "rpIngotBlue");
        AchieveLib.registerAchievement(117287, "rpPump", 4, 5, new ItemStack((Block)blockMachinePanel, 1, 1), "rpIngotBlue");
        AchieveLib.addCraftingAchievement(new ItemStack((Block)blockMachine, 1, 7), "rpFrames");
        AchieveLib.addCraftingAchievement(new ItemStack((Block)blockMachinePanel, 1, 1), "rpPump");
    }

    public static void initialize() {
        String[] arrstring;
        int n;
        int n2;
        if (initialized) {
            return;
        }
        initialized = true;
        FrameAlwaysCrate = Config.getInt("settings.machine.frame.alwayscrate") > 0;
        FrameLinkSize = Config.getInt("settings.machine.frame.linksize");
        blockFrame = new BlockFrame(Config.getBlockID("blocks.machine.frame.id"));
        blockFrame.a("rpframe");
        ModLoader.registerBlock((Block)blockFrame);
        ModLoader.registerTileEntity(TileFrame.class, (String)"RPFrame");
        blockFrame.addTileEntityMapping(0, TileFrame.class);
        blockFrame.addTileEntityMapping(1, TileFrameMoving.class);
        MicroPlacementTube microPlacementTube = new MicroPlacementTube();
        RedPowerBase.blockMicro.registerPlacement(7, microPlacementTube);
        RedPowerBase.blockMicro.registerPlacement(8, microPlacementTube);
        RedPowerBase.blockMicro.registerPlacement(9, microPlacementTube);
        RedPowerBase.blockMicro.registerPlacement(10, microPlacementTube);
        RedPowerBase.blockMicro.registerPlacement(11, microPlacementTube);
        blockMachine = new BlockMachine(Config.getBlockID("blocks.machine.machine.id"));
        blockMachine.a("rpmachine");
        ModLoader.registerBlock((Block)blockMachine, ItemMachine.class);
        ModLoader.registerTileEntity(TileDeploy.class, (String)"RPDeploy");
        ModLoader.registerTileEntity(TileBreaker.class, (String)"RPBreaker");
        ModLoader.registerTileEntity(TileTranspose.class, (String)"RPTranspose");
        ModLoader.registerTileEntity(TileFilter.class, (String)"RPFilter");
        ModLoader.registerTileEntity(TileItemDetect.class, (String)"RPItemDet");
        ModLoader.registerTileEntity(TileSorter.class, (String)"RPSorter");
        ModLoader.registerTileEntity(TileBatteryBox.class, (String)"RPBatBox");
        ModLoader.registerTileEntity(TileMotor.class, (String)"RPMotor");
        ModLoader.registerTileEntity(TileRetriever.class, (String)"RPRetrieve");
        ModLoader.registerTileEntity(TileRegulator.class, (String)"RPRegulate");
        ModLoader.registerTileEntity(TileThermopile.class, (String)"RPThermo");
        ModLoader.registerTileEntity(TileIgniter.class, (String)"RPIgnite");
        ModLoader.registerTileEntity(TileAssemble.class, (String)"RPAssemble");
        ModLoader.registerTileEntity(TileEject.class, (String)"RPEject");
        ModLoader.registerTileEntity(TileRelay.class, (String)"RPRelay");
        blockMachine.addTileEntityMapping(0, TileDeploy.class);
        blockMachine.addTileEntityMapping(1, TileBreaker.class);
        blockMachine.addTileEntityMapping(2, TileTranspose.class);
        blockMachine.addTileEntityMapping(3, TileFilter.class);
        blockMachine.addTileEntityMapping(4, TileItemDetect.class);
        blockMachine.addTileEntityMapping(5, TileSorter.class);
        blockMachine.addTileEntityMapping(6, TileBatteryBox.class);
        blockMachine.addTileEntityMapping(7, TileMotor.class);
        blockMachine.addTileEntityMapping(8, TileRetriever.class);
        blockMachine.addTileEntityMapping(10, TileRegulator.class);
        blockMachine.addTileEntityMapping(11, TileThermopile.class);
        blockMachine.addTileEntityMapping(12, TileIgniter.class);
        blockMachine.addTileEntityMapping(13, TileAssemble.class);
        blockMachine.addTileEntityMapping(14, TileEject.class);
        blockMachine.addTileEntityMapping(15, TileRelay.class);
        blockMachinePanel = new BlockMachinePanel(Config.getBlockID("blocks.machine.machinePanel.id"));
        ModLoader.registerBlock((Block)blockMachinePanel, ItemMachinePanel.class);
        ModLoader.registerTileEntity(TileSolarPanel.class, (String)"RPSolar");
        ModLoader.registerTileEntity(TileGrate.class, (String)"RPGrate");
        blockMachinePanel.addTileEntityMapping(0, TileSolarPanel.class);
        blockMachinePanel.addTileEntityMapping(1, TilePump.class);
        blockMachinePanel.addTileEntityMapping(2, TileAccel.class);
        blockMachinePanel.addTileEntityMapping(3, TileGrate.class);
        blockMachinePanel.setItemName(0, "rpsolar");
        blockMachinePanel.setItemName(1, "rppump");
        blockMachinePanel.setItemName(2, "rpaccel");
        blockMachinePanel.setItemName(3, "rpgrate");
        ModLoader.registerTileEntity(TileBlueFurnace.class, (String)"RPBFurnace");
        ModLoader.registerTileEntity(TileBufferChest.class, (String)"RPBuffer");
        ModLoader.registerTileEntity(TileBlueAlloyFurnace.class, (String)"RPBAFurnace");
        RedPowerBase.blockAppliance.setItemName(1, "rpbfurnace");
        RedPowerBase.blockAppliance.addTileEntityMapping(1, TileBlueFurnace.class);
        RedPowerBase.blockAppliance.setItemName(2, "rpbuffer");
        RedPowerBase.blockAppliance.addTileEntityMapping(2, TileBufferChest.class);
        RedPowerBase.blockAppliance.setItemName(4, "rpbafurnace");
        RedPowerBase.blockAppliance.addTileEntityMapping(4, TileBlueAlloyFurnace.class);
        RedPowerBase.blockMicro.addTileEntityMapping(7, TilePipe.class);
        RedPowerBase.blockMicro.addTileEntityMapping(8, TileTube.class);
        RedPowerBase.blockMicro.addTileEntityMapping(9, TileRedstoneTube.class);
        RedPowerBase.blockMicro.addTileEntityMapping(10, TileRestrictTube.class);
        RedPowerBase.blockMicro.addTileEntityMapping(11, TileMagTube.class);
        itemVoltmeter = new ItemVoltmeter(Config.getItemID("items.machine.voltmeter.id"));
        itemVoltmeter.a("voltmeter");
        itemBatteryEmpty = new ItemTextured(Config.getItemID("items.machine.battery.empty.id"), 25, "/eloraam/base/items1.png");
        itemBatteryEmpty.a("btbattery");
        itemBatteryPowered = new ItemBattery(Config.getItemID("items.machine.battery.powered.id"));
        itemBatteryPowered.a("btbattery");
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 0), (Object[])new Object[]{"SCS", "SPS", "SRS", Character.valueOf('S'), Block.COBBLESTONE, Character.valueOf('C'), Block.CHEST, Character.valueOf('R'), Item.REDSTONE, Character.valueOf('P'), Block.PISTON});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 1), (Object[])new Object[]{"SAS", "SPS", "SRS", Character.valueOf('S'), Block.COBBLESTONE, Character.valueOf('A'), Item.IRON_PICKAXE, Character.valueOf('R'), Item.REDSTONE, Character.valueOf('P'), Block.PISTON});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 2), (Object[])new Object[]{"SSS", "WPW", "SRS", Character.valueOf('S'), Block.COBBLESTONE, Character.valueOf('R'), Item.REDSTONE, Character.valueOf('P'), Block.PISTON, Character.valueOf('W'), Block.WOOD});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 3), (Object[])new Object[]{"SSS", "GPG", "SRS", Character.valueOf('S'), Block.COBBLESTONE, Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('P'), Block.PISTON, Character.valueOf('G'), Item.GOLD_INGOT});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 4), (Object[])new Object[]{"BTB", "RPR", "WTW", Character.valueOf('B'), RedPowerBase.itemIngotBrass, Character.valueOf('T'), new ItemStack((Block)RedPowerBase.blockMicro, 1, 2048), Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('W'), Block.WOOD, Character.valueOf('P'), Block.WOOD_PLATE});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 5), (Object[])new Object[]{"III", "RFR", "IBI", Character.valueOf('B'), RedPowerBase.itemIngotBlue, Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('F'), new ItemStack((Block)blockMachine, 1, 3), Character.valueOf('I'), Item.IRON_INGOT});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 8), (Object[])new Object[]{"BLB", "EFE", "INI", Character.valueOf('N'), RedPowerBase.itemIngotBlue, Character.valueOf('B'), RedPowerBase.itemIngotBrass, Character.valueOf('E'), Item.ENDER_PEARL, Character.valueOf('L'), Item.LEATHER, Character.valueOf('F'), new ItemStack((Block)blockMachine, 1, 3), Character.valueOf('I'), Item.IRON_INGOT});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockAppliance, 1, 2), (Object[])new Object[]{"BWB", "W W", "BWB", Character.valueOf('B'), Block.IRON_FENCE, Character.valueOf('W'), Block.WOOD});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 10), (Object[])new Object[]{"BCB", "RDR", "WCW", Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('B'), RedPowerBase.itemIngotBrass, Character.valueOf('D'), new ItemStack((Block)blockMachine, 1, 4), Character.valueOf('W'), Block.WOOD, Character.valueOf('C'), new ItemStack((Block)RedPowerBase.blockAppliance, 1, 2)});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 8, 2048), (Object[])new Object[]{"BGB", Character.valueOf('G'), Block.GLASS, Character.valueOf('B'), RedPowerBase.itemIngotBrass});
        ModLoader.addShapelessRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 1, 2304), (Object[])new Object[]{Item.REDSTONE, new ItemStack((Block)RedPowerBase.blockMicro, 1, 2048)});
        ModLoader.addShapelessRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 1, 2560), (Object[])new Object[]{Item.IRON_INGOT, new ItemStack((Block)RedPowerBase.blockMicro, 1, 2048)});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 8, 2816), (Object[])new Object[]{"CCC", "OGO", "CCC", Character.valueOf('G'), Block.GLASS, Character.valueOf('O'), Block.OBSIDIAN, Character.valueOf('C'), RedPowerBase.itemFineCopper});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockAppliance, 1, 1), (Object[])new Object[]{"CCC", "C C", "IBI", Character.valueOf('C'), Block.CLAY, Character.valueOf('B'), RedPowerBase.itemIngotBlue, Character.valueOf('I'), Item.IRON_INGOT});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockAppliance, 1, 4), (Object[])new Object[]{"CCC", "C C", "IBI", Character.valueOf('C'), Block.BRICK, Character.valueOf('B'), RedPowerBase.itemIngotBlue, Character.valueOf('I'), Item.IRON_INGOT});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachinePanel, 1, 0), (Object[])new Object[]{"WWW", "WBW", "WWW", Character.valueOf('W'), RedPowerBase.itemWaferBlue, Character.valueOf('B'), RedPowerBase.itemIngotBlue});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachinePanel, 1, 2), (Object[])new Object[]{"BOB", "O O", "BOB", Character.valueOf('O'), Block.OBSIDIAN, Character.valueOf('B'), RedPowerBase.itemIngotBlue});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 6), (Object[])new Object[]{"BWB", "BIB", "IAI", Character.valueOf('I'), Item.IRON_INGOT, Character.valueOf('W'), Block.WOOD, Character.valueOf('A'), RedPowerBase.itemIngotBlue, Character.valueOf('B'), itemBatteryEmpty});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 12), (Object[])new Object[]{"NFN", "SDS", "SRS", Character.valueOf('N'), Block.NETHERRACK, Character.valueOf('F'), Item.FLINT_AND_STEEL, Character.valueOf('D'), new ItemStack((Block)blockMachine, 1, 0), Character.valueOf('S'), Block.COBBLESTONE, Character.valueOf('R'), Item.REDSTONE});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 13), (Object[])new Object[]{"BIB", "CDC", "IRI", Character.valueOf('I'), Item.IRON_INGOT, Character.valueOf('D'), new ItemStack((Block)blockMachine, 1, 0), Character.valueOf('C'), new ItemStack((Block)RedPowerBase.blockMicro, 1, 768), Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('B'), RedPowerBase.itemIngotBrass});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 14), (Object[])new Object[]{"WBW", "WTW", "SRS", Character.valueOf('R'), Item.REDSTONE, Character.valueOf('T'), new ItemStack((Block)blockMachine, 1, 2), Character.valueOf('W'), Block.WOOD, Character.valueOf('B'), new ItemStack((Block)RedPowerBase.blockAppliance, 1, 2), Character.valueOf('S'), Block.COBBLESTONE});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 15), (Object[])new Object[]{"WBW", "WTW", "SRS", Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('T'), new ItemStack((Block)blockMachine, 1, 2), Character.valueOf('W'), Block.WOOD, Character.valueOf('B'), new ItemStack((Block)RedPowerBase.blockAppliance, 1, 2), Character.valueOf('S'), Block.COBBLESTONE});
        ModLoader.addRecipe((ItemStack)RedPowerBase.itemCopperCoil, (Object[])new Object[]{"FBF", "BIB", "FBF", Character.valueOf('F'), RedPowerBase.itemFineCopper, Character.valueOf('B'), Block.IRON_FENCE, Character.valueOf('I'), Item.IRON_INGOT});
        ModLoader.addRecipe((ItemStack)RedPowerBase.itemMotor, (Object[])new Object[]{"ICI", "ICI", "IBI", Character.valueOf('C'), RedPowerBase.itemCopperCoil, Character.valueOf('B'), RedPowerBase.itemIngotBlue, Character.valueOf('I'), Item.IRON_INGOT});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockFrame, 1), (Object[])new Object[]{"SSS", "SBS", "SSS", Character.valueOf('S'), Item.STICK, Character.valueOf('B'), RedPowerBase.itemIngotBrass});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachine, 1, 7), (Object[])new Object[]{"III", "BMB", "IAI", Character.valueOf('I'), Item.IRON_INGOT, Character.valueOf('A'), RedPowerBase.itemIngotBlue, Character.valueOf('B'), RedPowerBase.itemIngotBrass, Character.valueOf('M'), RedPowerBase.itemMotor});
        RedPowerMachine.initOreDictionary();
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockFrame, 1), (Object[])new Object[]{"SSS", "SBS", "SSS", Character.valueOf('S'), Item.STICK, Character.valueOf('B'), RedPowerBase.itemIngotBrass});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)RedPowerBase.blockMicro, 16, 1792), (Object[])new Object[]{"B B", "BGB", "B B", Character.valueOf('G'), Block.GLASS, Character.valueOf('B'), RedPowerBase.itemIngotBrass});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachinePanel, 1, 3), (Object[])new Object[]{"III", "I I", "IPI", Character.valueOf('P'), new ItemStack((Block)RedPowerBase.blockMicro, 1, 1792), Character.valueOf('I'), Block.IRON_FENCE});
        ModLoader.addRecipe((ItemStack)new ItemStack((Block)blockMachinePanel, 1, 1), (Object[])new Object[]{"III", "PMP", "IAI", Character.valueOf('I'), Item.IRON_INGOT, Character.valueOf('A'), RedPowerBase.itemIngotBlue, Character.valueOf('P'), new ItemStack((Block)RedPowerBase.blockMicro, 1, 1792), Character.valueOf('M'), RedPowerBase.itemMotor});
        itemSonicDriver = new ItemSonicDriver(Config.getItemID("items.machine.sonicDriver.id"));
        itemSonicDriver.a("sonicDriver").d(129);
        ModLoader.addRecipe((ItemStack)new ItemStack((Item)itemSonicDriver, 1, itemSonicDriver.getMaxDurability()), (Object[])new Object[]{"E  ", " R ", "  B", Character.valueOf('R'), RedPowerBase.itemIngotBrass, Character.valueOf('E'), RedPowerBase.itemEmerald, Character.valueOf('B'), itemBatteryEmpty});
        RedPowerMachine.initAchievements();
        FakePlayer.name = Config.getString("settings.machine.fakeplayer.name");
        FakePlayer.doLogin = Config.getInt("settings.machine.fakeplayer.login") > 0;
        tubeBufferLength = Config.getInt("settings.machine.tubebuffer.length");
        tubeBufferFull = Config.getString("settings.machine.tubebuffer.whenfull");
        String string = Config.getString("settings.machine.blockbreaker.blacklist");
        if (string != null && !string.equals("")) {
            for (String string2 : string.split(",")) {
                try {
                    arrstring = string2.split(":");
                    n = Integer.parseInt(arrstring[0]);
                    n2 = arrstring.length == 2 ? (arrstring[1] == "*" ? -1 : Integer.parseInt(arrstring[1])) : -1;
                    System.out.println("[RedPowerMachine] Adding " + n + ":" + n2 + " to blockbreaker blacklist.");
                    breakerBlacklist.add(n2 << 15 | n);
                }
                catch (Exception exception) {
                    System.out.println("[RedPowerMachine] Warning: Could not parse '" + string2 + "' as ID or ID:data.");
                }
            }
        }
        if ((string = Config.getString("settings.machine.deployer.blacklist")) != null && !string.equals("")) {
            for (String string2 : string.split(",")) {
                try {
                    arrstring = string2.split(":");
                    n = Integer.parseInt(arrstring[0]);
                    n2 = arrstring.length == 2 ? (arrstring[1] == "*" ? -1 : Integer.parseInt(arrstring[1])) : -1;
                    System.out.println("[RedPowerMachine] Adding " + n + ":" + n2 + " to deployer blacklist.");
                    deployerBlacklist.add(n2 << 15 | n);
                }
                catch (Exception exception) {
                    System.out.println("[RedPowerMachine] Warning: Could not parse '" + string2 + "' as ID or ID:data.");
                }
            }
        }
        Config.saveConfig();
    }

    public static int blockDamageDropped(Block block, int n) {
        return block.getDropData(n);
    }

    static {
        breakerBlacklist = new HashSet();
        deployerBlacklist = new HashSet();
        tubeBufferLength = -1;
        tubeBufferFull = "drop";
    }

}

