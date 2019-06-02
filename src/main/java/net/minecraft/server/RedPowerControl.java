/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  forge.MinecraftForge
 *  net.minecraft.server.Block
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.Material
 *  net.minecraft.server.ModLoader
 */
package net.minecraft.server;

import eloraam.control.*;
import eloraam.core.*;
import forge.MinecraftForge;

public class RedPowerControl {
    private static boolean initialized = false;
    public static BlockExtended blockBackplane;
    public static BlockExtended blockPeripheral;
    public static BlockExtended blockFlatPeripheral;
    public static ItemDisk itemDisk;

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        blockBackplane = new BlockMultipart(Config.getBlockID("blocks.control.backplane.id"), CoreLib.materialRedpower);
        ModLoader.registerBlock((Block) blockBackplane, ItemBackplane.class);
        blockBackplane.c(1.0f);
        blockBackplane.setItemName(0, "rpbackplane");
        blockBackplane.setItemName(1, "rpram");
        blockPeripheral = new BlockPeripheral(Config.getBlockID("blocks.control.peripheral.id"));
        ModLoader.registerBlock((Block) blockPeripheral, ItemExtended.class);
        blockPeripheral.c(1.0f);
        blockPeripheral.setItemName(0, "rpdisplay");
        blockPeripheral.setItemName(1, "rpcpu");
        blockPeripheral.setItemName(2, "rpdiskdrive");
        blockFlatPeripheral = new BlockMultipart(Config.getBlockID("blocks.control.peripheralFlat.id"), Material.STONE);
        ModLoader.registerBlock((Block) blockFlatPeripheral, ItemExtended.class);
        blockFlatPeripheral.c(1.0f);
        blockFlatPeripheral.setItemName(0, "rpioexp");
        ModLoader.registerTileEntity(TileBackplane.class, (String) "RPConBP");
        blockBackplane.addTileEntityMapping(0, TileBackplane.class);
        ModLoader.registerTileEntity(TileRAM.class, (String) "RPConRAM");
        blockBackplane.addTileEntityMapping(1, TileRAM.class);
        ModLoader.registerTileEntity(TileDisplay.class, (String) "RPConDisp");
        blockPeripheral.addTileEntityMapping(0, TileDisplay.class);
        ModLoader.registerTileEntity(TileDiskDrive.class, (String) "RPConDDrv");
        blockPeripheral.addTileEntityMapping(2, TileDiskDrive.class);
        ModLoader.registerTileEntity(TileCPU.class, (String) "RPConCPU");
        blockPeripheral.addTileEntityMapping(1, TileCPU.class);
        ModLoader.registerTileEntity(TileIOExpander.class, (String) "RPConIOX");
        blockFlatPeripheral.addTileEntityMapping(0, TileIOExpander.class);
        ModLoader.registerTileEntity(TileRibbon.class, (String) "RPConRibbon");
        RedPowerBase.blockMicro.addTileEntityMapping(12, TileRibbon.class);
        MicroPlacementRibbon microPlacementRibbon = new MicroPlacementRibbon();
        RedPowerBase.blockMicro.registerPlacement(12, microPlacementRibbon);
        itemDisk = new ItemDisk(Config.getItemID("items.control.disk.id"));
        ModLoader.addRecipe((ItemStack) new ItemStack((Item) itemDisk, 1), (Object[]) new Object[]{"WWW", "W W", "WIW", Character.valueOf('I'), Item.IRON_INGOT, Character.valueOf('W'), Block.WOOD});
        ModLoader.addShapelessRecipe((ItemStack) new ItemStack((Item) itemDisk, 1, 1), (Object[]) new Object[]{new ItemStack((Item) itemDisk, 1, 0), Item.REDSTONE});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockBackplane, 1, 0), (Object[]) new Object[]{"ICI", "IGI", "ICI", Character.valueOf('C'), RedPowerBase.itemFineCopper, Character.valueOf('I'), Block.IRON_FENCE, Character.valueOf('G'), Item.GOLD_INGOT});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockBackplane, 1, 1), (Object[]) new Object[]{"IRI", "RDR", "IRI", Character.valueOf('I'), Block.IRON_FENCE, Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('D'), Item.DIAMOND});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockPeripheral, 1, 0), (Object[]) new Object[]{"GWW", "GPR", "GBW", Character.valueOf('P'), new ItemStack((Item) RedPowerBase.itemLumar, 1, 5), Character.valueOf('G'), Block.GLASS, Character.valueOf('W'), Block.WOOD, Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('B'), new ItemStack((Block) RedPowerBase.blockMicro, 1, 3072)});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockPeripheral, 1, 1), (Object[]) new Object[]{"WWW", "RDR", "WBW", Character.valueOf('W'), Block.WOOD, Character.valueOf('D'), Block.DIAMOND_BLOCK, Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('B'), new ItemStack((Block) RedPowerBase.blockMicro, 1, 3072)});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockPeripheral, 1, 2), (Object[]) new Object[]{"WWW", "WMR", "WBW", Character.valueOf('G'), Block.GLASS, Character.valueOf('W'), Block.WOOD, Character.valueOf('M'), RedPowerBase.itemMotor, Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('B'), new ItemStack((Block) RedPowerBase.blockMicro, 1, 3072)});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) blockFlatPeripheral, 1, 0), (Object[]) new Object[]{"WCW", "WRW", "WBW", Character.valueOf('W'), Block.WOOD, Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('C'), new ItemStack((Block) RedPowerBase.blockMicro, 1, 768), Character.valueOf('B'), new ItemStack((Block) RedPowerBase.blockMicro, 1, 3072)});
        ModLoader.addRecipe((ItemStack) new ItemStack((Block) RedPowerBase.blockMicro, 8, 3072), (Object[]) new Object[]{"C", "C", "C", Character.valueOf('C'), RedPowerBase.itemFineCopper});
        MinecraftForge.addDungeonLoot((ItemStack) new ItemStack((Item) itemDisk, 1, 1), (float) 1.0f);
        Config.saveConfig();
    }
}

