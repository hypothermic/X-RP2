/* X-RP - decompiled with CFR */
package eloraam.world;

import forge.ITextureProvider;
import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.ItemSpade;

public class ItemCustomShovel extends ItemSpade implements ITextureProvider {

    public ItemCustomShovel(int n) {
	super(n, EnumToolMaterial.WOOD);
    }

    public String getTextureFile() {
	return "/eloraam/world/worlditems1.png";
    }
}
