/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.*;

import java.util.ArrayList;
import java.util.Random;

public class BlockMultipart extends BlockExtended {

    public BlockMultipart(int n, Material material) {
        super(n, material);
    }

    @Override
    public void doPhysics(World world, int n, int n2, int n3, int n4) {
        TileMultipart tileMultipart = (TileMultipart) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileMultipart.class);
        if (tileMultipart == null) {
            world.setTypeId(n, n2, n3, 0);
            return;
        }
        tileMultipart.onBlockNeighborChange(n4);
    }

    public void dropBlockAsItemWithChance(World world, int n, int n2, int n3, int n4, float f) {
    }

    public int getDropType(int n, Random random, int n2) {
        return 0;
    }

    public ArrayList getBlockDropped(World world, int n, int n2, int n3, int n4, int n5) {
        ArrayList arrayList = new ArrayList();
        TileMultipart tileMultipart = (TileMultipart) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileMultipart.class);
        if (tileMultipart == null) {
            return arrayList;
        }
        tileMultipart.addHarvestContents(arrayList);
        return arrayList;
    }

    public void a(World world, EntityHuman entityHuman, int n, int n2, int n3, int n4) {
    }

    public boolean removeBlockByPlayer(World world, EntityHuman entityHuman, int n, int n2, int n3) {
        if (CoreProxy.isClient(world)) {
            return true;
        }
        MovingObjectPosition movingObjectPosition = CoreLib.retraceBlock(world, entityHuman, n, n2, n3);
        if (movingObjectPosition == null) {
            return false;
        }
        if (movingObjectPosition.type != EnumMovingObjectType.TILE) {
            return false;
        }
        TileMultipart tileMultipart = (TileMultipart) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileMultipart.class);
        if (tileMultipart == null) {
            return false;
        }
        tileMultipart.onHarvestPart(entityHuman, movingObjectPosition.subHit);
        return false;
    }

    @Override
    public boolean interact(World world, int n, int n2, int n3, EntityHuman entityHuman) {
        MovingObjectPosition movingObjectPosition = CoreLib.retraceBlock(world, entityHuman, n, n2, n3);
        if (movingObjectPosition == null) {
            return false;
        }
        if (movingObjectPosition.type != EnumMovingObjectType.TILE) {
            return false;
        }
        TileMultipart tileMultipart = (TileMultipart) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileMultipart.class);
        if (tileMultipart == null) {
            return false;
        }
        return tileMultipart.onPartActivateSide(entityHuman, movingObjectPosition.subHit, movingObjectPosition.face);
    }

    public float blockStrength(EntityHuman entityHuman, int n) {
        MovingObjectPosition movingObjectPosition = CoreLib.traceBlock(entityHuman);
        if (movingObjectPosition == null || movingObjectPosition.type != EnumMovingObjectType.TILE) {
            return 0.0f;
        }
        TileMultipart tileMultipart = (TileMultipart) CoreLib.getTileEntity((IBlockAccess) entityHuman.world, movingObjectPosition.b, movingObjectPosition.c, movingObjectPosition.d, TileMultipart.class);
        if (tileMultipart == null) {
            return 0.0f;
        }
        return tileMultipart.getPartStrength(entityHuman, movingObjectPosition.subHit);
    }

    public void wasExploded(World world, int n, int n2, int n3) {
        TileMultipart tileMultipart = (TileMultipart) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileMultipart.class);
        if (tileMultipart == null) {
            return;
        }
        tileMultipart.breakBlock();
    }

    public void a(World world, int n, int n2, int n3, AxisAlignedBB axisAlignedBB, ArrayList arrayList) {
        int n4;
        TileMultipart tileMultipart = (TileMultipart) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileMultipart.class);
        if (tileMultipart == null) {
            return;
        }
        for (int i = tileMultipart.getSolidPartsMask(); i > 0; i &= ~(1 << n4)) {
            n4 = Integer.numberOfTrailingZeros(i);
            tileMultipart.setPartBounds(this, n4);
            super.a(world, n, n2, n3, axisAlignedBB, arrayList);
        }
    }

    public MovingObjectPosition a(World world, int n, int n2, int n3, Vec3D vec3D, Vec3D vec3D2) {
        int n4;
        TileMultipart tileMultipart = (TileMultipart) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileMultipart.class);
        if (tileMultipart == null) {
            return null;
        }
        MovingObjectPosition movingObjectPosition = null;
        int n5 = -1;
        double d = 0.0;
        for (int i = tileMultipart.getPartsMask(); i > 0; i &= ~(1 << n4)) {
            n4 = Integer.numberOfTrailingZeros(i);
            tileMultipart.setPartBounds(this, n4);
            MovingObjectPosition movingObjectPosition2 = super.a(world, n, n2, n3, vec3D, vec3D2);
            if (movingObjectPosition2 == null)
                continue;
            double d2 = movingObjectPosition2.pos.distanceSquared(vec3D);
            if (movingObjectPosition != null && d2 >= d)
                continue;
            d = d2;
            movingObjectPosition = movingObjectPosition2;
            n5 = n4;
        }
        if (movingObjectPosition == null) {
            return null;
        }
        tileMultipart.setPartBounds(this, n5);
        movingObjectPosition.subHit = n5;
        return movingObjectPosition;
    }

    public static void removeMultipart(World world, int n, int n2, int n3) {
        world.setRawTypeId(n, n2, n3, 0);
    }

    public static void removeMultipartWithNotify(World world, int n, int n2, int n3) {
        world.setTypeId(n, n2, n3, 0);
    }

    protected MovingObjectPosition traceCurrentBlock(World world, int n, int n2, int n3, Vec3D vec3D, Vec3D vec3D2) {
        return super.a(world, n, n2, n3, vec3D, vec3D2);
    }

    public void setPartBounds(World world, int n, int n2, int n3, int n4) {
        TileMultipart tileMultipart = (TileMultipart) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileMultipart.class);
        if (tileMultipart == null) {
            this.a(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            return;
        }
        tileMultipart.setPartBounds(this, n4);
    }

    public void computeCollidingBoxes(World world, int n, int n2, int n3, AxisAlignedBB axisAlignedBB, ArrayList arrayList, TileMultipart tileMultipart) {
        int n4;
        for (int i = tileMultipart.getSolidPartsMask(); i > 0; i &= ~(1 << n4)) {
            n4 = Integer.numberOfTrailingZeros(i);
            tileMultipart.setPartBounds(this, n4);
            super.a(world, n, n2, n3, axisAlignedBB, arrayList);
        }
    }
}
