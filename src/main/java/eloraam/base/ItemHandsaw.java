/* X-RP - decompiled with CFR */
package eloraam.base;

import forge.ITextureProvider;
import net.minecraft.server.Item;

public class ItemHandsaw extends Item implements ITextureProvider {

    private int sharp;

    public ItemHandsaw(int n, int n2) {
	super(n);
	this.e(1);
	this.setNoRepair();
	this.sharp = n2;
    }

    public boolean isFull3D() {
	return true;
    }

    public boolean shouldRotateAroundWhenRendering() {
	return true;
    }

    public int getSharpness() {
	return this.sharp;
    }

    public String getTextureFile() {
	return "/eloraam/base/items1.png";
    }
}
