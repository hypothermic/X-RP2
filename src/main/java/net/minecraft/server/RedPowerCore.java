/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  forge.ICraftingHandler
 *  forge.IDestroyToolHandler
 *  forge.MinecraftForge
 *  net.minecraft.server.Block
 *  net.minecraft.server.CraftingManager
 *  net.minecraft.server.EntityHuman
 *  net.minecraft.server.EntityLiving
 *  net.minecraft.server.IDataManager
 *  net.minecraft.server.IInventory
 *  net.minecraft.server.ItemStack
 *  net.minecraft.server.Packet
 *  net.minecraft.server.PlayerInventory
 *  net.minecraft.server.World
 *  net.minecraft.server.WorldNBTStorage
 */
package net.minecraft.server;

import eloraam.core.*;
import forge.ICraftingHandler;
import forge.IDestroyToolHandler;
import forge.MinecraftForge;

import java.io.File;

public class RedPowerCore {
    private static boolean initialized = false;
    public static int customBlockModel;
    public static final int idLogic = 1;
    public static final int idTimer = 2;
    public static final int idSequencer = 3;
    public static final int idCounter = 4;
    public static final int idWiring = 5;
    public static final int idArray = 6;
    public static final int idMachine = 7;
    public static final int idMachinePanel = 8;
    public static final int idFrame = 9;
    public static final int idItemUpdate = 10;
    public static final int idPipeUpdate = 11;

    private static void setupDestroyToolHook() {
        IDestroyToolHandler iDestroyToolHandler = new IDestroyToolHandler() {

            public void onDestroyCurrentItem(EntityHuman entityHuman, ItemStack itemStack) {
                int n = entityHuman.inventory.itemInHandIndex;
                int n2 = itemStack.id;
                int n3 = itemStack.getData();
                ItemStack itemStack2 = entityHuman.inventory.getItem(n + 27);
                ItemStack itemStack3 = entityHuman.inventory.getItem(n);
                if (itemStack2 == null) {
                    return;
                }
                if (itemStack2.id != n2) {
                    return;
                }
                if (itemStack2.usesData() && itemStack2.getData() != n3) {
                    return;
                }
                if (itemStack2.usesData() && itemStack2.getData() != n3) {
                    return;
                }
                if (itemStack3 != null) {
                    if (itemStack3.count > 0) {
                        return;
                    }
                    itemStack3.id = itemStack2.id;
                    itemStack3.setData(itemStack2.getData());
                    itemStack3.count = itemStack2.count;
                    entityHuman.inventory.setItem(n + 27, null);
                } else {
                    entityHuman.inventory.setItem(n, itemStack2);
                    entityHuman.inventory.setItem(n + 27, null);
                }
                for (int i = 2; i > 0; --i) {
                    ItemStack itemStack4 = entityHuman.inventory.getItem(n + 9 * i);
                    if (itemStack4 == null) {
                        return;
                    }
                    if (itemStack4.id != n2) {
                        return;
                    }
                    if (itemStack4.usesData() && itemStack4.getData() != n3) {
                        return;
                    }
                    if (itemStack4.usesData() && itemStack4.getData() != n3) {
                        return;
                    }
                    entityHuman.inventory.setItem(n + 9 * i + 9, itemStack4);
                    entityHuman.inventory.setItem(n + 9 * i, null);
                }
            }
        };
        MinecraftForge.registerDestroyToolHandler((IDestroyToolHandler) iDestroyToolHandler);
    }

    private static void setupCraftHook() {
        ICraftingHandler iCraftingHandler = new ICraftingHandler() {

            public void onTakenFromCrafting(EntityHuman entityHuman, ItemStack itemStack, IInventory iInventory) {
                for (int i = 0; i < iInventory.getSize(); ++i) {
                    Integer n;
                    ItemStack itemStack2 = iInventory.getItem(i);
                    if (itemStack2 == null || !CraftLib.damageOnCraft.contains(itemStack2.id)) continue;
                    ++itemStack2.count;
                    itemStack2.damage(1, (EntityLiving) entityHuman);
                    if (itemStack2.count != 1 || (n = (Integer) CraftLib.damageContainer.get(itemStack2.id)) == null)
                        continue;
                    iInventory.setItem(i, new ItemStack(n.intValue(), 2, 0));
                }
            }
        };
        MinecraftForge.registerCraftingHandler((ICraftingHandler) iCraftingHandler);
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        Config.loadConfig();
        MinecraftForge.versionDetect((String) "RedPowerCore", (int) 3, (int) 1, (int) 2);
        Packet.a((int) 211, (boolean) true, (boolean) true, Packet211TileDesc.class);
        Packet.a((int) 212, (boolean) true, (boolean) true, Packet212GuiEvent.class);
        RedPowerCore.setupDestroyToolHook();
        RedPowerCore.setupCraftHook();
        MinecraftForge.setBlockHarvestLevel((Block) Block.REDSTONE_ORE, (String) "pickaxe", (int) 2);
        MinecraftForge.setBlockHarvestLevel((Block) Block.GLOWING_REDSTONE_ORE, (String) "pickaxe", (int) 2);
        CraftingManager.getInstance().getRecipies().add(new CoverRecipe());
        Config.saveConfig();
    }

    public static File getSaveDir(World world) {
        IDataManager iDataManager = world.getDataManager();
        if (!(iDataManager instanceof WorldNBTStorage)) {
            return null;
        }
        WorldNBTStorage worldNBTStorage = (WorldNBTStorage) iDataManager;
        return worldNBTStorage.getDirectory();
    }

}

