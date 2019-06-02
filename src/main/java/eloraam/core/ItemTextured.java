/* X-RP - decompiled with CFR */
package eloraam.core;

import forge.ITextureProvider;
import net.minecraft.server.Item;

public class ItemTextured extends Item implements ITextureProvider {

    String textureFile;

    public ItemTextured(int n, String string) {
        super(n);
        this.textureFile = string;
    }

    public ItemTextured(int n, int n2, String string) {
        super(n);
        this.textureId = n2;
        this.textureFile = string;
    }

    public String getTextureFile() {
        return this.textureFile;
    }
}
