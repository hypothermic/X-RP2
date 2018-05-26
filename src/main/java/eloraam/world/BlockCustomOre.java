/* X-RP - decompiled with CFR */
package eloraam.world;

import eloraam.core.ItemParts;
import forge.ITextureProvider;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.server.Block;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.RedPowerBase;

public class BlockCustomOre extends Block implements ITextureProvider {

    public BlockCustomOre(int n) {
	super(n, Material.STONE);
	this.c(3.0f);
	this.b(5.0f);
    }

    public float getHardness(int n) {
	return 3.0f;
    }

    public int a(int n, int n2) {
	return 32 + n2;
    }

    public int getDropType(int n, Random random, int n2) {
	if (n < 3 || n == 7) {
	    return RedPowerBase.itemResource.id;
	}
	return this.id;
    }

    public int quantityDropped(int n, int n2, Random random) {
	if (n == 7) {
	    return 4 + random.nextInt(2) + random.nextInt(n2 + 1);
	}
	if (n < 3) {
	    int n3 = random.nextInt(n2 + 2) - 1;
	    if (n3 < 0) {
		n3 = 0;
	    }
	    return n3 + 1;
	}
	return 1;
    }

    protected int getDropData(int n) {
	if (n == 7) {
	    return 6;
	}
	return n;
    }

    public void addCreativeItems(ArrayList arrayList) {
	for (int i = 0; i <= 7; ++i) {
	    arrayList.add(new ItemStack((Block) this, 1, i));
	}
    }

    public String getTextureFile() {
	return "/eloraam/world/world1.png";
    }
}
