/* X-RP - decompiled with CFR */
package eloraam.base;

import forge.ITextureProvider;
import net.minecraft.server.Item;

public class ItemDrawplate extends Item implements ITextureProvider {

    public ItemDrawplate(int n) {
        super(n);
        this.e(1);
        this.setNoRepair();
    }

    public boolean isFull3D() {
        return true;
    }

    public boolean shouldRotateAroundWhenRendering() {
        return true;
    }

    public String getTextureFile() {
        return "/eloraam/base/items1.png";
    }
}
