/* X-RP - decompiled with CFR */
package eloraam.core;

import forge.ISidedInventory;
import net.minecraft.server.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

public class MachineLib {

    public static boolean addToInventory(World arg, ItemStack arg0, WorldCoord arg1, int arg2) {
        return MachineLib.addToInventoryCore(arg, arg0, arg1, arg2, true);
    }

    public static boolean addToInventoryCore(IInventory arg, ItemStack arg0, int arg1, int arg2, boolean arg3) {
        ItemStack arg5;
        int arg4 = arg1;
        while (arg4 < arg1 + arg2) {
            arg5 = arg.getItem(arg4);
            if (arg5 == null) {
                if (!arg3) {
                    return true;
                }
            } else if (arg0.doMaterialsMatch(arg5)) {
                int arg6 = Math.min(arg5.getMaxStackSize(), arg.getMaxStackSize());
                if ((arg6 -= arg5.count) > 0) {
                    int arg7 = Math.min(arg6, arg0.count);
                    if (!arg3) {
                        return true;
                    }
                    arg5.count += arg7;
                    arg.setItem(arg4, arg5);
                    arg0.count -= arg7;
                    if (arg0.count == 0) {
                        return true;
                    }
                }
            }
            ++arg4;
        }
        if (!arg3) {
            return false;
        }
        arg4 = arg1;
        while (arg4 < arg1 + arg2) {
            arg5 = arg.getItem(arg4);
            if (arg5 == null) {
                if (arg.getMaxStackSize() >= arg0.count) {
                    arg.setItem(arg4, arg0);
                    return true;
                }
                arg.setItem(arg4, arg0.a(arg.getMaxStackSize()));
            }
            ++arg4;
        }
        return false;
    }

    public static boolean addToInventoryCore(World arg, ItemStack arg0, WorldCoord arg1, int arg2, boolean arg3) {
        IInventory arg4 = MachineLib.getInventory(arg, arg1);
        if (arg4 == null) {
            return false;
        }
        int arg5 = 0;
        int arg6 = arg4.getSize();
        if (arg4 instanceof ISidedInventory) {
            ISidedInventory arg7 = (ISidedInventory) arg4;
            arg5 = arg7.getStartInventorySide(arg2);
            arg6 = arg7.getSizeInventorySide(arg2);
        }
        return MachineLib.addToInventoryCore(arg4, arg0, arg5, arg6, arg3);
    }

    public static boolean addToRandomInventory(World arg, ItemStack arg0, int arg1, int arg2, int arg3) {
        return false;
    }

    public static boolean canAddToInventory(World arg, ItemStack arg0, WorldCoord arg1, int arg2) {
        return MachineLib.addToInventoryCore(arg, arg0, arg1, arg2, false);
    }

    public static ItemStack collectOneStack(IInventory arg, int arg0, int arg1, ItemStack arg2) {
        ItemStack arg3 = null;
        int arg4 = arg2 != null ? arg2.count : 1;
        int arg5 = arg0;
        while (arg5 < arg0 + arg1) {
            ItemStack arg6 = arg.getItem(arg5);
            if (arg6 != null && arg6.count != 0) {
                if (arg2 == null) {
                    arg.setItem(arg5, null);
                    return arg6;
                }
                if (MachineLib.compareItem(arg2, arg6) == 0) {
                    int arg7 = Math.min(arg6.count, arg4);
                    if (arg3 == null) {
                        arg3 = arg.splitStack(arg5, arg7);
                    } else {
                        arg.splitStack(arg5, arg7);
                        arg3.count += arg7;
                    }
                    if ((arg4 -= arg7) <= 0)
                        break;
                }
            }
            ++arg5;
        }
        return arg3;
    }

    public static int compareItem(ItemStack arg, ItemStack arg0) {
        // X-RP: replaced
	/*int arg1;
	if (arg.id != arg0.id) {
	    return arg.id - arg0.id;
	}
	if (arg.getData() == arg0.getData()) {
	    return 0;
	}
	if (arg.getItem().e()) {
	    return arg.getData() - arg0.getData();
	}
	byte by = arg.getData() > 1 ? (byte)(arg.getData() != arg.i() - 1 ? 0 : 1) : -1;
	int arg2 = arg0.getData() > 1 ? (arg0.getData() != arg0.i() - 1 ? 0 : 1) : -1;
	return arg1 - arg2;*/
        if (arg.id != arg.id)
            return arg.id - arg.id;
        if (arg.getData() == arg0.getData())
            return 0;
        if (arg.getItem().e()) {
            return arg.getData() - arg0.getData();
        }
        byte arg1 = arg.getData() > 1 ? (byte) (arg.getData() != arg.i() - 1 ? 0 : 1) : -1;
        byte arg2 = arg0.getData() > 1 ? (byte) (arg0.getData() != arg0.i() - 1 ? 0 : 1) : -1;
        return arg1 - arg2;
    }

    public static int decMatchCount(FilterMap arg, int[] arg0, ItemStack arg1) {
        ArrayList arg2 = (ArrayList) arg.map.get((Object) arg1);
        if (arg2 == null) {
            return 0;
        }
        int arg3 = (Integer) arg2.get(0);
        int arg4 = Math.min(arg0[arg3], arg1.count);
        int[] arrn = arg0;
        int n = arg3;
        arrn[n] = arrn[n] - arg4;
        return arg4;
    }

    public static void decMatchCounts(FilterMap arg, int[] arg0, IInventory arg1, int arg2, int arg3) {
        int arg4 = arg2;
        while (arg4 < arg2 + arg3) {
            ItemStack arg5 = arg1.getItem(arg4);
            if (arg5 != null && arg5.count != 0) {
                MachineLib.decMatchCount(arg, arg0, arg5);
            }
            ++arg4;
        }
    }

    public static void ejectItem(World arg, WorldCoord arg0, ItemStack arg1, int arg2) {
        arg0 = arg0.copy();
        arg0.step(arg2);
        EntityItem arg3 = new EntityItem(arg, (double) arg0.x + 0.5, (double) arg0.y + 0.5, (double) arg0.z + 0.5, arg1);
        arg3.motX = 0.0;
        arg3.motY = 0.0;
        arg3.motZ = 0.0;
        switch (arg2) {
            case 0: {
                arg3.motY = -0.3;
                break;
            }
            case 1: {
                arg3.motY = 0.3;
                break;
            }
            case 2: {
                arg3.motZ = -0.3;
                break;
            }
            case 3: {
                arg3.motZ = 0.3;
                break;
            }
            case 4: {
                arg3.motX = -0.3;
                break;
            }
            default: {
                arg3.motX = 0.3;
            }
        }
        arg3.pickupDelay = 10;
        arg.addEntity((Entity) arg3);
    }

    public static boolean emptyInventory(IInventory arg, int arg0, int arg1) {
        int arg2 = arg0;
        while (arg2 < arg0 + arg1) {
            ItemStack arg3 = arg.getItem(arg2);
            if (arg3 != null && arg3.count != 0) {
                return false;
            }
            ++arg2;
        }
        return true;
    }

    public static int[] genMatchCounts(FilterMap arg) {
        int[] arg0 = new int[arg.filter.length];
        int arg1 = 0;
        while (arg1 < arg.filter.length) {
            ArrayList<Integer> arg3; // X-RP: added type decl
            ItemStack arg2 = arg.filter[arg1];
            if (arg2 != null && arg2.count != 0 && (arg3 = (ArrayList) arg.map.get((Object) arg2)) != null && (Integer) arg3.get(0) == arg1) {
                for (Integer arg5 : arg3) {
                    int arg10000 = arg5;
                    int[] arrn = arg0;
                    int n = arg10000;
                    arrn[n] = arrn[n] + arg.filter[arg1].count;
                }
            }
            ++arg1;
        }
        return arg0;
    }

    public static IInventory getInventory(World arg, WorldCoord arg0) {
        IInventory arg1 = (IInventory) CoreLib.getTileEntity((IBlockAccess) arg, arg0, IInventory.class);
        if (!(arg1 instanceof TileEntityChest)) {
            return arg1;
        }
        TileEntityChest arg2 = (TileEntityChest) CoreLib.getTileEntity((IBlockAccess) arg, arg0.x - 1, arg0.y, arg0.z, TileEntityChest.class);
        if (arg2 != null) {
            return new InventoryLargeChest("Large chest", (IInventory) arg2, arg1);
        }
        arg2 = (TileEntityChest) CoreLib.getTileEntity((IBlockAccess) arg, arg0.x + 1, arg0.y, arg0.z, TileEntityChest.class);
        if (arg2 != null) {
            return new InventoryLargeChest("Large chest", arg1, (IInventory) arg2);
        }
        arg2 = (TileEntityChest) CoreLib.getTileEntity((IBlockAccess) arg, arg0.x, arg0.y, arg0.z - 1, TileEntityChest.class);
        if (arg2 != null) {
            return new InventoryLargeChest("Large chest", (IInventory) arg2, arg1);
        }
        arg2 = (TileEntityChest) CoreLib.getTileEntity((IBlockAccess) arg, arg0.x, arg0.y, arg0.z + 1, TileEntityChest.class);
        return arg2 != null ? new InventoryLargeChest("Large chest", arg1, (IInventory) arg2) : arg1;
    }

    public static boolean handleItem(World arg, ItemStack arg0, WorldCoord arg1, int arg2) {
        WorldCoord arg3 = arg1.copy();
        arg3.step(arg2);
        if (arg0.count == 0) {
            return true;
        }
        if (TubeLib.addToTubeRoute(arg, arg0, arg1, arg3, arg2 ^ 1)) {
            return true;
        }
        if (MachineLib.addToInventory(arg, arg0, arg3, (arg2 ^ 1) & 63)) {
            return true;
        }
        TileEntity arg4 = (TileEntity) CoreLib.getTileEntity((IBlockAccess) arg, arg3, TileEntity.class);
        if (!(arg4 instanceof IInventory) && !(arg4 instanceof ITubeConnectable)) {
            if (arg.isBlockSolidOnSide(arg3.x, arg3.y, arg3.z, arg2 ^ 1)) {
                return false;
            }
            MachineLib.ejectItem(arg, arg1, arg0, arg2);
            return true;
        }
        return false;
    }

    public static boolean handleItem(World arg, TubeItem arg0, WorldCoord arg1, int arg2) {
        WorldCoord arg3 = arg1.copy();
        arg3.step(arg2);
        if (arg0.item.count == 0) {
            return true;
        }
        if (TubeLib.addToTubeRoute(arg, arg0, arg1, arg3, arg2 ^ 1)) {
            return true;
        }
        if (MachineLib.addToInventory(arg, arg0.item, arg3, (arg2 ^ 1) & 63)) {
            return true;
        }
        TileEntity arg4 = (TileEntity) CoreLib.getTileEntity((IBlockAccess) arg, arg3, TileEntity.class);
        if (!(arg4 instanceof IInventory) && !(arg4 instanceof ITubeConnectable)) {
            if (arg.isBlockSolidOnSide(arg3.x, arg3.y, arg3.z, arg2 ^ 1)) {
                return false;
            }
            MachineLib.ejectItem(arg, arg1, arg0.item, arg2);
            return true;
        }
        return false;
    }

    public static boolean isMatchEmpty(int[] arg) {
        int arg0 = 0;
        while (arg0 < arg.length) {
            if (arg[arg0] > 0) {
                return false;
            }
            ++arg0;
        }
        return true;
    }

    public static FilterMap makeFilterMap(ItemStack[] arg) {
        return new FilterMap(arg);
    }

    public static FilterMap makeFilterMap(ItemStack[] arg, int arg0, int arg1) {
        ItemStack[] arg2 = new ItemStack[arg1];
        System.arraycopy(arg, arg0, arg2, 0, arg1);
        return new FilterMap(arg2);
    }

    public static boolean matchAllCol(FilterMap arg, IInventory arg0, int arg1, int arg2, int arg3) {
        int[] arg4 = new int[5];
        int arg5 = arg1;
        while (arg5 < arg1 + arg2) {
            ArrayList<Integer> arg7; // X-RP: added type decl
            ItemStack arg6 = arg0.getItem(arg5);
            if (arg6 != null && arg6.count != 0 && (arg7 = (ArrayList) arg.map.get((Object) arg6)) != null) {
                int arg8 = arg6.count;
                for (Integer arg10 : arg7) {
                    if ((arg10 & 7) != arg3)
                        continue;
                    int arg11 = arg10 >> 3;
                    int arg12 = Math.min(arg8, arg.filter[arg10.intValue()].count - arg4[arg11]);
                    int[] arrn = arg4;
                    int n = arg11;
                    arrn[n] = arrn[n] + arg12;
                    if ((arg8 -= arg12) == 0)
                        break;
                }
            }
            ++arg5;
        }
        boolean arg13 = false;
        int arg14 = 0;
        while (arg14 < 5) {
            ItemStack arg15 = arg.filter[arg14 * 8 + arg3];
            if (arg15 != null && arg15.count != 0) {
                arg13 = true;
                if (arg15.count > arg4[arg14]) {
                    return false;
                }
            }
            ++arg14;
        }
        return arg13;
    }

    public static int matchAnyStack(FilterMap arg, IInventory arg0, int arg1, int arg2) {
        int[] arg3 = new int[arg.filter.length];
        int arg4 = arg1;
        while (arg4 < arg1 + arg2) {
            ArrayList<Integer> arg6; // X-RP: added type decl
            ItemStack arg5 = arg0.getItem(arg4);
            if (arg5 != null && arg5.count != 0 && (arg6 = (ArrayList) arg.map.get((Object) arg5)) != null) {
                for (Integer arg8 : arg6) {
                    int arg10000 = arg8;
                    int[] arrn = arg3;
                    int n = arg10000;
                    arrn[n] = arrn[n] + arg5.count;
                    if (arg3[arg8] < arg.filter[arg8.intValue()].count)
                        continue;
                    return arg8;
                }
            }
            ++arg4;
        }
        return -1;
    }

    public static int matchAnyStackCol(FilterMap arg, IInventory arg0, int arg1, int arg2, int arg3) {
        int[] arg4 = new int[5];
        int arg5 = arg1;
        while (arg5 < arg1 + arg2) {
            ArrayList<Integer> arg7; // X-RP: added type decl
            ItemStack arg6 = arg0.getItem(arg5);
            if (arg6 != null && arg6.count != 0 && (arg7 = (ArrayList) arg.map.get((Object) arg6)) != null) {
                for (Integer arg9 : arg7) {
                    if ((arg9 & 7) != arg3)
                        continue;
                    int arg10 = arg9 >> 3;
                    int[] arrn = arg4;
                    int n = arg10;
                    arrn[n] = arrn[n] + arg6.count;
                    if (arg4[arg10] < arg.filter[arg9.intValue()].count)
                        continue;
                    return arg9;
                }
            }
            ++arg5;
        }
        return -1;
    }

    public static boolean matchOneStack(FilterMap arg, IInventory arg0, int arg1, int arg2, int arg3) {
        ItemStack arg4 = arg.filter[arg3];
        int arg5 = arg4 != null ? arg4.count : 1;
        int arg6 = arg1;
        while (arg6 < arg1 + arg2) {
            ItemStack arg7 = arg0.getItem(arg6);
            if (arg7 != null && arg7.count != 0) {
                int arg8;
                if (arg4 == null) {
                    return true;
                }
                if (MachineLib.compareItem(arg4, arg7) == 0 && (arg5 -= (arg8 = Math.min(arg7.count, arg5))) <= 0) {
                    return true;
                }
            }
            ++arg6;
        }
        return false;
    }

    public static class FilterMap {

        protected TreeMap map;
        protected ItemStack[] filter;

        public FilterMap(ItemStack[] arg0) {
            this.filter = arg0;
            this.map = new TreeMap(new Comparator() {

                public int compare(ItemStack arg0, ItemStack arg1) {
                    return MachineLib.compareItem(arg0, arg1);
                }

                public int compare(Object arg0, Object arg1) {
                    return this.compare((ItemStack) arg0, (ItemStack) arg1);
                }
            });
            int arg1 = 0;
            while (arg1 < arg0.length) {
                if (arg0[arg1] != null && arg0[arg1].count != 0) {
                    ArrayList<Integer> arg2 = (ArrayList<Integer>) this.map.get((Object) arg0[arg1]);
                    if (arg2 == null) {
                        arg2 = new ArrayList<Integer>();
                        this.map.put(arg0[arg1], arg2);
                    }
                    arg2.add(arg1);
                }
                ++arg1;
            }
        }

        public boolean containsKey(ItemStack arg0) {
            return this.map.containsKey((Object) arg0);
        }

        public int firstMatch(ItemStack arg0) {
            ArrayList arg1 = (ArrayList) this.map.get((Object) arg0);
            return arg1 == null ? -1 : (Integer) arg1.get(0);
        }

        public int size() {
            return this.map.size();
        }

    }

}
