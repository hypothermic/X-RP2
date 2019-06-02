/* X-RP - decompiled with CFR */
package eloraam.world;

import forge.ITextureProvider;
import net.minecraft.server.Block;
import net.minecraft.server.BlockFlower;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

import java.util.ArrayList;
import java.util.Random;

public class BlockCustomFlower extends BlockFlower implements ITextureProvider {

    public BlockCustomFlower(int n, int n2) {
        super(n, n2);
        this.c(0.0f);
        this.a(g);
    }

    public int a(int n, int n2) {
        switch (n2) {
            case 0: {
                return 1;
            }
            case 1: {
                return 2;
            }
            case 2: {
                return 2;
            }
        }
        return 1;
    }

    public void a(World world, int n, int n2, int n3, Random random) {
        int n4 = world.getData(n, n2, n3);
        if (n4 != 1 && n4 != 2) {
            return;
        }
        if (world.getLightLevel(n, n2 + 1, n3) >= 9 && random.nextInt(300) == 0) {
            if (n4 == 1) {
                world.setRawData(n, n2, n3, 2);
            } else if (n4 == 2) {
                this.growTree(world, n, n2, n3);
            }
        }
    }

    public void growTree(World world, int n, int n2, int n3) {
        world.setRawTypeId(n, n2, n3, 0);
        WorldGenRubberTree worldGenRubberTree = new WorldGenRubberTree();
        if (!worldGenRubberTree.a(world, world.random, n, n2, n3)) {
            world.setRawTypeIdAndData(n, n2, n3, this.id, 1);
        }
    }

    protected int getDropData(int n) {
        if (n == 2) {
            return 1;
        }
        return n;
    }

    public void addCreativeItems(ArrayList arrayList) {
        for (int i = 0; i <= 1; ++i) {
            arrayList.add(new ItemStack((Block) this, 1, i));
        }
    }

    public String getTextureFile() {
        return "/eloraam/world/worlditems1.png";
    }
}
