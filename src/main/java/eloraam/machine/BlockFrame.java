/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.AxisAlignedBB
 *  net.minecraft.server.Block
 *  net.minecraft.server.IBlockAccess
 *  net.minecraft.server.Material
 *  net.minecraft.server.World
 */
package eloraam.machine;

import eloraam.core.BlockCoverable;
import eloraam.core.CoreLib;
import eloraam.core.TileMultipart;
import eloraam.core.WorldCoord;
import eloraam.machine.TileFrameMoving;
import eloraam.machine.TileMotor;
import java.util.ArrayList;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Material;
import net.minecraft.server.World;

public class BlockFrame
extends BlockCoverable {
    public BlockFrame(int n) {
        super(n, CoreLib.materialRedpower);
        this.c(0.5f);
    }

    @Override
    public void a(World world, int n, int n2, int n3, AxisAlignedBB axisAlignedBB, ArrayList arrayList) {
        TileFrameMoving tileFrameMoving = (TileFrameMoving)CoreLib.getTileEntity((IBlockAccess)world, n, n2, n3, TileFrameMoving.class);
        if (tileFrameMoving == null) {
            super.a(world, n, n2, n3, axisAlignedBB, arrayList);
            return;
        }
        this.computeCollidingBoxes(world, n, n2, n3, axisAlignedBB, arrayList, tileFrameMoving);
        TileMotor tileMotor = (TileMotor)CoreLib.getTileEntity((IBlockAccess)world, tileFrameMoving.motorX, tileFrameMoving.motorY, tileFrameMoving.motorZ, TileMotor.class);
        if (tileMotor == null) {
            return;
        }
        WorldCoord worldCoord = new WorldCoord(n, n2, n3);
        worldCoord.step(tileMotor.MoveDir ^ 1);
        tileFrameMoving = (TileFrameMoving)CoreLib.getTileEntity((IBlockAccess)world, worldCoord, TileFrameMoving.class);
        if (tileFrameMoving == null) {
            return;
        }
        this.computeCollidingBoxes(world, worldCoord.x, worldCoord.y, worldCoord.z, axisAlignedBB, arrayList, tileFrameMoving);
    }

    public String getTextureFile() {
        return "/eloraam/machine/machine1.png";
    }
}

