/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.IBlockAccess;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

import java.util.HashMap;

public class PipeLib {

    private static HashMap fluidByBlock = new HashMap();
    private static HashMap fluidByID = new HashMap();

    private static boolean isConSide(IBlockAccess iBlockAccess, int n, int n2, int n3, int n4) {
        TileEntity tileEntity = iBlockAccess.getTileEntity(n, n2, n3);
        if (tileEntity instanceof IPipeConnectable) {
            IPipeConnectable iPipeConnectable = (IPipeConnectable) tileEntity;
            int n5 = iPipeConnectable.getPipeConnectableSides();
            return (n5 & 1 << n4) > 0;
        }
        return false;
    }

    public static int getConnections(IBlockAccess iBlockAccess, int n, int n2, int n3) {
        IPipeConnectable iPipeConnectable = (IPipeConnectable) CoreLib.getTileEntity(iBlockAccess, n, n2, n3, IPipeConnectable.class);
        if (iPipeConnectable == null) {
            return 0;
        }
        int n4 = 0;
        int n5 = iPipeConnectable.getPipeConnectableSides();
        if ((n5 & 1) > 0 && PipeLib.isConSide(iBlockAccess, n, n2 - 1, n3, 1)) {
            n4 |= 1;
        }
        if ((n5 & 2) > 0 && PipeLib.isConSide(iBlockAccess, n, n2 + 1, n3, 0)) {
            n4 |= 2;
        }
        if ((n5 & 4) > 0 && PipeLib.isConSide(iBlockAccess, n, n2, n3 - 1, 3)) {
            n4 |= 4;
        }
        if ((n5 & 8) > 0 && PipeLib.isConSide(iBlockAccess, n, n2, n3 + 1, 2)) {
            n4 |= 8;
        }
        if ((n5 & 16) > 0 && PipeLib.isConSide(iBlockAccess, n - 1, n2, n3, 5)) {
            n4 |= 16;
        }
        if ((n5 & 32) > 0 && PipeLib.isConSide(iBlockAccess, n + 1, n2, n3, 4)) {
            n4 |= 32;
        }
        return n4;
    }

    public static void registerFluidBlock(int n, FluidClass fluidClass) {
        fluidByBlock.put(n, fluidClass);
    }

    public static void registerFluid(int n, FluidClass fluidClass) {
        fluidByID.put(n, fluidClass);
    }

    public static int getLiquidId(World world, WorldCoord worldCoord) {
        int n = world.getTypeId(worldCoord.x, worldCoord.y, worldCoord.z);
        FluidClass fluidClass = (FluidClass) fluidByBlock.get(n);
        if (fluidClass == null) {
            return 0;
        }
        return fluidClass.getFluidId(world, worldCoord);
    }

    public static FluidClass getLiquidClass(int n) {
        return (FluidClass) fluidByID.get(n);
    }

    public static void movePipeLiquid(World world, IPipeConnectable iPipeConnectable, WorldCoord worldCoord, int n) {
        for (int i = 0; i < 6; ++i) {
            FluidBuffer fluidBuffer;
            IPipeConnectable iPipeConnectable2;
            FluidBuffer fluidBuffer2;
            int n2;
            int n3;
            WorldCoord worldCoord2;
            if ((n & 1 << i) == 0 || (iPipeConnectable2 = (IPipeConnectable) CoreLib.getTileEntity((IBlockAccess) world, worldCoord2 = worldCoord.coordStep(i), IPipeConnectable.class)) == null || (n2 = iPipeConnectable.getPipePressure(i)) < (n3 = iPipeConnectable2.getPipePressure(i ^ 1)) || (fluidBuffer2 = iPipeConnectable.getPipeBuffer(i)) == null)
                continue;
            int n4 = fluidBuffer2.getLevel();
            if (fluidBuffer2.Type == 0 || (n4 += fluidBuffer2.Delta) <= 0 || (fluidBuffer = iPipeConnectable2.getPipeBuffer(i ^ 1)) == null)
                continue;
            int n5 = fluidBuffer.getLevel();
            if (fluidBuffer.Type != 0 && fluidBuffer.Type != fluidBuffer2.Type)
                continue;
            int n6 = Math.max(n2 <= n3 ? 0 : 1, (n4 - n5) / 2);
            if ((n6 = Math.min(n6, fluidBuffer.getMaxLevel() - n5)) < 0)
                continue;
            fluidBuffer2.addLevel(fluidBuffer2.Type, -n6);
            fluidBuffer.addLevel(fluidBuffer2.Type, n6);
        }
    }
}
