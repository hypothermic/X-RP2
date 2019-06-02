/* X-RP - decompiled with CFR */
package eloraam.world;

import forge.ITextureProvider;
import net.minecraft.server.Item;

public class ItemWoolCard extends Item implements ITextureProvider {

    public ItemWoolCard(int n) {
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
