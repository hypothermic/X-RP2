/* X-RP - decompiled with CFR */
package eloraam.core;

import forge.ITextureProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

public class ItemParts extends Item implements ITextureProvider {

    String textureFile;
    HashMap names = new HashMap();
    HashMap icons = new HashMap();
    ArrayList valid = new ArrayList();

    public ItemParts(int n, String string) {
	super(n);
	this.setMaxDurability(0);
	this.a(true);
	this.textureFile = string;
	this.setNoRepair();
    }

    public void addItem(int n, int n2, String string) {
	this.icons.put(n, n2);
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

    public int getIconFromDamage(int n) {
	Integer n2 = (Integer) this.icons.get(n);
	if (n2 == null) {
	    throw new IndexOutOfBoundsException();
	}
	return n2;
    }

    public void addCreativeItems(ArrayList arrayList) {
	// X-RP: TODO: better solution
	/*for (Integer n : this.valid) {
	    arrayList.add(new ItemStack((Item) this, 1, n.intValue()));
	}*/
	Integer localInteger;
	for (Iterator localIterator = valid.iterator(); localIterator.hasNext(); arrayList.add(new ItemStack(this, 1, localInteger.intValue()))) {
	    localInteger = (Integer)localIterator.next();
	}
    }

    public String getTextureFile() {
	return this.textureFile;
    }
}
