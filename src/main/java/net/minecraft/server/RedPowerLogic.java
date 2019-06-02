/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.Block
 *  net.minecraft.server.FurnaceRecipes
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.ModLoader
 */
package net.minecraft.server;

import eloraam.core.Config;
import eloraam.core.CoreLib;
import eloraam.core.ItemParts;
import eloraam.logic.*;

public class RedPowerLogic {
    private static boolean initialized = false;
    public static BlockLogic blockLogic;
    public static ItemParts itemParts;
    public static ItemStack itemAnode;
    public static ItemStack itemCathode;
    public static ItemStack itemWire;
    public static ItemStack itemWafer;
    public static ItemStack itemPointer;
    public static ItemStack itemPlate;
    public static ItemStack itemWaferRedwire;
    public static ItemStack itemChip;
    public static ItemStack itemTaintedChip;
    public static ItemStack itemWaferBundle;
    public static boolean EnableSounds;
    public static int minInterval;

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        EnableSounds = Config.getInt("settings.logic.enableSounds") > 0;
        minInterval = Config.getInt("settings.logic.minInterval");
        ModLoader.registerTileEntity(TileLogicSimple.class, (String) "RPLgSmp");
        ModLoader.registerTileEntity(TileLogicArray.class, (String) "RPLgAr");
        ModLoader.registerTileEntity(TileLogicStorage.class, (String) "RPLgStor");
        ModLoader.registerTileEntity(TileLogicAdv.class, (String) "RPLgAdv");
        ModLoader.registerTileEntity(TileNor.class, (String) "IRNor");
        ModLoader.registerTileEntity(TileOr.class, (String) "IROr");
        ModLoader.registerTileEntity(TileNand.class, (String) "IRNand");
        ModLoader.registerTileEntity(TileAnd.class, (String) "IRAnd");
        ModLoader.registerTileEntity(TileXnor.class, (String) "IRXnor");
        ModLoader.registerTileEntity(TileXor.class, (String) "IRXor");
        ModLoader.registerTileEntity(TileToggle.class, (String) "IRToggle");
        ModLoader.registerTileEntity(TilePulse.class, (String) "IRPulse");
        ModLoader.registerTileEntity(TileLatch.class, (String) "IRLatch");
        ModLoader.registerTileEntity(TileNot.class, (String) "IRNot");
        ModLoader.registerTileEntity(TileBuffer.class, (String) "IRBuf");
        ModLoader.registerTileEntity(TileMux.class, (String) "IRMux");
        ModLoader.registerTileEntity(TileCounter.class, (String) "IRCounter");
        ModLoader.registerTileEntity(TileRepeater.class, (String) "IRRepeater");
        itemParts = new ItemParts(Config.getItemID("items.logic.parts.id"), "/eloraam/base/items1.png");
        itemParts.addItem(0, 0, "item.irwafer");
        itemParts.addItem(1, 1, "item.irwire");
        itemParts.addItem(2, 2, "item.iranode");
        itemParts.addItem(3, 3, "item.ircathode");
        itemParts.addItem(4, 4, "item.irpointer");
        itemParts.addItem(5, 5, "item.irredwire");
        itemParts.addItem(6, 6, "item.irplate");
        itemParts.addItem(7, 7, "item.irchip");
        itemParts.addItem(8, 8, "item.irtchip");
        itemParts.addItem(9, 9, "item.irbundle");
        itemWafer = new ItemStack((Item) itemParts, 1, 0);
        itemWire = new ItemStack((Item) itemParts, 1, 1);
        itemAnode = new ItemStack((Item) itemParts, 1, 2);
        itemCathode = new ItemStack((Item) itemParts, 1, 3);
        itemPointer = new ItemStack((Item) itemParts, 1, 4);
        itemWaferRedwire = new ItemStack((Item) itemParts, 1, 5);
        itemPlate = new ItemStack((Item) itemParts, 1, 6);
        itemChip = new ItemStack((Item) itemParts, 1, 7);
        itemTaintedChip = new ItemStack((Item) itemParts, 1, 8);
        itemWaferBundle = new ItemStack((Item) itemParts, 1, 9);
        FurnaceRecipes.getInstance().registerRecipe(Block.STONE.id, new ItemStack((Item) itemParts, 2, 0));
        ModLoader.addRecipe((ItemStack) itemWire, (Object[]) new Object[]{"R", "B", Character.valueOf('B'), itemWafer, Character.valueOf('R'), Item.REDSTONE});
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemParts, 3, 2), (Object[]) new Object[]{" R ", "RRR", "BBB", Character.valueOf('B'), itemWafer, Character.valueOf('R'), Item.REDSTONE});
        ModLoader.addRecipe((ItemStack) itemCathode, (Object[]) new Object[]{"T", "B", Character.valueOf('B'), itemWafer, Character.valueOf('T'), Block.REDSTONE_TORCH_ON});
        ModLoader.addRecipe((ItemStack) itemPointer, (Object[]) new Object[]{"S", "T", "B", Character.valueOf('B'), itemWafer, Character.valueOf('S'), Block.STONE, Character.valueOf('T'), Block.REDSTONE_TORCH_ON});
        ModLoader.addRecipe((ItemStack) itemWaferRedwire, (Object[]) new Object[]{"W", "B", Character.valueOf('B'), itemWafer, Character.valueOf('W'), new ItemStack((Block) RedPowerBase.blockMicro, 1, 256)});
        ModLoader.addRecipe((ItemStack) itemPlate, (Object[]) new Object[]{" B ", "SRS", "BCB", Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('R'), RedPowerBase.itemIngotRed, Character.valueOf('S'), Item.STICK});
        ModLoader.addRecipe((ItemStack) CoreLib.copyStack(itemChip, 3), (Object[]) new Object[]{" R ", "BBB", Character.valueOf('B'), itemWafer, Character.valueOf('R'), RedPowerBase.itemWaferRed});
        ModLoader.addShapelessRecipe((ItemStack) CoreLib.copyStack(itemTaintedChip, 1), (Object[]) new Object[]{itemChip, Item.GLOWSTONE_DUST});
        ModLoader.addRecipe((ItemStack) itemWaferBundle, (Object[]) new Object[]{"W", "B", Character.valueOf('B'), itemWafer, Character.valueOf('W'), new ItemStack((Block) RedPowerBase.blockMicro, 1, 768)});
        blockLogic = new BlockLogic(Config.getBlockID("blocks.logic.logic.id"));
        ModLoader.registerBlock((Block) blockLogic, ItemLogic.class);
        blockLogic.addTileEntityMapping(0, TileLogicPointer.class);
        blockLogic.addTileEntityMapping(1, TileLogicSimple.class);
        blockLogic.addTileEntityMapping(2, TileLogicArray.class);
        blockLogic.addTileEntityMapping(3, TileLogicStorage.class);
        blockLogic.addTileEntityMapping(4, TileLogicAdv.class);
        blockLogic.setItemName(0, "irtimer");
        blockLogic.setItemName(1, "irseq");
        blockLogic.setItemName(2, "irstate");
        blockLogic.setItemName(256, "irlatch");
        blockLogic.setItemName(257, "irnor");
        blockLogic.setItemName(258, "iror");
        blockLogic.setItemName(259, "irnand");
        blockLogic.setItemName(260, "irand");
        blockLogic.setItemName(261, "irxnor");
        blockLogic.setItemName(262, "irxor");
        blockLogic.setItemName(263, "irpulse");
        blockLogic.setItemName(264, "irtoggle");
        blockLogic.setItemName(265, "irnot");
        blockLogic.setItemName(266, "irbuf");
        blockLogic.setItemName(267, "irmux");
        blockLogic.setItemName(268, "irrepeater");
        blockLogic.setItemName(269, "irsync");
        blockLogic.setItemName(270, "irrand");
        blockLogic.setItemName(271, "irdlatch");
        blockLogic.setItemName(272, "rplightsensor");
        blockLogic.setItemName(512, "rpanc");
        blockLogic.setItemName(513, "rpainv");
        blockLogic.setItemName(514, "rpaninv");
        blockLogic.setItemName(768, "ircounter");
        blockLogic.setItemName(1024, "irbusxcvr");
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 0), (Object[]) new Object[]{"BWB", "WPW", "ACA", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode, Character.valueOf('P'), itemPointer});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 1), (Object[]) new Object[]{"BCB", "CPC", "BCB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode, Character.valueOf('P'), itemPointer});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 2), (Object[]) new Object[]{"BAC", "WSP", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode, Character.valueOf('P'), itemPointer, Character.valueOf('S'), itemChip});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 256), (Object[]) new Object[]{"WWA", "CBC", "AWW", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 257), (Object[]) new Object[]{"BAB", "WCW", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 258), (Object[]) new Object[]{"BCB", "WCW", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 259), (Object[]) new Object[]{"AAA", "CCC", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 260), (Object[]) new Object[]{"ACA", "CCC", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 261), (Object[]) new Object[]{"ACA", "CAC", "WCW", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 262), (Object[]) new Object[]{"AWA", "CAC", "WCW", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 263), (Object[]) new Object[]{"ACA", "CAC", "WWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 264), (Object[]) new Object[]{"BCB", "WLW", "BCB", Character.valueOf('L'), Block.LEVER, Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 265), (Object[]) new Object[]{"BAB", "ACA", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 266), (Object[]) new Object[]{"ACA", "WCW", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 267), (Object[]) new Object[]{"ACA", "CBC", "ACW", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 268), (Object[]) new Object[]{"BCW", "BAW", "BWC", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('A'), itemAnode, Character.valueOf('C'), itemCathode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 269), (Object[]) new Object[]{"WCW", "SAS", "WWW", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('A'), itemAnode, Character.valueOf('C'), itemCathode, Character.valueOf('S'), itemChip});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 270), (Object[]) new Object[]{"BSB", "WWW", "SWS", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('S'), itemTaintedChip});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 271), (Object[]) new Object[]{"ACW", "CCC", "CWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 272), (Object[]) new Object[]{"BWB", "BSB", "BBB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('S'), RedPowerBase.itemWaferBlue});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 768), (Object[]) new Object[]{"BWB", "CPC", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('P'), itemPointer});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 512), (Object[]) new Object[]{"BRB", "RRR", "BRB", Character.valueOf('B'), itemWafer, Character.valueOf('R'), itemWaferRedwire});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 513), (Object[]) new Object[]{"BRB", "RPR", "BRB", Character.valueOf('B'), itemWafer, Character.valueOf('R'), itemWaferRedwire, Character.valueOf('P'), itemPlate});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 514), (Object[]) new Object[]{"BRB", "RPR", "BRC", Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('R'), itemWaferRedwire, Character.valueOf('P'), itemPlate});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockLogic, 1, 1024), (Object[]) new Object[]{"CCC", "WBW", "CCC", Character.valueOf('B'), itemWafer, Character.valueOf('W'), RedPowerBase.itemWaferRed, Character.valueOf('C'), itemWaferBundle});
        Config.saveConfig();
    }
}

