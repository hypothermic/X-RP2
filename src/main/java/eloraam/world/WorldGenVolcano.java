/* X-RP - decompiled with CFR */
package eloraam.world;

import eloraam.world.BlockCustomLeaves;
import eloraam.world.BlockCustomLog;
import eloraam.world.WorldGenCustomOre;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.server.Block;
import net.minecraft.server.BlockFlower;
import net.minecraft.server.BlockLeaves;
import net.minecraft.server.RedPowerWorld;
import net.minecraft.server.World;

public class WorldGenVolcano extends WorldGenCustomOre {

    LinkedList fillStack = new LinkedList();
    HashMap fillStackTest = new HashMap();

    public WorldGenVolcano(int n, int n2, int n3) {
	super(n, n2, n3);
    }

    private void addBlock(int n, int n2, int n3, int n4) {
	if (n4 <= 0) {
	    return;
	}
	List<Integer> list = Arrays.asList(n, n3);
	Integer n5 = (Integer) this.fillStackTest.get(list);
	if (n5 != null && n4 <= n5) {
	    return;
	}
	this.fillStack.addLast(Arrays.asList(n, n2, n3));
	this.fillStackTest.put(list, n4);
    }

    private void searchBlock(int n, int n2, int n3, int n4, Random random) {
	int n5 = random.nextInt(16);
	this.addBlock(n - 1, n2, n3, (n5 & 1) <= 0 ? n4 : n4 - 1);
	this.addBlock(n + 1, n2, n3, (n5 & 2) <= 0 ? n4 : n4 - 1);
	this.addBlock(n, n2, n3 - 1, (n5 & 4) <= 0 ? n4 : n4 - 1);
	this.addBlock(n, n2, n3 + 1, (n5 & 8) <= 0 ? n4 : n4 - 1);
    }

    public boolean canReplaceId(int n) {
	if (n == 0) {
	    return true;
	}
	if (n == Block.WATER.id || n == Block.STATIONARY_WATER.id || n == Block.LOG.id || n == Block.LEAVES.id || n == Block.VINE.id || n == Block.SNOW.id || n == Block.ICE.id) {
	    return true;
	}
	if (n == RedPowerWorld.blockLogs.id || n == RedPowerWorld.blockLeaves.id) {
	    return true;
	}
	return Block.byId[n] instanceof BlockFlower;
    }

    public void eatTree(World world, int n, int n2, int n3) {
	int n4 = world.getTypeId(n, n2, n3);
	if (n4 == Block.SNOW.id) {
	    world.setRawTypeId(n, n2, n3, 0);
	    return;
	}
	if (n4 != Block.LOG.id && n4 != Block.LEAVES.id && n4 != Block.VINE.id) {
	    return;
	}
	world.setRawTypeId(n, n2, n3, 0);
	this.eatTree(world, n, n2 + 1, n3);
    }

    @Override
    public boolean a(World world, Random random, int n, int n2, int n3) {
	int n4;
	if (world.getTypeId(n, n2, n3) != Block.STATIONARY_LAVA.id) {
	    return false;
	}
	int n5 = world.getHighestBlockYAt(n, n3);
	int n6 = Block.LAVA.id;
	while (this.canReplaceId(world.getTypeId(n, n5 - 1, n3))) {
	    --n5;
	}
	for (n4 = n2; n4 < n5; ++n4) {
	    world.setRawTypeId(n, n4, n3, n6);
	    world.setRawTypeIdAndData(n - 1, n4, n3, this.minableBlockId, this.minableBlockMeta);
	    world.setRawTypeIdAndData(n + 1, n4, n3, this.minableBlockId, this.minableBlockMeta);
	    world.setRawTypeIdAndData(n, n4, n3 - 1, this.minableBlockId, this.minableBlockMeta);
	    world.setRawTypeIdAndData(n, n4, n3 + 1, this.minableBlockId, this.minableBlockMeta);
	}
	int n7 = 3 + random.nextInt(4);
	int n8 = random.nextInt(3);
	block2: while (this.numberOfBlocks > 0) {
	    int n9;
	    int n10;
	    while (this.fillStack.size() == 0) {
		world.setRawTypeId(n, n4, n3, n6);
		this.fillStackTest.clear();
		this.searchBlock(n, n4, n3, n7, random);
		if (++n4 <= 125)
		    continue;
		break block2;
	    }
	    List list = (List) this.fillStack.removeFirst();
	    Integer[] arrinteger = (Integer[]) list.toArray();
	    world.getTypeId(arrinteger[0].intValue(), 64, arrinteger[2].intValue());
	    if (!world.isLoaded(arrinteger[0].intValue(), 64, arrinteger[2].intValue()))
		continue;
	    int n11 = (Integer) this.fillStackTest.get(Arrays.asList(arrinteger[0], arrinteger[2]));
	    for (n10 = world.getHighestBlockYAt((int) arrinteger[0].intValue(), (int) arrinteger[2].intValue()) + 1; n10 > 0 && this.canReplaceId(world.getTypeId(arrinteger[0].intValue(), n10 - 1, arrinteger[2].intValue())); --n10) {
	    }
	    if (n10 > arrinteger[1] || !this.canReplaceId(n9 = world.getTypeId(arrinteger[0].intValue(), n10, arrinteger[2].intValue())))
		continue;
	    this.eatTree(world, arrinteger[0], n10, arrinteger[2]);
	    world.setRawTypeIdAndData(arrinteger[0].intValue(), n10, arrinteger[2].intValue(), this.minableBlockId, this.minableBlockMeta);
	    if (arrinteger[1] > n10) {
		n11 = Math.max(n11, n8);
	    }
	    this.searchBlock(arrinteger[0], n10, arrinteger[2], n11, random);
	    --this.numberOfBlocks;
	}
	world.setRawTypeId(n, n4, n3, n6);
	while (n4 > n5 && world.getTypeId(n, n4, n3) == n6) {
	    world.notify(n, n4, n3);
	    world.applyPhysics(n, n4, n3, n6);
	    world.a = true;
	    Block.byId[n6].a(world, n, n4, n3, random);
	    world.a = false;
	    --n4;
	}
	return true;
    }
}
