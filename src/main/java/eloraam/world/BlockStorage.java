/* X-RP - decompiled with CFR */
package eloraam.world;

import forge.ITextureProvider;
import java.util.ArrayList;
import net.minecraft.server.Block;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.StepSound;

public class BlockStorage extends Block implements ITextureProvider {

    public BlockStorage(int n) {
	super(n, Material.ORE);
	this.c(5.0f);
	this.b(10.0f);
	this.a(Block.i);
    }

    public int a(int n, int n2) {
	return 80 + n2;
    }

    protected int getDropData(int n) {
	return n;
    }

    public String getTextureFile() {
	return "/eloraam/world/world1.png";
    }

    public void addCreativeItems(ArrayList arrayList) {
	for (int i = 0; i <= 2; ++i) {
	    arrayList.add(new ItemStack((Block) this, 1, i));
	}
    }
}
