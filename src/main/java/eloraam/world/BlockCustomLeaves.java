/* X-RP - decompiled with CFR */
package eloraam.world;

import eloraam.core.CoreProxy;
import eloraam.core.WorldCoord;
import eloraam.world.BlockCustomFlower;
import eloraam.world.BlockCustomLog;
import forge.ITextureProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.server.Block;
import net.minecraft.server.BlockLeaves;
import net.minecraft.server.ItemStack;
import net.minecraft.server.RedPowerWorld;
import net.minecraft.server.StepSound;
import net.minecraft.server.World;

public class BlockCustomLeaves extends BlockLeaves implements ITextureProvider {

    public BlockCustomLeaves(int n) {
	super(n, 64);
	this.a(true);
	this.c(0.2f);
	this.a(Block.g);
	this.f(1);
    }

    public boolean a() {
	this.b = !Block.LEAVES.a();
	return !this.b;
    }

    public int a(int n, int n2) {
	this.b = !Block.LEAVES.a();
	return 48 + (this.b ? 0 : 1);
    }

    public void remove(World world, int n, int n2, int n3) {
	BlockCustomLeaves.updateLeaves(world, n, n2, n3, 1);
    }

    public static void updateLeaves(World world, int n, int n2, int n3, int n4) {
	if (!world.a(n - n4 - 1, n2 - n4 - 1, n3 - n4 - 1, n + n4 + 1, n2 + n4 + 1, n3 + n4 + 1)) {
	    return;
	}
	for (int i = -n4; i <= n4; ++i) {
	    for (int j = -n4; j <= n4; ++j) {
		for (int k = -n4; k <= n4; ++k) {
		    if (world.getTypeId(n + i, n2 + j, n3 + k) != RedPowerWorld.blockLeaves.id)
			continue;
		    int n5 = world.getData(n + i, n2 + j, n3 + k);
		    world.setRawData(n + i, n2 + j, n3 + k, n5 | 8);
		}
	    }
	}
    }

    public void a(World world, int n, int n2, int n3, Random random) {
	if (CoreProxy.isClient(world)) {
	    return;
	}
	int n4 = world.getData(n, n2, n3);
	if ((n4 & 8) == 0 || (n4 & 4) > 0) {
	    return;
	}
	HashMap<WorldCoord, Integer> hashMap = new HashMap<WorldCoord, Integer>();
	LinkedList<WorldCoord> linkedList = new LinkedList<WorldCoord>();
	WorldCoord worldCoord = new WorldCoord(n, n2, n3);
	WorldCoord worldCoord2 = worldCoord.copy();
	linkedList.addLast(worldCoord);
	hashMap.put(worldCoord, 4);
	while (linkedList.size() > 0) {
	    WorldCoord worldCoord3 = (WorldCoord) linkedList.removeFirst();
	    Integer n5 = (Integer) hashMap.get(worldCoord3);
	    if (n5 == null)
		continue;
	    for (int i = 0; i < 6; ++i) {
		worldCoord2.set(worldCoord3);
		worldCoord2.step(i);
		if (hashMap.containsKey(worldCoord2))
		    continue;
		int n6 = world.getTypeId(worldCoord2.x, worldCoord2.y, worldCoord2.z);
		if (n6 == RedPowerWorld.blockLogs.id) {
		    world.setRawData(n, n2, n3, n4 & -9);
		    return;
		}
		if (n5 == 0 || n6 != this.id)
		    continue;
		hashMap.put(worldCoord2, n5 - 1);
		linkedList.addLast(worldCoord2);
	    }
	}
	this.b(world, n, n2, n3, n4, 0);
	world.setTypeId(n, n2, n3, 0);
    }

    public int getDropType(int n, Random random, int n2) {
	return RedPowerWorld.blockPlants.id;
    }

    public int quantityDropped(int n, int n2, Random random) {
	return random.nextInt(20) == 0 ? 1 : 0;
    }

    protected int getDropData(int n) {
	return 1;
    }

    public void addCreativeItems(ArrayList arrayList) {
	arrayList.add(new ItemStack((Block) this, 1, 0));
    }

    public String getTextureFile() {
	return "/eloraam/world/world1.png";
    }
}
