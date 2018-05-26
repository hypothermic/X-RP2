/* X-RP - decompiled with CFR */
package eloraam.world;

import eloraam.world.ItemCustomSeeds;
import forge.ITextureProvider;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.server.Block;
import net.minecraft.server.BlockFlower;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.RedPowerWorld;
import net.minecraft.server.StepSound;
import net.minecraft.server.World;

public class BlockCustomCrops extends BlockFlower implements ITextureProvider {

    public BlockCustomCrops(int n) {
	super(n, 0);
	this.c(0.0f);
	this.a(g);
	this.a(true);
	this.j();
	this.a(0.0f, 0.0f, 0.0f, 1.0f, 0.25f, 1.0f);
    }

    public int a(int n, int n2) {
	switch (n2) {
	case 0: {
	    return 64;
	}
	case 1: {
	    return 65;
	}
	case 2: {
	    return 66;
	}
	case 3: {
	    return 67;
	}
	case 4: {
	    return 68;
	}
	case 5: {
	    return 69;
	}
	}
	return 69;
    }

    public void updateShape(IBlockAccess iBlockAccess, int n, int n2, int n3) {
	int n4 = iBlockAccess.getData(n, n2, n3);
	float f = Math.min(1.0f, 0.1f + 0.25f * (float) n4);
	this.a(0.0f, 0.0f, 0.0f, 1.0f, f, 1.0f);
    }

    public int c() {
	return 6;
    }

    public int getDropType(int n, Random random, int n2) {
	return -1;
    }

    public boolean fertilize(World world, int n, int n2, int n3) {
	if (world.getLightLevel(n, n2 + 1, n3) < 9) {
	    return false;
	}
	int n4 = world.getData(n, n2, n3);
	if (n4 == 4 || n4 == 5) {
	    return false;
	}
	if (world.getTypeId(n, n2 - 1, n3) != Block.SOIL.id || world.getData(n, n2 - 1, n3) == 0 || !world.isEmpty(n, n2 + 1, n3)) {
	    return false;
	}
	world.setData(n, n2, n3, 4);
	world.setTypeIdAndData(n, n2 + 1, n3, this.id, 5);
	return true;
    }

    public ArrayList getBlockDropped(World world, int n, int n2, int n3, int n4, int n5) {
	int n6;
	ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
	if (n4 == 4 || n4 == 5) {
	    n6 = 1 + world.random.nextInt(3) + world.random.nextInt(1 + n5);
	    while (n6-- > 0) {
		arrayList.add(new ItemStack(Item.STRING));
	    }
	}
	for (n6 = 0; n6 < 3 + n5; ++n6) {
	    if (n4 == 5) {
		n4 = 4;
	    }
	    if (world.random.nextInt(8) > n4)
		continue;
	    arrayList.add(new ItemStack((Item) RedPowerWorld.itemSeeds, 1, 0));
	}
	return arrayList;
    }

    public void a(World world, int n, int n2, int n3, Random random) {
	super.a(world, n, n2, n3, random);
	if (world.getLightLevel(n, n2 + 1, n3) < 9) {
	    return;
	}
	int n4 = world.getData(n, n2, n3);
	if (n4 == 4 || n4 == 5) {
	    return;
	}
	if (world.getTypeId(n, n2 - 1, n3) != Block.SOIL.id || world.getData(n, n2 - 1, n3) == 0 || !world.isEmpty(n, n2 + 1, n3)) {
	    return;
	}
	if (random.nextInt(30) == 0) {
	    world.setData(n, n2, n3, n4 + 1);
	    if (n4 == 3) {
		world.setTypeIdAndData(n, n2 + 1, n3, this.id, 5);
	    }
	}
    }

    public boolean f(World world, int n, int n2, int n3) {
	int n4 = world.getData(n, n2, n3);
	if (n4 == 5) {
	    if (world.getTypeId(n, n2 - 1, n3) != this.id) {
		return false;
	    }
	    return world.getData(n, n2 - 1, n3) == 4;
	}
	if (world.getTypeId(n, n2 - 1, n3) != Block.SOIL.id) {
	    return false;
	}
	if (n4 == 4) {
	    return true;
	}
	return world.isEmpty(n, n2 + 1, n3);
    }

    public String getTextureFile() {
	return "/eloraam/world/world1.png";
    }
}
