/* X-RP - decompiled with CFR */
package eloraam.world;

import eloraam.core.WorldCoord;
import net.minecraft.server.Block;
import net.minecraft.server.Material;
import net.minecraft.server.World;

import java.util.Random;

public class BlockCobbleMossifier extends Block {

    public BlockCobbleMossifier(int n) {
        super(n, 36, Material.STONE);
        this.a(true);
        this.c(2.0f);
        this.b(10.0f);
        this.a(Block.h);
        this.a("stoneMoss");
    }

    public void a(World world, int n, int n2, int n3, Random random) {
        if (!world.isEmpty(n, n2 + 1, n3)) {
            return;
        }
        if (world.isChunkLoaded(n, n2 + 1, n3)) {
            return;
        }
        WorldCoord worldCoord = new WorldCoord(n, n2, n3);
        for (int i = 0; i < 4; ++i) {
            int n4;
            WorldCoord worldCoord2 = worldCoord.coordStep(2 + i);
            int n5 = n4 = world.getTypeId(worldCoord2.x, worldCoord2.y, worldCoord2.z);
            int n6 = 0;
            if (n4 == Block.COBBLESTONE.id) {
                n5 = this.id;
            } else {
                if (n4 != Block.SMOOTH_BRICK.id || world.getData(worldCoord2.x, worldCoord2.y, worldCoord2.z) != 0)
                    continue;
                n6 = 1;
            }
            if (world.getTypeId(worldCoord2.x, worldCoord2.y + 1, worldCoord2.z) != 0)
                continue;
            if (world.isChunkLoaded(worldCoord2.x, worldCoord2.y + 1, worldCoord2.z)) {
                return;
            }
            boolean bl = false;
            for (int j = 0; j < 4; ++j) {
                WorldCoord worldCoord3 = worldCoord2.coordStep(2 + j);
                int n7 = world.getTypeId(worldCoord3.x, worldCoord3.y, worldCoord3.z);
                if (n7 != Block.STATIONARY_WATER.id && n7 != Block.WATER.id)
                    continue;
                bl = true;
                break;
            }
            if (!bl || random.nextInt(2) != 0)
                continue;
            world.setTypeIdAndData(worldCoord2.x, worldCoord2.y, worldCoord2.z, n5, n6);
        }
    }
}
