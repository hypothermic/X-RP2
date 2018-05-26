/* X-RP - decompiled with CFR */
package eloraam.world;

import forge.ITextureProvider;
import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.ItemSword;

public class ItemCustomSword extends ItemSword implements ITextureProvider {

    public ItemCustomSword(int n) {
	super(n, EnumToolMaterial.DIAMOND);
    }

    public String getTextureFile() {
	return "/eloraam/world/worlditems1.png";
    }
}
