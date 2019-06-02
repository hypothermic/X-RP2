/* X-RP - decompiled with CFR */
package eloraam.core;

import forge.ITextureProvider;
import net.minecraft.server.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CoverLib {

    public static final float selectBoxWidth = 0.25f;
    public static Block blockCoverPlate = null;
    private static ItemStack[] materials = new ItemStack[256];
    private static String[] names = new String[256];
    private static String[] descs = new String[256];
    private static int[] hardness = new int[256];
    private static ArrayList<IMaterialHandler> materialHandlers = new ArrayList();
    private static boolean[] transparency = new boolean[256];
    public static int[][] coverTextures = new int[256][];
    public static String[] coverTextureFiles = new String[256];
    private static HashMap coverIndex = new HashMap();

    private CoverLib() {
    }

    public static void addMaterialHandler(IMaterialHandler iMaterialHandler) {
        for (int i = 0; i < 256; ++i) {
            if (materials[i] == null)
                continue;
            iMaterialHandler.addMaterial(i);
        }
        materialHandlers.add(iMaterialHandler);
    }

    public static Integer getMaterial(ItemStack itemStack) {
        return (Integer) coverIndex.get(Arrays.asList(itemStack.id, itemStack.getData()));
    }

    public static void addMaterial(int n, int n2, Block block, String string, String string2) {
        CoverLib.addMaterial(n, n2, false, block, 0, string, string2);
    }

    public static void addMaterial(int n, int n2, Block block, int n3, String string, String string2) {
        CoverLib.addMaterial(n, n2, false, block, n3, string, string2);
    }

    public static void addMaterial(int n, int n2, boolean bl, Block block, String string, String string2) {
        CoverLib.addMaterial(n, n2, bl, block, 0, string, string2);
    }

    public static void addMaterial(int n, int n2, boolean bl, Block block, int n3, String string, String string2) {
        ItemStack itemStack = new ItemStack(block, 1, n3);
        CoverLib.coverTextures[n] = new int[6];
        for (int i = 0; i < 6; ++i) {
            CoverLib.coverTextures[n][i] = block.a(i, n3);
        }
        if (block instanceof ITextureProvider) {
            ITextureProvider iTextureProvider = (ITextureProvider) block;
            CoverLib.coverTextureFiles[n] = iTextureProvider.getTextureFile();
        }
        CoverLib.materials[n] = itemStack;
        CoverLib.names[n] = string;
        CoverLib.descs[n] = string2;
        CoverLib.hardness[n] = n2;
        CoverLib.transparency[n] = bl;
        coverIndex.put(Arrays.asList(block.id, n3), n);
        for (IMaterialHandler iMaterialHandler : materialHandlers) {
            iMaterialHandler.addMaterial(n);
        }
        Config.addName("tile.rpcover." + string + ".name", string2 + " Cover");
        Config.addName("tile.rppanel." + string + ".name", string2 + " Panel");
        Config.addName("tile.rpslab." + string + ".name", string2 + " Slab");
        Config.addName("tile.rphcover." + string + ".name", "Hollow " + string2 + " Cover");
        Config.addName("tile.rphpanel." + string + ".name", "Hollow " + string2 + " Panel");
        Config.addName("tile.rphslab." + string + ".name", "Hollow " + string2 + " Slab");
        Config.addName("tile.rpcovc." + string + ".name", string2 + " Cover Corner");
        Config.addName("tile.rppanc." + string + ".name", string2 + " Panel Corner");
        Config.addName("tile.rpslabc." + string + ".name", string2 + " Slab Corner");
        Config.addName("tile.rpcovs." + string + ".name", string2 + " Cover Strip");
        Config.addName("tile.rppans." + string + ".name", string2 + " Panel Strip");
        Config.addName("tile.rpslabs." + string + ".name", string2 + " Slab Strip");
        Config.addName("tile.rpcov3." + string + ".name", string2 + " Triple Cover");
        Config.addName("tile.rpcov5." + string + ".name", string2 + " Cover Slab");
        Config.addName("tile.rpcov6." + string + ".name", string2 + " Triple Panel");
        Config.addName("tile.rpcov7." + string + ".name", string2 + " Anticover");
        Config.addName("tile.rphcov3." + string + ".name", string2 + " Hollow Triple Cover");
        Config.addName("tile.rphcov5." + string + ".name", string2 + " Hollow Cover Slab");
        Config.addName("tile.rphcov6." + string + ".name", string2 + " Hollow Triple Panel");
        Config.addName("tile.rphcov7." + string + ".name", string2 + " Hollow Anticover");
        Config.addName("tile.rpcov3c." + string + ".name", string2 + " Triple Cover Corner");
        Config.addName("tile.rpcov5c." + string + ".name", string2 + " Cover Slab Corner");
        Config.addName("tile.rpcov6c." + string + ".name", string2 + " Triple Panel Corner");
        Config.addName("tile.rpcov7c." + string + ".name", string2 + " Anticover Corner");
        Config.addName("tile.rpcov3s." + string + ".name", string2 + " Triple Cover Strip");
        Config.addName("tile.rpcov5s." + string + ".name", string2 + " Cover Slab Strip");
        Config.addName("tile.rpcov6s." + string + ".name", string2 + " Triple Panel Strip");
        Config.addName("tile.rpcov7s." + string + ".name", string2 + " Anticover Strip");
        Config.addName("tile.rppole1." + string + ".name", string2 + " Post");
        Config.addName("tile.rppole2." + string + ".name", string2 + " Pillar");
        Config.addName("tile.rppole3." + string + ".name", string2 + " Column");
    }

    public static void addSaw(Item item, int n) {
        CraftLib.addDamageOnCraft(item);
    }

    public static int damageToCoverData(int n) {
        int n2 = n >> 8;
        int n3 = n & 255;
        switch (n2) {
            case 0: {
                n3 |= 65536;
                break;
            }
            case 16: {
                n3 |= 131328;
                break;
            }
            case 17: {
                n3 |= 262656;
                break;
            }
            case 24: {
                n3 |= 1114880;
                break;
            }
            case 25: {
                n3 |= 1180672;
                break;
            }
            case 26: {
                n3 |= 1312000;
                break;
            }
            case 27: {
                n3 |= 198144;
                break;
            }
            case 28: {
                n3 |= 329472;
                break;
            }
            case 29: {
                n3 |= 395264;
                break;
            }
            case 30: {
                n3 |= 461056;
                break;
            }
            case 31: {
                n3 |= 1247744;
                break;
            }
            case 32: {
                n3 |= 1379072;
                break;
            }
            case 33: {
                n3 |= 1444864;
                break;
            }
            case 34: {
                n3 |= 1510656;
                break;
            }
            case 18: {
                n3 |= 33619968;
                break;
            }
            case 19: {
                n3 |= 33685760;
                break;
            }
            case 20: {
                n3 |= 33817088;
                break;
            }
            case 35: {
                n3 |= 33751808;
                break;
            }
            case 36: {
                n3 |= 33883136;
                break;
            }
            case 37: {
                n3 |= 33948928;
                break;
            }
            case 38: {
                n3 |= 34014720;
                break;
            }
            case 21: {
                n3 |= 16842752;
                break;
            }
            case 22: {
                n3 |= 16908544;
                break;
            }
            case 23: {
                n3 |= 17039872;
                break;
            }
            case 39: {
                n3 |= 16974592;
                break;
            }
            case 40: {
                n3 |= 17105920;
                break;
            }
            case 41: {
                n3 |= 17171712;
                break;
            }
            case 42: {
                n3 |= 17237504;
                break;
            }
            case 43: {
                n3 |= 50462720;
                break;
            }
            case 44: {
                n3 |= 50594048;
                break;
            }
            case 45: {
                n3 |= 50725376;
            }
        }
        return n3;
    }

    public static int damageToCoverValue(int n) {
        return CoverLib.damageToCoverData(n) & 65535;
    }

    public static int coverValueToDamage(int n, int n2) {
        int n3 = n2 >> 8;
        int n4 = n2 & 255;
        if (n < 6) {
            switch (n3) {
                case 1: {
                    n4 |= 4096;
                    break;
                }
                case 2: {
                    n4 |= 4352;
                    break;
                }
                case 3: {
                    n4 |= 6144;
                    break;
                }
                case 4: {
                    n4 |= 6400;
                    break;
                }
                case 5: {
                    n4 |= 6656;
                    break;
                }
                case 6: {
                    n4 |= 6912;
                    break;
                }
                case 7: {
                    n4 |= 7168;
                    break;
                }
                case 8: {
                    n4 |= 7424;
                    break;
                }
                case 9: {
                    n4 |= 7680;
                    break;
                }
                case 10: {
                    n4 |= 7936;
                    break;
                }
                case 11: {
                    n4 |= 8192;
                    break;
                }
                case 12: {
                    n4 |= 8448;
                    break;
                }
                case 13: {
                    n4 |= 8704;
                }
            }
        } else if (n < 14) {
            switch (n3) {
                case 0: {
                    n4 |= 4608;
                    break;
                }
                case 1: {
                    n4 |= 4864;
                    break;
                }
                case 2: {
                    n4 |= 5120;
                    break;
                }
                case 3: {
                    n4 |= 8960;
                    break;
                }
                case 4: {
                    n4 |= 9216;
                    break;
                }
                case 5: {
                    n4 |= 9472;
                    break;
                }
                case 6: {
                    n4 |= 9728;
                }
            }
        } else if (n < 26) {
            switch (n3) {
                case 0: {
                    n4 |= 5376;
                    break;
                }
                case 1: {
                    n4 |= 5632;
                    break;
                }
                case 2: {
                    n4 |= 5888;
                    break;
                }
                case 3: {
                    n4 |= 9984;
                    break;
                }
                case 4: {
                    n4 |= 10240;
                    break;
                }
                case 5: {
                    n4 |= 10496;
                    break;
                }
                case 6: {
                    n4 |= 10752;
                }
            }
        } else if (n < 29) {
            switch (n3) {
                case 0: {
                    n4 |= 11008;
                    break;
                }
                case 1: {
                    n4 |= 11264;
                    break;
                }
                case 2: {
                    n4 |= 11520;
                }
            }
        }
        return n4;
    }

    public static ItemStack convertCoverPlate(int n, int n2) {
        if (blockCoverPlate == null) {
            return null;
        }
        return new ItemStack(blockCoverPlate, 1, CoverLib.coverValueToDamage(n, n2));
    }

    public static int cornerToCoverMask(int n) {
        switch (n) {
            case 0: {
                return 21;
            }
            case 1: {
                return 25;
            }
            case 2: {
                return 37;
            }
            case 3: {
                return 41;
            }
            case 4: {
                return 22;
            }
            case 5: {
                return 26;
            }
            case 6: {
                return 38;
            }
        }
        return 42;
    }

    public static int coverToCornerMask(int n) {
        switch (n) {
            case 0: {
                return 15;
            }
            case 1: {
                return 240;
            }
            case 2: {
                return 85;
            }
            case 3: {
                return 170;
            }
            case 4: {
                return 51;
            }
        }
        return 204;
    }

    public static int coverToStripMask(int n) {
        switch (n) {
            case 0: {
                return 15;
            }
            case 1: {
                return 3840;
            }
            case 2: {
                return 337;
            }
            case 3: {
                return 674;
            }
            case 4: {
                return 1076;
            }
        }
        return 2248;
    }

    public static int stripToCornerMask(int n) {
        switch (n) {
            case 0: {
                return 5;
            }
            case 1: {
                return 10;
            }
            case 2: {
                return 3;
            }
            case 3: {
                return 12;
            }
            case 4: {
                return 17;
            }
            case 5: {
                return 34;
            }
            case 6: {
                return 68;
            }
            case 7: {
                return 136;
            }
            case 8: {
                return 80;
            }
            case 9: {
                return 160;
            }
            case 10: {
                return 48;
            }
        }
        return 192;
    }

    public static int stripToCoverMask(int n) {
        switch (n) {
            case 0: {
                return 5;
            }
            case 1: {
                return 9;
            }
            case 2: {
                return 17;
            }
            case 3: {
                return 33;
            }
            case 4: {
                return 20;
            }
            case 5: {
                return 24;
            }
            case 6: {
                return 36;
            }
            case 7: {
                return 40;
            }
            case 8: {
                return 6;
            }
            case 9: {
                return 10;
            }
            case 10: {
                return 18;
            }
        }
        return 34;
    }

    public static float getThickness(int n, int n2) {
        if (n < 6) {
            switch (n2 >> 8) {
                case 0: {
                    return 0.125f;
                }
                case 1: {
                    return 0.25f;
                }
                case 2: {
                    return 0.5f;
                }
                case 3: {
                    return 0.125f;
                }
                case 4: {
                    return 0.25f;
                }
                case 5: {
                    return 0.5f;
                }
                case 6: {
                    return 0.375f;
                }
                case 7: {
                    return 0.625f;
                }
                case 8: {
                    return 0.75f;
                }
                case 9: {
                    return 0.875f;
                }
                case 10: {
                    return 0.375f;
                }
                case 11: {
                    return 0.625f;
                }
                case 12: {
                    return 0.75f;
                }
                case 13: {
                    return 0.875f;
                }
            }
            return 1.0f;
        }
        if (n >= 26 && n < 29) {
            switch (n2 >> 8) {
                case 0: {
                    return 0.125f;
                }
                case 1: {
                    return 0.25f;
                }
                case 2: {
                    return 0.375f;
                }
            }
        }
        switch (n2 >> 8) {
            case 0: {
                return 0.125f;
            }
            case 1: {
                return 0.25f;
            }
            case 2: {
                return 0.5f;
            }
            case 3: {
                return 0.375f;
            }
            case 4: {
                return 0.625f;
            }
            case 5: {
                return 0.75f;
            }
            case 6: {
                return 0.875f;
            }
        }
        return 1.0f;
    }

    public static int getThicknessQuanta(int n, int n2) {
        if (n < 6) {
            switch (n2 >> 8) {
                case 0: {
                    return 1;
                }
                case 1: {
                    return 2;
                }
                case 2: {
                    return 4;
                }
                case 3: {
                    return 1;
                }
                case 4: {
                    return 2;
                }
                case 5: {
                    return 4;
                }
                case 6: {
                    return 3;
                }
                case 7: {
                    return 5;
                }
                case 8: {
                    return 6;
                }
                case 9: {
                    return 7;
                }
                case 10: {
                    return 3;
                }
                case 11: {
                    return 5;
                }
                case 12: {
                    return 6;
                }
                case 13: {
                    return 7;
                }
            }
            return 0;
        }
        if (n >= 26 && n < 29) {
            switch (n2 >> 8) {
                case 0: {
                    return 1;
                }
                case 1: {
                    return 2;
                }
                case 2: {
                    return 3;
                }
            }
        }
        switch (n2 >> 8) {
            case 0: {
                return 1;
            }
            case 1: {
                return 2;
            }
            case 2: {
                return 4;
            }
            case 3: {
                return 3;
            }
            case 4: {
                return 5;
            }
            case 5: {
                return 6;
            }
            case 6: {
                return 7;
            }
        }
        return 0;
    }

    public static boolean checkPlacement(int n, short[] arrs, int n2, boolean bl) {
        boolean bl2 = false;
        boolean bl3 = false;
        PlacementValidator placementValidator = new PlacementValidator(n, arrs);
        return placementValidator.checkPlacement(n2, bl);
    }

    private static boolean canAddCover(World world, MovingObjectPosition movingObjectPosition, int n) {
        if (world.mayPlace(CoverLib.blockCoverPlate.id, movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d, false, movingObjectPosition.face)) {
            return true;
        }
        ICoverable iCoverable = (ICoverable) CoreLib.getTileEntity((IBlockAccess) world, movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d, ICoverable.class);
        if (iCoverable == null) {
            return false;
        }
        return iCoverable.canAddCover(movingObjectPosition.subHit, n);
    }

    private static int extractCoverSide(MovingObjectPosition movingObjectPosition) {
        int n = 0;
        double d = movingObjectPosition.pos.a - (double) movingObjectPosition.b - 0.5;
        double d2 = movingObjectPosition.pos.b - (double) movingObjectPosition.c - 0.5;
        double d3 = movingObjectPosition.pos.c - (double) movingObjectPosition.d - 0.5;
        float f = 0.25f;
        switch (movingObjectPosition.face) {
            case 0:
            case 1: {
                if (d3 > (double) (-f) && d3 < (double) f && d > (double) (-f) && d < (double) f) {
                    return movingObjectPosition.face;
                }
                if (d3 > d) {
                    return d3 <= -d ? 4 : 3;
                }
                return d3 <= -d ? 2 : 5;
            }
            case 2:
            case 3: {
                if (d2 > (double) (-f) && d2 < (double) f && d > (double) (-f) && d < (double) f) {
                    return movingObjectPosition.face;
                }
                if (d2 > d) {
                    return d2 <= -d ? 4 : 1;
                }
                return d2 <= -d ? 0 : 5;
            }
            case 4:
            case 5: {
                if (d2 > (double) (-f) && d2 < (double) f && d3 > (double) (-f) && d3 < (double) f) {
                    return movingObjectPosition.face;
                }
                if (d2 > d3) {
                    return d2 <= -d3 ? 2 : 1;
                }
                return d2 <= -d3 ? 0 : 3;
            }
        }
        return n;
    }

    private static int extractCoverAxis(MovingObjectPosition movingObjectPosition) {
        switch (movingObjectPosition.face) {
            case 0:
            case 1: {
                return movingObjectPosition.pos.b - (double) movingObjectPosition.c <= 0.5 ? 0 : 1;
            }
            case 2:
            case 3: {
                return movingObjectPosition.pos.c - (double) movingObjectPosition.d <= 0.5 ? 0 : 1;
            }
        }
        return movingObjectPosition.pos.a - (double) movingObjectPosition.b <= 0.5 ? 0 : 1;
    }

    private static void stepDir(MovingObjectPosition movingObjectPosition) {
        switch (movingObjectPosition.face) {
            case 0: {
                --movingObjectPosition.c;
                break;
            }
            case 1: {
                ++movingObjectPosition.c;
                break;
            }
            case 2: {
                --movingObjectPosition.d;
                break;
            }
            case 3: {
                ++movingObjectPosition.d;
                break;
            }
            case 4: {
                --movingObjectPosition.b;
                break;
            }
            default: {
                ++movingObjectPosition.b;
            }
        }
    }

    private static boolean isClickOutside(MovingObjectPosition movingObjectPosition) {
        if (movingObjectPosition.subHit < 0) {
            return true;
        }
        if (movingObjectPosition.subHit < 6) {
            return movingObjectPosition.face != (movingObjectPosition.subHit ^ 1);
        }
        if (movingObjectPosition.subHit < 14) {
            int n = movingObjectPosition.subHit - 6;
            return ((movingObjectPosition.face ^ (n = n >> 2 | (n & 3) << 1) >> (movingObjectPosition.face >> 1)) & 1) == 0;
        }
        if (movingObjectPosition.subHit < 26) {
            int n = movingObjectPosition.subHit - 14;
            return ((n = CoverLib.stripToCoverMask(n)) & 1 << (movingObjectPosition.face ^ 1)) <= 0;
        }
        if (movingObjectPosition.subHit < 29) {
            return true;
        }
        return movingObjectPosition.subHit == 29;
    }

    public static MovingObjectPosition getPlacement(World world, MovingObjectPosition movingObjectPosition, int n) {
        MovingObjectPosition movingObjectPosition2 = new MovingObjectPosition(movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d, movingObjectPosition.face, movingObjectPosition.pos);
        int n2 = CoverLib.damageToCoverValue(n);
        switch (n >> 8) {
            case 0:
            case 16:
            case 17:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34: {
                int n3 = CoverLib.extractCoverSide(movingObjectPosition);
                if (n3 != movingObjectPosition2.face) {
                    movingObjectPosition2.subHit = n3;
                    if (!CoverLib.isClickOutside(movingObjectPosition) && CoverLib.canAddCover(world, movingObjectPosition2, n2)) {
                        return movingObjectPosition2;
                    }
                    CoverLib.stepDir(movingObjectPosition2);
                    if (CoverLib.canAddCover(world, movingObjectPosition2, n2)) {
                        return movingObjectPosition2;
                    }
                    return null;
                }
                if (!CoverLib.isClickOutside(movingObjectPosition)) {
                    movingObjectPosition2.subHit = n3 ^ 1;
                    if (CoverLib.canAddCover(world, movingObjectPosition2, n2)) {
                        return movingObjectPosition2;
                    }
                }
                movingObjectPosition2.subHit = n3;
                if (CoverLib.canAddCover(world, movingObjectPosition2, n2)) {
                    return movingObjectPosition2;
                }
                if (!CoverLib.isClickOutside(movingObjectPosition)) {
                    return null;
                }
                CoverLib.stepDir(movingObjectPosition2);
                movingObjectPosition2.subHit = n3 ^ 1;
                if (CoverLib.canAddCover(world, movingObjectPosition2, n2)) {
                    return movingObjectPosition2;
                }
                return null;
            }
            case 21:
            case 22:
            case 23:
            case 39:
            case 40:
            case 41:
            case 42: {
                int n4 = CoverLib.extractCoverSide(movingObjectPosition);
                if (n4 == movingObjectPosition2.face) {
                    return null;
                }
                int n5 = CoverLib.coverToStripMask(n4);
                if (!CoverLib.isClickOutside(movingObjectPosition)) {
                    int n6 = n5 & CoverLib.coverToStripMask(movingObjectPosition2.face ^ 1);
                    movingObjectPosition2.subHit = 14 + Integer.numberOfTrailingZeros(n6);
                    if (CoverLib.canAddCover(world, movingObjectPosition2, n2)) {
                        return movingObjectPosition2;
                    }
                    n6 = n5 & CoverLib.coverToStripMask(movingObjectPosition2.face);
                    movingObjectPosition2.subHit = 14 + Integer.numberOfTrailingZeros(n6);
                    if (CoverLib.canAddCover(world, movingObjectPosition2, n2)) {
                        return movingObjectPosition2;
                    }
                    return null;
                }
                CoverLib.stepDir(movingObjectPosition2);
                int n7 = n5 & CoverLib.coverToStripMask(movingObjectPosition2.face ^ 1);
                movingObjectPosition2.subHit = 14 + Integer.numberOfTrailingZeros(n7);
                if (CoverLib.canAddCover(world, movingObjectPosition2, n2)) {
                    return movingObjectPosition2;
                }
                return null;
            }
            case 18:
            case 19:
            case 20:
            case 35:
            case 36:
            case 37:
            case 38: {
                double d = movingObjectPosition.pos.a - (double) movingObjectPosition.b;
                double d2 = movingObjectPosition.pos.b - (double) movingObjectPosition.c;
                double d3 = movingObjectPosition.pos.c - (double) movingObjectPosition.d;
                int n8 = 0;
                if (d3 > 0.5) {
                    ++n8;
                }
                if (d > 0.5) {
                    n8 += 2;
                }
                if (d2 > 0.5) {
                    n8 += 4;
                }
                switch (movingObjectPosition.face) {
                    case 0: {
                        n8 &= 3;
                        break;
                    }
                    case 1: {
                        n8 |= 4;
                        break;
                    }
                    case 2: {
                        n8 &= 6;
                        break;
                    }
                    case 3: {
                        n8 |= 1;
                        break;
                    }
                    case 4: {
                        n8 &= 5;
                        break;
                    }
                    default: {
                        n8 |= 2;
                    }
                }
                int n9 = n8;
                switch (movingObjectPosition.face) {
                    case 0:
                    case 1: {
                        n9 ^= 4;
                        break;
                    }
                    case 2:
                    case 3: {
                        n9 ^= 1;
                        break;
                    }
                    default: {
                        n9 ^= 2;
                    }
                }
                if (CoverLib.isClickOutside(movingObjectPosition)) {
                    movingObjectPosition2.subHit = n9 + 6;
                    CoverLib.stepDir(movingObjectPosition2);
                    if (CoverLib.canAddCover(world, movingObjectPosition2, n2)) {
                        return movingObjectPosition2;
                    }
                    return null;
                }
                movingObjectPosition2.subHit = n9 + 6;
                if (CoverLib.canAddCover(world, movingObjectPosition2, n2)) {
                    return movingObjectPosition2;
                }
                movingObjectPosition2.subHit = n8 + 6;
                if (CoverLib.canAddCover(world, movingObjectPosition2, n2)) {
                    return movingObjectPosition2;
                }
                return null;
            }
            case 43:
            case 44:
            case 45: {
                int n10 = CoverLib.extractCoverSide(movingObjectPosition);
                if (n10 != movingObjectPosition2.face && n10 != (movingObjectPosition2.face ^ 1)) {
                    return null;
                }
                if (CoverLib.isClickOutside(movingObjectPosition)) {
                    CoverLib.stepDir(movingObjectPosition2);
                }
                movingObjectPosition2.subHit = (n10 >> 1) + 26;
                if (CoverLib.canAddCover(world, movingObjectPosition2, n2)) {
                    return movingObjectPosition2;
                }
                return null;
            }
        }
        return null;
    }

    public static void replaceWithCovers(World world, int n, int n2, int n3, int n4, short[] arrs) {
        BlockMultipart.removeMultipart(world, n, n2, n3);
        if (blockCoverPlate == null) {
            return;
        }
        if (n4 == 0) {
            return;
        }
        world.setRawTypeIdAndData(n, n2, n3, CoverLib.blockCoverPlate.id, 0);
        TileCovered tileCovered = (TileCovered) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileCovered.class);
        if (tileCovered == null) {
            return;
        }
        tileCovered.CoverSides = n4;
        tileCovered.Covers = arrs;
        RedPowerLib.updateIndirectNeighbors(world, n, n2, n3, CoverLib.blockCoverPlate.id);
    }

    public static boolean tryMakeCompatible(World world, WorldCoord worldCoord, int n, int n2) {
        TileCovered tileCovered = (TileCovered) CoreLib.getTileEntity((IBlockAccess) world, worldCoord, TileCovered.class);
        if (tileCovered == null) {
            return false;
        }
        int n3 = n2 >> 8;
        int n4 = n2 & 255;
        int n5 = tileCovered.getExtendedID();
        if (n5 == n3) {
            return tileCovered.getExtendedMetadata() == n4;
        }
        if (n5 != 0) {
            return false;
        }
        short[] arrs = tileCovered.Covers;
        int n6 = tileCovered.CoverSides;
        BlockMultipart.removeMultipart(world, worldCoord.x, worldCoord.y, worldCoord.z);
        if (!world.setRawTypeIdAndData(worldCoord.x, worldCoord.y, worldCoord.z, n, n3)) {
            return false;
        }
        tileCovered = (TileCovered) CoreLib.getTileEntity((IBlockAccess) world, worldCoord, TileCovered.class);
        if (tileCovered == null) {
            return true;
        }
        tileCovered.Covers = arrs;
        tileCovered.CoverSides = n6;
        tileCovered.setExtendedMetadata(n4);
        return true;
    }

    public static ItemStack getItemStack(int n) {
        return materials[n];
    }

    public static Block getBlock(int n) {
        ItemStack itemStack = materials[n];
        return Block.byId[itemStack.id];
    }

    public static String getName(int n) {
        return names[n];
    }

    public static String getDesc(int n) {
        return descs[n];
    }

    public static int getHardness(int n) {
        return hardness[n];
    }

    public static boolean isTransparent(int n) {
        return transparency[n];
    }

    public static interface IMaterialHandler {

        public void addMaterial(int var1);
    }

    private static class PlacementValidator {

        public int sidemask = 0;
        public int cornermask = 0;
        public int fillcornermask = 0;
        public int hollowcornermask = 0;
        public int thickfaces = 0;
        public int covm;
        public short[] covs;
        public int[] quanta = new int[29];

        public boolean checkThickFace(int n) {
            for (int i = 0; i < 6; ++i) {
                if ((this.covm & 1 << i) == 0 || this.covs[i] >> 8 != n)
                    continue;
                int n2 = CoverLib.coverToCornerMask(i);
                if ((this.fillcornermask & n2) > 0) {
                    return false;
                }
                this.fillcornermask |= n2;
                this.sidemask |= CoverLib.coverToStripMask(i);
            }
            return true;
        }

        public boolean checkThickSide(int n) {
            for (int i = 0; i < 12; ++i) {
                if ((this.covm & 1 << i + 14) == 0 || this.covs[i + 14] >> 8 != n)
                    continue;
                int n2 = CoverLib.stripToCornerMask(i);
                if ((this.fillcornermask & n2) > 0) {
                    return false;
                }
                this.fillcornermask |= n2;
                this.sidemask |= 1 << i;
            }
            return true;
        }

        public boolean checkThickCorner(int n) {
            for (int i = 0; i < 8; ++i) {
                if ((this.covm & 1 << i + 6) == 0 || this.covs[i + 6] >> 8 != n)
                    continue;
                int n2 = 1 << i;
                if ((this.fillcornermask & n2) == n2) {
                    return false;
                }
                this.fillcornermask |= n2;
            }
            return true;
        }

        public boolean checkFace(int n) {
            for (int i = 0; i < 6; ++i) {
                if ((this.covm & 1 << i) == 0 || this.covs[i] >> 8 != n)
                    continue;
                int n2 = CoverLib.coverToCornerMask(i);
                if ((this.fillcornermask & n2) == n2) {
                    return false;
                }
                this.cornermask |= n2;
                this.sidemask |= CoverLib.coverToStripMask(i);
            }
            return true;
        }

        public boolean checkSide(int n) {
            for (int i = 0; i < 12; ++i) {
                if ((this.covm & 1 << i + 14) == 0 || this.covs[i + 14] >> 8 != n)
                    continue;
                int n2 = CoverLib.stripToCornerMask(i);
                if ((this.fillcornermask & n2) == n2) {
                    return false;
                }
                if ((this.sidemask & 1 << i) > 0) {
                    return false;
                }
                this.cornermask |= n2;
                this.sidemask |= 1 << i;
            }
            return true;
        }

        public boolean checkCorner(int n) {
            for (int i = 0; i < 8; ++i) {
                if ((this.covm & 1 << i + 6) == 0 || this.covs[i + 6] >> 8 != n)
                    continue;
                int n2 = 1 << i;
                if ((this.cornermask & n2) == n2) {
                    return false;
                }
                this.cornermask |= n2;
            }
            return true;
        }

        public boolean checkHollow(int n) {
            for (int i = 0; i < 6; ++i) {
                if ((this.covm & 1 << i) == 0 || this.covs[i] >> 8 != n)
                    continue;
                int n2 = CoverLib.coverToCornerMask(i);
                if ((this.cornermask & n2) > 0) {
                    return false;
                }
                this.cornermask |= n2;
                this.hollowcornermask |= n2;
                n2 = CoverLib.coverToStripMask(i);
                if ((this.sidemask & n2) > 0) {
                    return false;
                }
                this.sidemask |= n2;
            }
            return true;
        }

        public boolean checkHollowCover(int n) {
            int n2 = 0;
            int n3 = 0;
            for (int i = 0; i < 6; ++i) {
                if ((this.covm & 1 << i) == 0 || this.covs[i] >> 8 != n)
                    continue;
                int n4 = CoverLib.coverToCornerMask(i);
                if ((this.cornermask & n4) > 0) {
                    return false;
                }
                n2 |= n4;
                n4 = CoverLib.coverToStripMask(i);
                if ((this.sidemask & n4) > 0) {
                    return false;
                }
                n3 |= n4;
            }
            this.cornermask |= n2;
            this.sidemask |= n3;
            return true;
        }

        public void calcQuanta() {
            for (int i = 0; i < 29; ++i) {
                this.quanta[i] = (this.covm & 1 << i) == 0 ? 0 : CoverLib.getThicknessQuanta(i, this.covs[i]);
            }
        }

        private boolean checkOverlap(int n, int n2, int n3, int n4) {
            n = this.quanta[n];
            n2 = this.quanta[n2];
            n3 = this.quanta[n3];
            n4 = this.quanta[n4];
            return n + n2 > 8 || n + n3 > 8 || n + n4 > 8 || n2 + n3 > 8 || n2 + n4 > 8 || n3 + n4 > 8;
        }

        public boolean checkImpingement() {
            int n;
            int n2;
            int n3;
            int n4;
            int n5;
            for (n2 = 0; n2 < 6; n2 += 2) {
                if (this.quanta[n2] + this.quanta[n2 + 1] <= 8)
                    continue;
                return false;
            }
            if (this.checkOverlap(14, 15, 22, 23)) {
                return false;
            }
            if (this.checkOverlap(16, 17, 24, 25)) {
                return false;
            }
            if (this.checkOverlap(18, 19, 20, 22)) {
                return false;
            }
            if (this.checkOverlap(6, 7, 8, 9)) {
                return false;
            }
            if (this.checkOverlap(10, 11, 12, 13)) {
                return false;
            }
            if (this.checkOverlap(6, 8, 10, 12)) {
                return false;
            }
            if (this.checkOverlap(7, 9, 11, 13)) {
                return false;
            }
            if (this.checkOverlap(6, 7, 10, 11)) {
                return false;
            }
            if (this.checkOverlap(8, 9, 12, 13)) {
                return false;
            }
            for (n2 = 0; n2 < 6; ++n2) {
                int n6;
                int n7;
                n = this.quanta[n2];
                if (n == 0)
                    continue;
                n4 = CoverLib.coverToCornerMask(n2);
                n3 = CoverLib.coverToStripMask(n2);
                n5 = CoverLib.coverToStripMask(n2 ^ 1);
                for (n6 = 0; n6 < 8; ++n6) {
                    n7 = this.quanta[6 + n6];
                    if (!((n4 & 1 << n6) == 0 ? n + n7 > 8 : n7 > 0 && n7 < n))
                        continue;
                    return false;
                }
                for (n6 = 0; n6 < 12; ++n6) {
                    n7 = this.quanta[14 + n6];
                    if (!((n5 & 1 << n6) > 0 ? n + n7 > 8 : (n3 & 1 << n6) > 0 && n7 > 0 && n7 < n))
                        continue;
                    return false;
                }
            }
            for (n2 = 0; n2 < 12; ++n2) {
                n = this.quanta[14 + n2];
                if (n == 0)
                    continue;
                n4 = CoverLib.stripToCornerMask(n2);
                for (n3 = 0; n3 < 8; ++n3) {
                    n5 = this.quanta[6 + n3];
                    if (!((n4 & 1 << n3) == 0 ? n + n5 > 8 : n5 > 0 && n5 < n))
                        continue;
                    return false;
                }
            }
            for (n2 = 0; n2 < 3; ++n2) {
                n = this.quanta[26 + n2];
                if (n == 0)
                    continue;
                for (n4 = 0; n4 < 8; ++n4) {
                    n3 = this.quanta[6 + n4];
                    if (n + n3 <= 4)
                        continue;
                    return false;
                }
                for (n4 = 0; n4 < 12; ++n4) {
                    n3 = this.quanta[14 + n4];
                    if (n + n3 <= 4)
                        continue;
                    return false;
                }
                for (n4 = 0; n4 < 6; ++n4) {
                    if (n4 >> 1 == n2 || this.quanta[n4] + n <= 4)
                        continue;
                    return false;
                }
            }
            return true;
        }

        public boolean checkPlacement(int n, boolean bl) {
            int n2;
            this.calcQuanta();
            if (!this.checkImpingement()) {
                return false;
            }
            if (!this.checkThickFace(9)) {
                return false;
            }
            if (!this.checkThickSide(6)) {
                return false;
            }
            if (!this.checkThickCorner(6)) {
                return false;
            }
            if (!this.checkThickFace(8)) {
                return false;
            }
            if (!this.checkThickSide(5)) {
                return false;
            }
            if (!this.checkThickCorner(5)) {
                return false;
            }
            if (!this.checkThickFace(7)) {
                return false;
            }
            if (!this.checkThickSide(4)) {
                return false;
            }
            if (!this.checkThickCorner(4)) {
                return false;
            }
            if (this.cornermask > 0 && n > 0) {
                return false;
            }
            if (!this.checkThickFace(2)) {
                return false;
            }
            if (!this.checkThickSide(2)) {
                return false;
            }
            if (!this.checkThickCorner(2)) {
                return false;
            }
            this.cornermask = this.fillcornermask;
            if (!this.checkFace(6)) {
                return false;
            }
            if (!this.checkSide(3)) {
                return false;
            }
            if (!this.checkCorner(3)) {
                return false;
            }
            if ((this.covm & 469762048) > 0) {
                if (bl) {
                    return false;
                }
                if (n > 0) {
                    return false;
                }
            }
            for (n2 = 0; n2 < 6; ++n2) {
                if ((n & 1 << n2) == 0 || (this.cornermask & CoverLib.coverToCornerMask(n2)) <= 0)
                    continue;
                return false;
            }
            if (!this.checkFace(1)) {
                return false;
            }
            if (!this.checkSide(1)) {
                return false;
            }
            if (!this.checkCorner(1)) {
                return false;
            }
            if (bl && (this.cornermask > 0 || this.sidemask > 0)) {
                return false;
            }
            if (!this.checkHollow(13)) {
                return false;
            }
            if (!this.checkHollow(12)) {
                return false;
            }
            if (!this.checkHollow(11)) {
                return false;
            }
            if (!this.checkHollow(10)) {
                return false;
            }
            if (!this.checkHollow(5)) {
                return false;
            }
            for (n2 = 0; n2 < 6; ++n2) {
                if ((n & 1 << n2) == 0 || (this.hollowcornermask & CoverLib.coverToCornerMask(n2)) <= 0)
                    continue;
                return false;
            }
            if (!this.checkHollow(4)) {
                return false;
            }
            if (!this.checkHollowCover(3)) {
                return false;
            }
            if (!this.checkFace(0)) {
                return false;
            }
            if (!this.checkSide(0)) {
                return false;
            }
            if (!this.checkCorner(0)) {
                return false;
            }
            for (n2 = 0; n2 < 12; ++n2) {
                int n3;
                if ((this.covm & 1 << n2 + 14) == 0 || (n & (n3 = CoverLib.stripToCoverMask(n2))) != n3)
                    continue;
                return false;
            }
            return true;
        }

        public PlacementValidator(int n, short[] arrs) {
            this.covm = n;
            this.covs = arrs;
        }
    }

}
