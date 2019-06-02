/* X-RP - decompiled with CFR */
package eloraam.world;

import forge.ITextureProvider;
import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.ItemAxe;

public class ItemCustomAxe extends ItemAxe implements ITextureProvider {

    public ItemCustomAxe(int n) {
        super(n, EnumToolMaterial.DIAMOND);
    }

    public String getTextureFile() {
        return "/eloraam/world/worlditems1.png";
    }
}
