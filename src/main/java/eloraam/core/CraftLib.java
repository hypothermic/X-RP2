/* X-RP - decompiled with CFR */
// NOTE: This whole class has been identified as unstable. Use with caution.
package eloraam.core;

import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

import java.util.*;

public class CraftLib {

    static List alloyRecipes = new ArrayList();
    public static HashSet damageOnCraft = new HashSet();
    public static HashMap damageContainer = new HashMap();

    public static void addAlloyResult(ItemStack itemStack, ItemStack[] arritemStack) {
        alloyRecipes.add(Arrays.asList(new Object[]{arritemStack, itemStack}));
    }

    public static ItemStack getAlloyResult(ItemStack[] arritemStack, int n, int n2, boolean bl) {
        block0:
        for (ItemStack[] arritemStack2 : (List<ItemStack[]>) alloyRecipes) { // X-RP2: cast alloyRecipes
            ItemStack[] arritemStack3;
            int n3;
            Object[] arrobject = arritemStack2;
            ItemStack[] arritemStack4 = arritemStack3 = (ItemStack[]) arrobject[0];
            int n4 = arritemStack4.length;
            for (int i = 0; i < n4; ++i) {
                ItemStack itemStack = arritemStack4[i];
                n3 = itemStack.count;
                for (int j = n; j < n2; ++j) {
                    if (arritemStack[j] == null)
                        continue;
                    if (arritemStack[j].doMaterialsMatch(itemStack)) {
                        n3 -= arritemStack[j].count;
                    }
                    if (n3 <= 0)
                        break;
                }
                if (n3 > 0)
                    continue block0;
            }
            if (bl) {
                block3:
                for (ItemStack itemStack : arritemStack3) {
                    int n5 = itemStack.count;
                    for (n3 = n; n3 < n2; ++n3) {
                        if (arritemStack[n3] == null || !arritemStack[n3].doMaterialsMatch(itemStack))
                            continue;
                        if ((n5 -= arritemStack[n3].count) < 0) {
                            arritemStack[n3].count = -n5;
                        } else {
                            arritemStack[n3] = arritemStack[n3].getItem().k() ? new ItemStack(arritemStack[n3].getItem().j()) : null;
                        }
                        if (n5 <= 0)
                            continue block3;
                    }
                }
            }
            return (ItemStack) arrobject[1];
        }
        return null;
    }

    public static void addDamageOnCraft(Item item) {
        damageOnCraft.add(item.id);
    }

    public static void addDamageContainer(Item item, Item item2) {
        damageOnCraft.add(item.id);
        damageContainer.put(item.id, item2.id);
    }
}
