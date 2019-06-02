/* X-RP - decompiled with CFR */
package eloraam.world;

import forge.ITextureProvider;
import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.ItemHoe;

public class ItemCustomHoe extends ItemHoe implements ITextureProvider {

    public ItemCustomHoe(int n) {
        super(n, EnumToolMaterial.WOOD);
    }

    public String getTextureFile() {
        return "/eloraam/world/worlditems1.png";
    }
}
