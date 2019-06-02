/* X-RP - decompiled with CFR */
package eloraam.core;

import forge.ISpecialResistance;
import net.minecraft.server.*;

public abstract class BlockCoverable extends BlockMultipart implements ISpecialResistance {

    public BlockCoverable(int n, Material material) {
        super(n, material);
    }

    public boolean isBlockSolidOnSide(World world, int n, int n2, int n3, int n4) {
        TileCoverable tileCoverable = (TileCoverable) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileCoverable.class);
        if (tileCoverable == null) {
            return false;
        }
        return tileCoverable.isSideNormal(n4);
    }

    public float getSpecialExplosionResistance(World world, int n, int n2, int n3, double d, double d2, double d3, Entity entity) {
        Vec3D vec3D = Vec3D.create((double) d, (double) d2, (double) d3);
        Vec3D vec3D2 = Vec3D.create((double) ((double) n + 0.5), (double) ((double) n2 + 0.5), (double) ((double) n3 + 0.5));
        Block block = Block.byId[world.getTypeId(n, n2, n3)];
        if (block == null) {
            return 0.0f;
        }
        MovingObjectPosition movingObjectPosition = block.a(world, n, n2, n3, vec3D, vec3D2);
        if (movingObjectPosition == null) {
            return block.a(entity);
        }
        TileCoverable tileCoverable = (TileCoverable) CoreLib.getTileEntity((IBlockAccess) world, n, n2, n3, TileCoverable.class);
        if (tileCoverable == null) {
            return block.a(entity);
        }
        float f = tileCoverable.getExplosionResistance(movingObjectPosition.subHit, movingObjectPosition.face, entity);
        if (f < 0.0f) {
            return block.a(entity);
        }
        return f;
    }
}
