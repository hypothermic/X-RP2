/* X-RP - decompiled with CFR */
package eloraam.core;

import forge.IConnectRedstone;
import net.minecraft.server.*;

import java.util.*;

public class RedPowerLib {

    private static Set powerClassMapping = Collections.synchronizedSet(new HashSet());
    private static Set blockUpdates = Collections.synchronizedSet(new HashSet());
    private static List powerSearch = Collections.synchronizedList(new LinkedList());
    private static Set powerSearchTest = Collections.synchronizedSet(new HashSet());
    private static boolean searching = false;

    public static void addCompatibleMapping(int arg, int arg0) {
        powerClassMapping.add(Arrays.asList(arg, arg0));
        powerClassMapping.add(Arrays.asList(arg0, arg));
    }

    private static void addIndBl(int arg, int arg0, int arg1, int arg2, int arg3) {
        int arg4;
        switch (arg2) {
            case 0: {
                --arg0;
                arg4 = arg3 + 2;
                break;
            }
            case 1: {
                ++arg0;
                arg4 = arg3 + 2;
                break;
            }
            case 2: {
                --arg1;
                arg4 = arg3 + (arg3 & 2);
                break;
            }
            case 3: {
                ++arg1;
                arg4 = arg3 + (arg3 & 2);
                break;
            }
            case 4: {
                --arg;
                arg4 = arg3;
                break;
            }
            default: {
                ++arg;
                arg4 = arg3;
            }
        }
        switch (arg4) {
            case 0: {
                --arg0;
                break;
            }
            case 1: {
                ++arg0;
                break;
            }
            case 2: {
                --arg1;
                break;
            }
            case 3: {
                ++arg1;
                break;
            }
            case 4: {
                --arg;
                break;
            }
            case 5: {
                ++arg;
            }
        }
        RedPowerLib.addSearchBlock(arg, arg0, arg1);
    }

    public static void addSearchBlock(int arg, int arg0, int arg1) {
        RedPowerLib.addStartSearchBlock(arg, arg0, arg1);
        blockUpdates.add(Arrays.asList(arg, arg0, arg1));
    }

    public static void addSearchBlocks(int arg, int arg0, int arg1, int arg2, int arg3) {
        int arg4 = arg2 << 1 & 11184810 | arg2 >> 1 & 5592405;
        if ((arg2 & 17895680) > 0) {
            RedPowerLib.addSearchBlock(arg, arg0 - 1, arg1);
        }
        if ((arg2 & 35791360) > 0) {
            RedPowerLib.addSearchBlock(arg, arg0 + 1, arg1);
        }
        if ((arg2 & 71565329) > 0) {
            RedPowerLib.addSearchBlock(arg, arg0, arg1 - 1);
        }
        if ((arg2 & 143130658) > 0) {
            RedPowerLib.addSearchBlock(arg, arg0, arg1 + 1);
        }
        if ((arg2 & 268452932) > 0) {
            RedPowerLib.addSearchBlock(arg - 1, arg0, arg1);
        }
        if ((arg2 & 536905864) > 0) {
            RedPowerLib.addSearchBlock(arg + 1, arg0, arg1);
        }
        int arg5 = 0;
        while (arg5 < 6) {
            int arg6 = 0;
            while (arg6 < 4) {
                if ((arg3 & 1 << arg5 * 4 + arg6) > 0) {
                    RedPowerLib.addIndBl(arg, arg0, arg1, arg5, arg6);
                }
                ++arg6;
            }
            ++arg5;
        }
    }

    public static void addStartSearchBlock(int arg, int arg0, int arg1) {
        List<Integer> arg2 = Arrays.asList(arg, arg0, arg1);
        if (!powerSearchTest.contains(arg2)) {
            powerSearch.add(arg2);
            powerSearchTest.add(arg2);
        }
    }

    public static void addUpdateBlock(int arg, int arg0, int arg1) {
        int arg2 = -3;
        while (arg2 <= 3) {
            int arg3 = -3;
            while (arg3 <= 3) {
                int arg4 = -3;
                while (arg4 <= 3) {
                    int arg5 = arg2 >= 0 ? arg2 : -arg2;
                    arg5 += arg3 >= 0 ? arg3 : -arg3;
                    if ((arg5 += arg4 >= 0 ? arg4 : -arg4) <= 3) {
                        blockUpdates.add(Arrays.asList(arg + arg2, arg0 + arg3, arg1 + arg4));
                    }
                    ++arg4;
                }
                ++arg3;
            }
            ++arg2;
        }
    }

    public static boolean canSupportWire(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3) {
        switch (arg3) {
            case 0: {
                --arg1;
                break;
            }
            case 1: {
                ++arg1;
                break;
            }
            case 2: {
                --arg2;
                break;
            }
            case 3: {
                ++arg2;
                break;
            }
            case 4: {
                --arg0;
                break;
            }
            case 5: {
                ++arg0;
            }
        }
        arg3 ^= 1;
        if (arg instanceof World) {
            World arg4 = (World) arg;
            if (!arg4.isLoaded(arg0, arg1, arg2)) {
                return true;
            }
            if (arg4.isBlockSolidOnSide(arg0, arg1, arg2, arg3)) {
                return true;
            }
        }
        if (arg.e(arg0, arg1, arg2)) {
            return true;
        }
        int arg6 = arg.getTypeId(arg0, arg1, arg2);
        if (arg6 == Block.PISTON_MOVING.id) {
            return true;
        }
        if (arg6 != Block.PISTON_STICKY.id && arg6 != Block.PISTON.id) {
            IMultipart arg7 = (IMultipart) CoreLib.getTileEntity(arg, arg0, arg1, arg2, IMultipart.class);
            return arg7 == null ? false : arg7.isSideNormal(arg3);
        }
        int arg5 = arg.getData(arg0, arg1, arg2) & 7;
        if (arg0 != arg5 && arg5 != 7) {
            return true;
        }
        return false;
    }

    public static int getConDirMask(int arg) {
        switch (arg) {
            case 0: {
                return 17895680;
            }
            case 1: {
                return 35791360;
            }
            case 2: {
                return 71565329;
            }
            case 3: {
                return 143130658;
            }
            case 4: {
                return 268452932;
            }
        }
        return 536905864;
    }

    public static int getConnections(IBlockAccess arg, IConnectable arg0, int arg1, int arg2, int arg3) {
        int arg6;
        int arg4 = arg0.getConnectableMask();
        int arg5 = 0;
        if ((arg4 & 17895680) > 0) {
            arg6 = arg0.getConnectClass(0);
            arg5 |= RedPowerLib.getConSides(arg, arg1, arg2 - 1, arg3, 1, arg6) & 35791360;
        }
        if ((arg4 & 35791360) > 0) {
            arg6 = arg0.getConnectClass(1);
            arg5 |= RedPowerLib.getConSides(arg, arg1, arg2 + 1, arg3, 0, arg6) & 17895680;
        }
        if ((arg4 & 71565329) > 0) {
            arg6 = arg0.getConnectClass(2);
            arg5 |= RedPowerLib.getConSides(arg, arg1, arg2, arg3 - 1, 3, arg6) & 143130658;
        }
        if ((arg4 & 143130658) > 0) {
            arg6 = arg0.getConnectClass(3);
            arg5 |= RedPowerLib.getConSides(arg, arg1, arg2, arg3 + 1, 2, arg6) & 71565329;
        }
        if ((arg4 & 268452932) > 0) {
            arg6 = arg0.getConnectClass(4);
            arg5 |= RedPowerLib.getConSides(arg, arg1 - 1, arg2, arg3, 5, arg6) & 536905864;
        }
        if ((arg4 & 536905864) > 0) {
            arg6 = arg0.getConnectClass(5);
            arg5 |= RedPowerLib.getConSides(arg, arg1 + 1, arg2, arg3, 4, arg6) & 268452932;
        }
        arg5 = arg5 << 1 & 715827882 | arg5 >> 1 & 357913941;
        return arg5 &= arg4;
    }

    public static int getConSides(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3, int arg4) {
        int arg5 = arg.getTypeId(arg0, arg1, arg2);
        if (arg5 == 0) {
            return 0;
        }
        IConnectable arg6 = (IConnectable) CoreLib.getTileEntity(arg, arg0, arg1, arg2, IConnectable.class);
        if (arg6 != null) {
            int arg7 = arg6.getConnectClass(arg3);
            return RedPowerLib.isCompatible(arg7, arg4) ? arg6.getConnectableMask() : 0;
        }
        if (!RedPowerLib.isCompatible(0, arg4)) {
            return 0;
        }
        if (Block.byId[arg5] instanceof IConnectRedstone) {
            IConnectRedstone arg12 = (IConnectRedstone) Block.byId[arg5];
            return arg12.canConnectRedstone(arg, arg0, arg1, arg2, RedPowerLib.getDirToRedstone(arg3)) ? RedPowerLib.getConDirMask(arg3) : 0;
        }
        if (arg5 != Block.PISTON.id && arg5 != Block.PISTON_STICKY.id) {
            if (arg5 == Block.PISTON_MOVING.id) {
                TileEntity arg11 = arg.getTileEntity(arg0, arg1, arg2);
                if (!(arg11 instanceof TileEntityPiston)) {
                    return 0;
                }
                TileEntityPiston arg8 = (TileEntityPiston) arg11;
                int arg9 = arg8.c();
                if (arg9 != Block.PISTON.id && arg9 != Block.PISTON_STICKY.id) {
                    return 0;
                }
                int arg10 = arg8.k() & 7;
                return arg10 == 7 ? 0 : 1073741823 ^ RedPowerLib.getConDirMask(arg10);
            }
            if (arg5 != Block.DISPENSER.id && arg5 != Block.STONE_BUTTON.id && arg5 != Block.LEVER.id) {
                if (arg5 != Block.REDSTONE_TORCH_ON.id && arg5 != Block.REDSTONE_TORCH_OFF.id) {
                    if (arg5 != Block.DIODE_OFF.id && arg5 != Block.DIODE_ON.id) {
                        return !Block.byId[arg5].isPowerSource() ? 0 : 1073741823;
                    }
                    int arg7 = arg.getData(arg0, arg1, arg2) & 1;
                    return arg7 <= 0 ? 3 : 12;
                }
                return 1073741823;
            }
            return 1073741823;
        }
        int arg7 = arg.getData(arg0, arg1, arg2) & 7;
        return arg7 == 7 ? 0 : 1073741823 ^ RedPowerLib.getConDirMask(arg7);
    }

    public static int getDirToRedstone(int arg) {
        switch (arg) {
            case 2: {
                return 0;
            }
            case 3: {
                return 2;
            }
            case 4: {
                return 3;
            }
            case 5: {
                return 1;
            }
        }
        return 0;
    }

    private static int getES1(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        int arg6 = arg.getTypeId(arg0, arg1, arg2);
        if (arg6 == 0) {
            return 0;
        }
        IConnectable arg7 = (IConnectable) CoreLib.getTileEntity(arg, arg0, arg1, arg2, IConnectable.class);
        if (arg7 == null) {
            return 0;
        }
        int arg8 = arg7.getCornerPowerMode();
        if (arg8 == 0 || arg5 == 2 && arg8 == 2) {
            return 0;
        }
        int arg9 = arg7.getConnectClass(arg3);
        return RedPowerLib.isCompatible(arg9, arg4) ? arg7.getConnectableMask() : 0;
    }

    public static int getExtConnections(IBlockAccess arg, IConnectable arg0, int arg1, int arg2, int arg3) {
        int arg4 = 0;
        int arg5 = arg4 | RedPowerLib.getExtConSides(arg, arg0, arg1, arg2 - 1, arg3, 0);
        arg5 |= RedPowerLib.getExtConSides(arg, arg0, arg1, arg2 + 1, arg3, 1);
        arg5 |= RedPowerLib.getExtConSides(arg, arg0, arg1, arg2, arg3 - 1, 2);
        arg5 |= RedPowerLib.getExtConSides(arg, arg0, arg1, arg2, arg3 + 1, 3);
        arg5 |= RedPowerLib.getExtConSides(arg, arg0, arg1 - 1, arg2, arg3, 4);
        return arg5 |= RedPowerLib.getExtConSides(arg, arg0, arg1 + 1, arg2, arg3, 5);
    }

    public static int getExtConSides(IBlockAccess arg, IConnectable arg0, int arg1, int arg2, int arg3, int arg4) {
        int arg9;
        int arg5 = arg0.getCornerPowerMode();
        int arg6 = arg0.getConnectableMask();
        if ((arg6 &= RedPowerLib.getConDirMask(arg4) & 16777215) == 0) {
            return 0;
        }
        int arg7 = arg.getTypeId(arg1, arg2, arg3);
        if (CoverLib.blockCoverPlate != null && arg7 == CoverLib.blockCoverPlate.id) {
            if (arg.getData(arg1, arg2, arg3) != 0) {
                return 0;
            }
            ICoverable arg8 = (ICoverable) CoreLib.getTileEntity(arg, arg1, arg2, arg3, ICoverable.class);
            if (arg8 == null) {
                return 0;
            }
            arg9 = arg8.getCoverMask();
            if ((arg9 & 1 << (arg4 ^ 1)) > 0) {
                return 0;
            }
            arg9 |= arg9 << 12;
            arg9 |= arg9 << 6;
            arg9 &= 197379;
            arg9 |= arg9 << 3;
            arg9 &= 1118481;
            arg9 |= arg9 << 2;
            arg9 |= arg9 << 1;
            arg6 &= ~arg9;
        } else if (arg7 != 0 && arg7 != Block.WATER.id && arg7 != Block.STATIONARY_WATER.id) {
            return 0;
        }
        int arg10 = arg0.getConnectClass(arg4);
        arg9 = 0;
        if ((arg6 & 15) > 0) {
            arg9 |= RedPowerLib.getES1(arg, arg1, arg2 - 1, arg3, 1, arg10, arg5) & 2236928;
        }
        if ((arg6 & 240) > 0) {
            arg9 |= RedPowerLib.getES1(arg, arg1, arg2 + 1, arg3, 0, arg10, arg5) & 1118464;
        }
        if ((arg6 & 3840) > 0) {
            arg9 |= RedPowerLib.getES1(arg, arg1, arg2, arg3 - 1, 3, arg10, arg5) & 8912930;
        }
        if ((arg6 & 61440) > 0) {
            arg9 |= RedPowerLib.getES1(arg, arg1, arg2, arg3 + 1, 2, arg10, arg5) & 4456465;
        }
        if ((arg6 & 983040) > 0) {
            arg9 |= RedPowerLib.getES1(arg, arg1 - 1, arg2, arg3, 5, arg10, arg5) & 34952;
        }
        if ((arg6 & 15728640) > 0) {
            arg9 |= RedPowerLib.getES1(arg, arg1 + 1, arg2, arg3, 4, arg10, arg5) & 17476;
        }
        arg9 >>= (arg4 ^ 1) << 2;
        arg9 = (arg9 & 10) >> 1 | (arg9 & 5) << 1;
        arg9 |= arg9 << 6;
        arg9 |= arg9 << 3;
        arg9 &= 4369;
        arg9 <<= arg4 & 1;
        switch (arg4) {
            case 0:
            case 1: {
                return arg9 << 8;
            }
            case 2:
            case 3: {
                return arg9 << 10 & 16711680 | arg9 & 255;
            }
        }
        return arg9 << 2;
    }

    private static int getIndCur(World arg, int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        int arg6;
        int arg7;
        switch (arg3) {
            case 0: {
                --arg1;
                arg6 = arg4 + 2;
                break;
            }
            case 1: {
                ++arg1;
                arg6 = arg4 + 2;
                break;
            }
            case 2: {
                --arg2;
                arg6 = arg4 + (arg4 & 2);
                break;
            }
            case 3: {
                ++arg2;
                arg6 = arg4 + (arg4 & 2);
                break;
            }
            case 4: {
                --arg0;
                arg6 = arg4;
                break;
            }
            default: {
                ++arg0;
                arg6 = arg4;
            }
        }
        switch (arg6) {
            case 0: {
                --arg1;
                arg7 = arg3 - 2;
                break;
            }
            case 1: {
                ++arg1;
                arg7 = arg3 - 2;
                break;
            }
            case 2: {
                --arg2;
                arg7 = arg3 & 1 | (arg3 & 4) >> 1;
                break;
            }
            case 3: {
                ++arg2;
                arg7 = arg3 & 1 | (arg3 & 4) >> 1;
                break;
            }
            case 4: {
                --arg0;
                arg7 = arg3;
                break;
            }
            default: {
                ++arg0;
                arg7 = arg3;
            }
        }
        return RedPowerLib.getTileCurrentStrength(arg, arg0, arg1, arg2, 1 << (arg7 ^ 1) << ((arg6 ^ 1) << 2), arg5);
    }

    public static int getMaxCurrentStrength(World arg, int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        int arg6 = -1;
        int arg7 = arg3 << 1 & 715827882 | arg3 >> 1 & 357913941;
        if ((arg3 & 17895680) > 0) {
            arg6 = Math.max(arg6, RedPowerLib.getTileOrRedstoneCurrentStrength(arg, arg0, arg1 - 1, arg2, arg7 & 35791360, arg5));
        }
        if ((arg3 & 35791360) > 0) {
            arg6 = Math.max(arg6, RedPowerLib.getTileOrRedstoneCurrentStrength(arg, arg0, arg1 + 1, arg2, arg7 & 17895680, arg5));
        }
        if ((arg3 & 71565329) > 0) {
            arg6 = Math.max(arg6, RedPowerLib.getTileOrRedstoneCurrentStrength(arg, arg0, arg1, arg2 - 1, arg7 & 143130658, arg5));
        }
        if ((arg3 & 143130658) > 0) {
            arg6 = Math.max(arg6, RedPowerLib.getTileOrRedstoneCurrentStrength(arg, arg0, arg1, arg2 + 1, arg7 & 71565329, arg5));
        }
        if ((arg3 & 268452932) > 0) {
            arg6 = Math.max(arg6, RedPowerLib.getTileOrRedstoneCurrentStrength(arg, arg0 - 1, arg1, arg2, arg7 & 536905864, arg5));
        }
        if ((arg3 & 536905864) > 0) {
            arg6 = Math.max(arg6, RedPowerLib.getTileOrRedstoneCurrentStrength(arg, arg0 + 1, arg1, arg2, arg7 & 268452932, arg5));
        }
        int arg8 = 0;
        while (arg8 < 6) {
            int arg9 = 0;
            while (arg9 < 4) {
                if ((arg4 & 1 << arg8 * 4 + arg9) > 0) {
                    arg6 = Math.max(arg6, RedPowerLib.getIndCur(arg, arg0, arg1, arg2, arg8, arg9, arg5));
                }
                ++arg9;
            }
            ++arg8;
        }
        return arg6;
    }

    public static int getPowerState(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3, int arg4) {
        int arg5 = 0;
        if ((arg3 & 17895680) > 0) {
            arg5 |= RedPowerLib.getSidePowerMask(arg, arg0, arg1 - 1, arg2, arg4, 0);
        }
        if ((arg3 & 35791360) > 0) {
            arg5 |= RedPowerLib.getSidePowerMask(arg, arg0, arg1 + 1, arg2, arg4, 1);
        }
        if ((arg3 & 71565329) > 0) {
            arg5 |= RedPowerLib.getSidePowerMask(arg, arg0, arg1, arg2 - 1, arg4, 2);
        }
        if ((arg3 & 143130658) > 0) {
            arg5 |= RedPowerLib.getSidePowerMask(arg, arg0, arg1, arg2 + 1, arg4, 3);
        }
        if ((arg3 & 268452932) > 0) {
            arg5 |= RedPowerLib.getSidePowerMask(arg, arg0 - 1, arg1, arg2, arg4, 4);
        }
        if ((arg3 & 536905864) > 0) {
            arg5 |= RedPowerLib.getSidePowerMask(arg, arg0 + 1, arg1, arg2, arg4, 5);
        }
        return arg5 & arg3;
    }

    public static int getRotPowerState(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        int arg6 = RedPowerLib.mapRotToCon(arg3, arg4);
        int arg7 = RedPowerLib.getPowerState(arg, arg0, arg1, arg2, arg6, arg5);
        return RedPowerLib.mapConToRot(arg7, arg4);
    }

    private static int getSidePowerMask(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3, int arg4) {
        IRedPowerConnectable arg5 = (IRedPowerConnectable) CoreLib.getTileEntity(arg, arg0, arg1, arg2, IRedPowerConnectable.class);
        int arg6 = RedPowerLib.getConDirMask(arg4);
        if (arg5 != null) {
            int arg7 = arg5.getPoweringMask(arg3);
            arg7 = (arg7 & 1431655765) << 1 | (arg7 & 715827882) >> 1;
            return arg7 & arg6;
        }
        return arg3 != 0 ? 0 : (RedPowerLib.isWeakPoweringTo(arg, arg0, arg1, arg2, arg4) ? arg6 & 16777215 : (RedPowerLib.isPoweringTo(arg, arg0, arg1, arg2, arg4) ? arg6 : 0));
    }

    public static int getTileCurrentStrength(World arg, int arg0, int arg1, int arg2, int arg3, int arg4) {
        IRedPowerConnectable arg5 = (IRedPowerConnectable) CoreLib.getTileEntity((IBlockAccess) arg, arg0, arg1, arg2, IRedPowerConnectable.class);
        if (arg5 == null) {
            return -1;
        }
        if (arg5 instanceof IRedPowerWiring) {
            IRedPowerWiring arg6 = (IRedPowerWiring) arg5;
            return arg6.getCurrentStrength(arg3, arg4);
        }
        return (arg5.getPoweringMask(arg4) & arg3) <= 0 ? -1 : 255;
    }

    public static int getTileOrRedstoneCurrentStrength(World arg, int arg0, int arg1, int arg2, int arg3, int arg4) {
        int arg5 = arg.getTypeId(arg0, arg1, arg2);
        if (arg5 == 0) {
            return -1;
        }
        if (arg5 == Block.REDSTONE_WIRE.id) {
            int arg8 = arg.getData(arg0, arg1, arg2);
            return arg8 > 0 ? arg8 : -1;
        }
        IRedPowerConnectable arg6 = (IRedPowerConnectable) CoreLib.getTileEntity((IBlockAccess) arg, arg0, arg1, arg2, IRedPowerConnectable.class);
        if (arg6 == null) {
            return -1;
        }
        if (arg6 instanceof IRedPowerWiring) {
            IRedPowerWiring arg7 = (IRedPowerWiring) arg6;
            return arg7.getCurrentStrength(arg3, arg4);
        }
        return (arg6.getPoweringMask(arg4) & arg3) <= 0 ? -1 : 255;
    }

    public static boolean isBlockRedstone(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3) {
        switch (arg3) {
            case 0: {
                --arg1;
                break;
            }
            case 1: {
                ++arg1;
                break;
            }
            case 2: {
                --arg2;
                break;
            }
            case 3: {
                ++arg2;
                break;
            }
            case 4: {
                --arg0;
                break;
            }
            case 5: {
                ++arg0;
            }
        }
        int arg4 = arg.getTypeId(arg0, arg1, arg2);
        if (arg4 == Block.REDSTONE_WIRE.id) {
            return true;
        }
        return false;
    }

    public static boolean isCompatible(int arg, int arg0) {
        if (arg != arg0 && !powerClassMapping.contains(Arrays.asList(arg, arg0))) {
            return false;
        }
        return true;
    }

    public static boolean isPowered(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3, int arg4) {
        return (arg3 & 17895680) > 0 && RedPowerLib.isWeakPoweringTo(arg, arg0, arg1 - 1, arg2, 0) ? true
                : ((arg3 & 35791360) > 0 && RedPowerLib.isWeakPoweringTo(arg, arg0, arg1 + 1, arg2, 1) ? true
                : ((arg3 & 71565329) > 0 && RedPowerLib.isWeakPoweringTo(arg, arg0, arg1, arg2 - 1, 2) ? true
                : ((arg3 & 143130658) > 0 && RedPowerLib.isWeakPoweringTo(arg, arg0, arg1, arg2 + 1, 3) ? true
                : ((arg3 & 268452932) > 0 && RedPowerLib.isWeakPoweringTo(arg, arg0 - 1, arg1, arg2, 4) ? true
                : ((arg3 & 536905864) > 0 && RedPowerLib.isWeakPoweringTo(arg, arg0 + 1, arg1, arg2, 5) ? true
                : ((arg4 & 1) > 0 && RedPowerLib.isPoweringTo(arg, arg0, arg1 - 1, arg2, 0) ? true : ((arg4 & 2) > 0 && RedPowerLib.isPoweringTo(arg, arg0, arg1 + 1, arg2, 1) ? true : ((arg4 & 4) > 0 && RedPowerLib.isPoweringTo(arg, arg0, arg1, arg2 - 1, 2) ? true : ((arg4 & 8) > 0 && RedPowerLib.isPoweringTo(arg, arg0, arg1, arg2 + 1, 3) ? true : ((arg4 & 16) > 0 && RedPowerLib.isPoweringTo(arg, arg0 - 1, arg1, arg2, 4) ? true : (arg4 & 32) > 0 && RedPowerLib.isPoweringTo(arg, arg0 + 1, arg1, arg2, 5)))))))))));
    }

    public static boolean isPoweringTo(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3) {
        int arg4 = arg.getTypeId(arg0, arg1, arg2);
        if (arg4 == 0) {
            return false;
        }
        if (Block.byId[arg4].a(arg, arg0, arg1, arg2, arg3)) {
            return true;
        }
        if (arg.e(arg0, arg1, arg2) && RedPowerLib.isStrongPowered(arg, arg0, arg1, arg2, arg3)) {
            return true;
        }
        if (arg3 > 1 && arg4 == Block.REDSTONE_WIRE.id) {
            if (searching) {
                return false;
            }
            if (Block.byId[arg4].a(arg, arg0, arg1, arg2, 1)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSearching() {
        return searching;
    }

    public static boolean isSideNormal(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3) {
        switch (arg3) {
            case 0: {
                --arg1;
                break;
            }
            case 1: {
                ++arg1;
                break;
            }
            case 2: {
                --arg2;
                break;
            }
            case 3: {
                ++arg2;
                break;
            }
            case 4: {
                --arg0;
                break;
            }
            case 5: {
                ++arg0;
            }
        }
        arg3 ^= 1;
        if (arg.e(arg0, arg1, arg2)) {
            return true;
        }
        arg.getTypeId(arg0, arg1, arg2);
        IMultipart arg5 = (IMultipart) CoreLib.getTileEntity(arg, arg0, arg1, arg2, IMultipart.class);
        return arg5 == null ? false : arg5.isSideNormal(arg3);
    }

    public static boolean isStrongPowered(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3) {
        return arg3 != 1 && RedPowerLib.isStrongPoweringTo(arg, arg0, arg1 - 1, arg2, 0) ? true : (arg3 != 0 && RedPowerLib.isStrongPoweringTo(arg, arg0, arg1 + 1, arg2, 1) ? true : (arg3 != 3 && RedPowerLib.isStrongPoweringTo(arg, arg0, arg1, arg2 - 1, 2) ? true : (arg3 != 2 && RedPowerLib.isStrongPoweringTo(arg, arg0, arg1, arg2 + 1, 3) ? true : (arg3 != 5 && RedPowerLib.isStrongPoweringTo(arg, arg0 - 1, arg1, arg2, 4) ? true : arg3 != 4 && RedPowerLib.isStrongPoweringTo(arg, arg0 + 1, arg1, arg2, 5)))));
    }

    public static boolean isStrongPoweringTo(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3) {
        int arg4 = arg.getTypeId(arg0, arg1, arg2);
        if (arg4 == 0) {
            return false;
        }
        if (searching && arg4 == Block.REDSTONE_WIRE.id) {
            return false;
        }
        if (!(arg instanceof World)) {
            return false;
        }
        World arg5 = (World) arg;
        return Block.byId[arg4].d(arg5, arg0, arg1, arg2, arg3);
    }

    public static boolean isWeakPoweringTo(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3) {
        int arg4 = arg.getTypeId(arg0, arg1, arg2);
        return arg4 == 0 ? false : (searching && arg4 == Block.REDSTONE_WIRE.id ? false : (Block.byId[arg4].a(arg, arg0, arg1, arg2, arg3) ? true : arg3 > 1 && arg4 == Block.REDSTONE_WIRE.id && Block.byId[arg4].a(arg, arg0, arg1, arg2, 1)));
    }

    public static int mapConToLocal(int arg, int arg0) {
        arg >>= arg0 * 4;
        arg &= 15;
        switch (arg0) {
            case 0: {
                return arg;
            }
            case 1: {
                arg ^= ((arg ^ arg >> 1) & 1) * 3;
                return arg;
            }
            default: {
                arg ^= ((arg ^ arg >> 2) & 3) * 5;
                return arg;
            }
            case 3:
            case 4:
        }
        arg ^= ((arg ^ arg >> 2) & 3) * 5;
        arg ^= ((arg ^ arg >> 1) & 1) * 3;
        return arg;
    }

    public static int mapConToRot(int arg, int arg0) {
        return RedPowerLib.mapLocalToRot(RedPowerLib.mapConToLocal(arg, arg0 >> 2), arg0 & 3);
    }

    public static int mapLocalToCon(int arg, int arg0) {
        switch (arg0) {
            case 0: {
                break;
            }
            case 1: {
                arg ^= ((arg ^ arg >> 1) & 1) * 3;
                break;
            }
            default: {
                arg ^= ((arg ^ arg >> 2) & 3) * 5;
                break;
            }
            case 3:
            case 4: {
                arg ^= ((arg ^ arg >> 1) & 1) * 3;
                arg ^= ((arg ^ arg >> 2) & 3) * 5;
            }
        }
        return arg << arg0 * 4;
    }

    public static int mapLocalToRot(int arg, int arg0) {
        arg = arg & 8 | (arg & 6) >> 1 | arg << 2 & 4;
        arg = arg << 4 - arg0 | arg >> arg0;
        return arg & 15;
    }

    public static int mapRotToCon(int arg, int arg0) {
        return RedPowerLib.mapLocalToCon(RedPowerLib.mapRotToLocal(arg, arg0 & 3), arg0 >> 2);
    }

    public static int mapRotToLocal(int arg, int arg0) {
        arg = arg << arg0 | arg >> 4 - arg0;
        return arg & 8 | (arg & 3) << 1 | (arg &= 15) >> 2 & 1;
    }

    public static void notifyBlock(World arg, int arg0, int arg1, int arg2, int arg3) {
        Block arg4 = Block.byId[arg.getTypeId(arg0, arg1, arg2)];
        if (arg4 != null) {
            try {
                arg4.doPhysics(arg, arg0, arg1, arg2, arg3);
            } catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public static int updateBlockCurrentStrength(World arg, IRedPowerWiring arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        int arg6 = arg0.getConnectionMask() & arg4;
        int arg7 = arg0.getExtConnectionMask() & arg4;
        int arg8 = -1;
        int arg9 = 0;
        int arg10 = 0;
        int arg11 = arg5;
        while (arg11 > 0) {
            int arg12 = Integer.numberOfTrailingZeros(arg11);
            arg11 &= ~(1 << arg12);
            arg10 = Math.max(arg10, arg0.getCurrentStrength(arg4, arg12));
            arg8 = Math.max(arg8, RedPowerLib.getMaxCurrentStrength(arg, arg1, arg2, arg3, arg6, arg7, arg12));
            arg9 = Math.max(arg9, arg0.scanPoweringStrength(arg6 | arg7, arg12));
        }
        if (arg9 > arg10 || arg8 != arg10 + 1 && (arg10 != 0 || arg8 != 0)) {
            if (arg9 == arg10 && arg8 <= arg10) {
                return arg10;
            }
            if ((arg10 = Math.max(arg9, arg10)) >= arg8) {
                if (arg10 > arg9) {
                    arg10 = 0;
                }
            } else {
                arg10 = Math.max(0, arg8 - 1);
            }
            if ((arg5 & 1) > 0) {
                RedPowerLib.addUpdateBlock(arg1, arg2, arg3);
            }
            RedPowerLib.addSearchBlocks(arg1, arg2, arg3, arg6, arg7);
            return arg10;
        }
        return arg10;
    }

    public static void updateCurrent(World arg, int arg0, int arg1, int arg2) {
        RedPowerLib.addStartSearchBlock(arg0, arg1, arg2);
        try {
            if (!searching) {
                searching = true;
                while (powerSearch.size() > 0) {
                    List arg3 = (List) powerSearch.remove(0);
                    powerSearchTest.remove(arg3);
                    Integer[] arg4 = (Integer[]) arg3.toArray();
                    IRedPowerWiring arg5 = (IRedPowerWiring) CoreLib.getTileEntity((IBlockAccess) arg, arg4[0], arg4[1], arg4[2], IRedPowerWiring.class);
                    if (arg5 == null)
                        continue;
                    arg5.updateCurrentStrength();
                }
                searching = false;
                List arg6 = Collections.synchronizedList(new LinkedList(blockUpdates));
                blockUpdates.clear();
                int arg7 = 0;
                while (arg7 < arg6.size()) {
                    Integer[] arg8 = (Integer[]) ((List) arg6.get(arg7)).toArray();
                    RedPowerLib.notifyBlock(arg, arg8[0], arg8[1], arg8[2], Block.REDSTONE_WIRE.id);
                    arg.notify(arg8[0].intValue(), arg8[1].intValue(), arg8[2].intValue());
                    ++arg7;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateIndirectNeighbors(World arg, int arg0, int arg1, int arg2, int arg3) {
        if (!arg.suppressPhysics && !CoreProxy.isClient(arg)) {
            int arg4 = -3;
            while (arg4 <= 3) {
                int arg5 = -3;
                while (arg5 <= 3) {
                    int arg6 = -3;
                    while (arg6 <= 3) {
                        int arg7 = arg4 >= 0 ? arg4 : -arg4;
                        arg7 += arg5 >= 0 ? arg5 : -arg5;
                        if ((arg7 += arg6 >= 0 ? arg6 : -arg6) <= 3) {
                            RedPowerLib.notifyBlock(arg, arg0 + arg4, arg1 + arg5, arg2 + arg6, arg3);
                        }
                        ++arg6;
                    }
                    ++arg5;
                }
                ++arg4;
            }
        }
    }
}
