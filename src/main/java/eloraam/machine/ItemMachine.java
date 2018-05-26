/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.Item
 *  net.minecraft.server.ItemBlock
 *  net.minecraft.server.ItemStack
 */
package eloraam.machine;

import java.util.ArrayList;
import net.minecraft.server.Item;
import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemStack;

public class ItemMachine
extends ItemBlock {
    public ItemMachine(int n) {
        super(n);
        this.setMaxDurability(0);
        this.a(true);
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
                return "tile.rpdeploy";
            }
            case 1: {
                return "tile.rpbreaker";
            }
            case 2: {
                return "tile.rptranspose";
            }
            case 3: {
                return "tile.rpfilter";
            }
            case 4: {
                return "tile.rpitemdet";
            }
            case 5: {
                return "tile.rpsorter";
            }
            case 6: {
                return "tile.rpbatbox";
            }
            case 7: {
                return "tile.rpmotor";
            }
            case 8: {
                return "tile.rpretriever";
            }
            case 10: {
                return "tile.rpregulate";
            }
            case 11: {
                return "tile.rpthermo";
            }
            case 12: {
                return "tile.rpignite";
            }
            case 13: {
                return "tile.rpassemble";
            }
            case 14: {
                return "tile.rpeject";
            }
            case 15: {
                return "tile.rprelay";
            }
        }
        throw new IndexOutOfBoundsException();
    }

    public void addCreativeItems(ArrayList arrayList) {
        for (int i = 0; i <= 15; ++i) {
            if (i == 9) continue;
            arrayList.add(new ItemStack((Item)this, 1, i));
        }
    }
}

