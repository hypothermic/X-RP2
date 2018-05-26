/* X-RP - decompiled with CFR */
package eloraam.world;

import forge.ISpecialResistance;
import forge.ITextureProvider;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.server.Block;
import net.minecraft.server.Entity;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.World;

public class BlockCustomStone extends Block implements ITextureProvider, ISpecialResistance {

    public BlockCustomStone(int n) {
	super(n, Material.STONE);
	this.c(3.0f);
	this.b(10.0f);
    }

    public float getHardness(int n) {
	switch (n) {
	case 0: {
	    return 1.0f;
	}
	case 1: {
	    return 2.5f;
	}
	case 2: {
	    return 1.0f;
	}
	case 3: {
	    return 2.5f;
	}
	case 4: {
	    return 2.5f;
	}
	}
	return 3.0f;
    }

    public float getSpecialExplosionResistance(World world, int n, int n2, int n3, double d, double d2, double d3, Entity entity) {
	int n4 = world.getData(n, n2, n3);
	switch (n4) {
	case 1:
	case 3:
	case 4: {
	    return 12.0f;
	}
	}
	return 6.0f;
    }

    public int a(int n, int n2) {
	return 16 + n2;
    }

    public int getDropType(int n, Random random, int n2) {
	return this.id;
    }

    public int a(Random random) {
	return 1;
    }

    protected int getDropData(int n) {
	if (n == 1) {
	    return 3;
	}
	return n;
    }

    public void addCreativeItems(ArrayList arrayList) {
	for (int i = 0; i <= 4; ++i) {
	    arrayList.add(new ItemStack((Block) this, 1, i));
	}
    }

    public String getTextureFile() {
	return "/eloraam/world/world1.png";
    }
}
