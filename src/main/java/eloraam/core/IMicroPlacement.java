/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.WorldCoord;
import java.util.ArrayList;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public interface IMicroPlacement {

    public boolean onPlaceMicro(ItemStack var1, EntityHuman var2, World var3, WorldCoord var4, int var5);

    public String getMicroName(int var1, int var2);

    public void addCreativeItems(int var1, ArrayList var2);
}
