/* X-RP - decompiled with CFR */
package eloraam.world;

import java.util.Random;
import net.minecraft.server.Block;
import net.minecraft.server.MathHelper;
import net.minecraft.server.World;
import net.minecraft.server.WorldGenerator;

public class WorldGenCustomOre extends WorldGenerator {

    protected int minableBlockId;
    protected int minableBlockMeta;
    protected int numberOfBlocks;

    public WorldGenCustomOre(int n, int n2, int n3) {
	this.minableBlockId = n;
	this.minableBlockMeta = n2;
	this.numberOfBlocks = n3;
    }

    public void tryGenerateBlock(World world, Random random, int n, int n2, int n3) {
	if (world.getTypeId(n, n2, n3) == Block.STONE.id) {
	    world.setRawTypeIdAndData(n, n2, n3, this.minableBlockId, this.minableBlockMeta);
	}
    }

    public boolean a(World world, Random random, int n, int n2, int n3) {
	float f = random.nextFloat() * 3.1415927f;
	double d = (float) (n + 8) + MathHelper.sin((float) f) * (float) this.numberOfBlocks / 8.0f;
	double d2 = (float) (n + 8) - MathHelper.sin((float) f) * (float) this.numberOfBlocks / 8.0f;
	double d3 = (float) (n3 + 8) + MathHelper.cos((float) f) * (float) this.numberOfBlocks / 8.0f;
	double d4 = (float) (n3 + 8) - MathHelper.cos((float) f) * (float) this.numberOfBlocks / 8.0f;
	double d5 = n2 + random.nextInt(3) + 2;
	double d6 = n2 + random.nextInt(3) + 2;
	for (int i = 0; i <= this.numberOfBlocks; ++i) {
	    double d7 = d + (d2 - d) * (double) i / (double) this.numberOfBlocks;
	    double d8 = d5 + (d6 - d5) * (double) i / (double) this.numberOfBlocks;
	    double d9 = d3 + (d4 - d3) * (double) i / (double) this.numberOfBlocks;
	    double d10 = random.nextDouble() * (double) this.numberOfBlocks / 16.0;
	    double d11 = (double) (MathHelper.sin((float) ((float) i * 3.1415927f / (float) this.numberOfBlocks)) + 1.0f) * d10 + 1.0;
	    double d12 = (double) (MathHelper.sin((float) ((float) i * 3.1415927f / (float) this.numberOfBlocks)) + 1.0f) * d10 + 1.0;
	    int n4 = MathHelper.floor((double) (d7 - d11 / 2.0));
	    int n5 = MathHelper.floor((double) (d8 - d12 / 2.0));
	    int n6 = MathHelper.floor((double) (d9 - d11 / 2.0));
	    int n7 = MathHelper.floor((double) (d7 + d11 / 2.0));
	    int n8 = MathHelper.floor((double) (d8 + d12 / 2.0));
	    int n9 = MathHelper.floor((double) (d9 + d11 / 2.0));
	    for (int j = n4; j <= n7; ++j) {
		double d13 = ((double) j + 0.5 - d7) / (d11 / 2.0);
		if (d13 * d13 >= 1.0)
		    continue;
		for (int k = n5; k <= n8; ++k) {
		    double d14 = ((double) k + 0.5 - d8) / (d12 / 2.0);
		    if (d13 * d13 + d14 * d14 >= 1.0)
			continue;
		    for (int i2 = n6; i2 <= n9; ++i2) {
			double d15 = ((double) i2 + 0.5 - d9) / (d11 / 2.0);
			if (d13 * d13 + d14 * d14 + d15 * d15 >= 1.0)
			    continue;
			this.tryGenerateBlock(world, random, j, k, i2);
		    }
		}
	    }
	}
	return true;
    }
}
