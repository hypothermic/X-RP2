/* X-RP - decompiled with CFR */
package eloraam.world;

import eloraam.core.Vector3;
import eloraam.world.BlockCustomLeaves;
import eloraam.world.BlockCustomLog;
import eloraam.world.FractalLib;
import java.util.Random;
import net.minecraft.server.Block;
import net.minecraft.server.BlockGrass;
import net.minecraft.server.BlockLeaves;
import net.minecraft.server.RedPowerWorld;
import net.minecraft.server.World;
import net.minecraft.server.WorldGenerator;

public class WorldGenRubberTree extends WorldGenerator {

    public void putLeaves(World world, int n, int n2, int n3) {
	int n4 = world.getTypeId(n, n2, n3);
	if (n4 != 0) {
	    return;
	}
	world.setRawTypeIdAndData(n, n2, n3, RedPowerWorld.blockLeaves.id, 0);
    }

    public boolean fillBlock(World world, int n, int n2, int n3) {
	if (n2 < 0 || n2 > 126) {
	    return false;
	}
	int n4 = world.getTypeId(n, n2, n3);
	if (n4 == Block.LOG.id || n4 == RedPowerWorld.blockLogs.id) {
	    return true;
	}
	if (n4 != 0 && n4 != Block.LEAVES.id && n4 != Block.VINE.id && n4 != RedPowerWorld.blockLeaves.id) {
	    return false;
	}
	world.setRawTypeIdAndData(n, n2, n3, RedPowerWorld.blockLogs.id, 0);
	this.putLeaves(world, n, n2 - 1, n3);
	this.putLeaves(world, n, n2 + 1, n3);
	this.putLeaves(world, n, n2, n3 - 1);
	this.putLeaves(world, n, n2, n3 + 1);
	this.putLeaves(world, n - 1, n2, n3);
	this.putLeaves(world, n + 1, n2, n3);
	return true;
    }

    public boolean a(World world, Random random, int n, int n2, int n3) {
	int n4;
	int n5;
	int n6;
	int n7;
	int n8;
	int n9 = random.nextInt(6) + 25;
	if (n2 < 1 || n2 + n9 + 2 > world.getHeight()) {
	    return false;
	}
	for (n6 = -1; n6 <= 1; ++n6) {
	    for (n7 = -1; n7 <= 1; ++n7) {
		n5 = world.getTypeId(n + n6, n2 - 1, n3 + n7);
		if (n5 == Block.GRASS.id || n5 == Block.DIRT.id)
		    continue;
		return false;
	    }
	}
	n6 = 1;
	for (n7 = n2; n7 < n2 + n9; ++n7) {
	    if (n7 > n2 + 3) {
		n6 = 5;
	    }
	    for (n5 = n - n6; n5 <= n + n6; ++n5) {
		for (n8 = n3 - n6; n8 <= n3 + n6; ++n8) {
		    n4 = world.getTypeId(n5, n7, n8);
		    if (n4 == 0 || n4 == Block.LEAVES.id || n4 == Block.LOG.id || n4 == Block.VINE.id || n4 == RedPowerWorld.blockLeaves.id)
			continue;
		    return false;
		}
	    }
	}
	for (n7 = -1; n7 <= 1; ++n7) {
	    for (n5 = -1; n5 <= 1; ++n5) {
		world.setRawTypeId(n + n7, n2 - 1, n3 + n5, Block.DIRT.id);
	    }
	}
	for (n7 = 0; n7 <= 6; ++n7) {
	    for (n5 = -1; n5 <= 1; ++n5) {
		for (n8 = -1; n8 <= 1; ++n8) {
		    world.setTypeIdAndData(n + n5, n2 + n7, n3 + n8, RedPowerWorld.blockLogs.id, 1);
		}
	    }
	    for (n5 = -1; n5 <= 1; ++n5) {
		if (random.nextInt(5) == 1 && world.getTypeId(n + n5, n2 + n7, n3 - 2) == 0) {
		    world.setTypeIdAndData(n + n5, n2 + n7, n3 - 2, Block.VINE.id, 1);
		}
		if (random.nextInt(5) != 1 || world.getTypeId(n + n5, n2 + n7, n3 + 2) != 0)
		    continue;
		world.setTypeIdAndData(n + n5, n2 + n7, n3 + 2, Block.VINE.id, 4);
	    }
	    for (n5 = -1; n5 <= 1; ++n5) {
		if (random.nextInt(5) == 1 && world.getTypeId(n - 2, n2 + n7, n3 + n5) == 0) {
		    world.setTypeIdAndData(n - 2, n2 + n7, n3 + n5, Block.VINE.id, 8);
		}
		if (random.nextInt(5) != 1 || world.getTypeId(n + 2, n2 + n7, n3 + n5) != 0)
		    continue;
		world.setTypeIdAndData(n + 2, n2 + n7, n3 + n5, Block.VINE.id, 2);
	    }
	}
	Vector3 vector3 = new Vector3();
	Vector3 vector32 = new Vector3();
	n8 = random.nextInt(100) + 10;
	for (n4 = 0; n4 < n8; ++n4) {
	    vector32.set((double) random.nextFloat() - 0.5, random.nextFloat(), (double) random.nextFloat() - 0.5);
	    vector32.normalize();
	    double d = ((double) n8 / 10.0 + 4.0) * (double) (1.0f + 1.0f * random.nextFloat());
	    vector32.x *= d;
	    vector32.z *= d;
	    vector32.y = vector32.y * (double) (n9 - 15) + (double) n8 / 10.0;
	    if (n8 < 8) {
		switch (n8) {
		case 0: {
		    vector3.set(n - 1, n2 + 6, n3 - 1);
		    break;
		}
		case 1: {
		    vector3.set(n - 1, n2 + 6, n3);
		    break;
		}
		case 2: {
		    vector3.set(n - 1, n2 + 6, n3 + 1);
		    break;
		}
		case 3: {
		    vector3.set(n, n2 + 6, n3 + 1);
		    break;
		}
		case 4: {
		    vector3.set(n + 1, n2 + 6, n3 + 1);
		    break;
		}
		case 5: {
		    vector3.set(n + 1, n2 + 6, n3);
		    break;
		}
		case 6: {
		    vector3.set(n + 1, n2 + 6, n3 - 1);
		    break;
		}
		default: {
		    vector3.set(n, n2 + 6, n3 - 1);
		    break;
		}
		}
	    } else {
		vector3.set(n + random.nextInt(3) - 1, n2 + 6, n3 + random.nextInt(3) - 1);
	    }
	    long l = random.nextLong();
	    FractalLib.BlockSnake blockSnake = new FractalLib.BlockSnake(vector3, vector32, l);
	    while (blockSnake.iterate()) {
		Vector3 vector33 = blockSnake.get();
		if (this.fillBlock(world, (int) Math.floor(vector33.x), (int) Math.floor(vector33.y), (int) Math.floor(vector33.z)))
		    continue;
	    }
	}
	return true;
    }
}
