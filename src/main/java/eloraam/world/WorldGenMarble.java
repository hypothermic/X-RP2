/* X-RP - decompiled with CFR */
package eloraam.world;

import eloraam.world.WorldGenCustomOre;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.server.Block;
import net.minecraft.server.World;

public class WorldGenMarble extends WorldGenCustomOre {

    LinkedList fillStack = new LinkedList();
    HashSet fillStackTest = new HashSet();

    public WorldGenMarble(int n, int n2, int n3) {
	super(n, n2, n3);
    }

    private void addBlock(int n, int n2, int n3, int n4) {
	List<Integer> list = Arrays.asList(n, n2, n3);
	if (this.fillStackTest.contains(list)) {
	    return;
	}
	this.fillStack.addLast(Arrays.asList(n, n2, n3, n4));
	this.fillStackTest.add(list);
    }

    private void searchBlock(World world, int n, int n2, int n3, int n4) {
	if (world.getTypeId(n - 1, n2, n3) == 0 || world.getTypeId(n + 1, n2, n3) == 0 || world.getTypeId(n, n2 - 1, n3) == 0 || world.getTypeId(n, n2 + 1, n3) == 0 || world.getTypeId(n, n2, n3 - 1) == 0 || world.getTypeId(n, n2, n3 + 1) == 0) {
	    n4 = 6;
	}
	this.addBlock(n - 1, n2, n3, n4);
	this.addBlock(n + 1, n2, n3, n4);
	this.addBlock(n, n2 - 1, n3, n4);
	this.addBlock(n, n2 + 1, n3, n4);
	this.addBlock(n, n2, n3 - 1, n4);
	this.addBlock(n, n2, n3 + 1, n4);
    }

    @Override
    public boolean a(World world, Random random, int n, int n2, int n3) {
	if (world.getTypeId(n, n2, n3) != 0) {
	    return false;
	}
	int n4 = n2;
	while (world.getTypeId(n, n4, n3) != Block.STONE.id) {
	    if (n4 > 96) {
		return false;
	    }
	    ++n4;
	}
	this.addBlock(n, n4, n3, 6);
	while (this.fillStack.size() > 0 && this.numberOfBlocks > 0) {
	    List list = (List) this.fillStack.removeFirst();
	    Integer[] arrinteger = (Integer[]) list.toArray();
	    if (world.getTypeId(arrinteger[0].intValue(), arrinteger[1].intValue(), arrinteger[2].intValue()) != Block.STONE.id)
		continue;
	    world.setRawTypeIdAndData(arrinteger[0].intValue(), arrinteger[1].intValue(), arrinteger[2].intValue(), this.minableBlockId, this.minableBlockMeta);
	    if (arrinteger[3] > 0) {
		this.searchBlock(world, arrinteger[0], arrinteger[1], arrinteger[2], arrinteger[3] - 1);
	    }
	    --this.numberOfBlocks;
	}
	return true;
    }
}
