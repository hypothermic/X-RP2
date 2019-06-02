/* X-RP - decompiled with CFR */
package eloraam.world;

import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemStack;

public class ItemCustomOre extends ItemBlock {

    public ItemCustomOre(int n) {
        super(n);
        this.setMaxDurability(0);
        this.a(true);
        this.setNoRepair();
    }

    public int getPlacedBlockMetadata(int n) {
        return n;
    }

    public int filterData(int n) {
        return n;
    }

    public String a(ItemStack itemStack) {
        switch (itemStack.getData()) {
            case 0: {
                return "tile.oreRuby";
            }
            case 1: {
                return "tile.oreEmerald";
            }
            case 2: {
                return "tile.oreSapphire";
            }
            case 3: {
                return "tile.oreSilver";
            }
            case 4: {
                return "tile.oreTin";
            }
            case 5: {
                return "tile.oreCopper";
            }
            case 6: {
                return "tile.oreTungsten";
            }
            case 7: {
                return "tile.oreNikolite";
            }
        }
        throw new IndexOutOfBoundsException();
    }
}
