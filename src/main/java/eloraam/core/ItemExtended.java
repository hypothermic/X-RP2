/* X-RP - decompiled with CFR */
package eloraam.core;

import net.minecraft.server.Item;
import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemStack;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ItemExtended extends ItemBlock {

    HashMap names = new HashMap();
    ArrayList valid = new ArrayList();

    public ItemExtended(int n) {
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

    public void setMetaName(int n, String string) {
        this.names.put(n, string);
        this.valid.add(n);
    }

    public String a(ItemStack itemStack) {
        String string = (String) this.names.get(itemStack.getData());
        if (string == null) {
            throw new IndexOutOfBoundsException();
        }
        return string;
    }

    public void addCreativeItems(ArrayList arrayList) {
	    for (Integer n : ((ArrayList<Integer>) this.valid)) {
	        arrayList.add(new ItemStack((Item) this, 1, n.intValue()));
	    }
        /*Integer localInteger;
        for (java.util.Iterator localIterator = valid.iterator(); localIterator.hasNext(); arrayList.add(new ItemStack(this, 1, localInteger.intValue()))) {
            localInteger = (Integer) localIterator.next();
        }*/
    }
}
