/* X-RP - decompiled with CFR */
package eloraam.world;

import eloraam.world.BlockCustomLeaves;
import forge.ITextureProvider;
import java.util.ArrayList;
import net.minecraft.server.Block;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.StepSound;
import net.minecraft.server.World;

public class BlockCustomLog extends Block implements ITextureProvider {

    public BlockCustomLog(int n) {
	super(n, Material.WOOD);
	this.c(1.5f);
	this.a(Block.e);
    }

    public int a(int n, int n2) {
	return n >= 2 ? 50 : 51;
    }

    protected int getDropData(int n) {
	if (n == 1) {
	    return 0;
	}
	return n;
    }

    public void remove(World world, int n, int n2, int n3) {
	BlockCustomLeaves.updateLeaves(world, n, n2, n3, 4);
    }

    public void addCreativeItems(ArrayList arrayList) {
	arrayList.add(new ItemStack((Block) this, 1, 0));
    }

    public String getTextureFile() {
	return "/eloraam/world/world1.png";
    }
}
