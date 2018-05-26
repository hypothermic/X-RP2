/* X-RP - decompiled with CFR */
package eloraam.world;

import forge.ITextureProvider;
import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.ItemPickaxe;

public class ItemCustomPickaxe extends ItemPickaxe implements ITextureProvider {

    public ItemCustomPickaxe(int n) {
	super(n, EnumToolMaterial.WOOD);
    }

    public String getTextureFile() {
	return "/eloraam/world/worlditems1.png";
    }
}
