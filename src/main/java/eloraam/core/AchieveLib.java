/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.CoreLib;
import forge.AchievementPage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import net.minecraft.server.Achievement;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Statistic;

public class AchieveLib {

    private static HashMap achievelist = new HashMap();
    public static AchievementPage achievepage = new AchievementPage("RedPower", new Achievement[0]);
    private static TreeMap achievebycraft = new TreeMap(CoreLib.itemStackComparator);
    private static TreeMap achievebyfurnace = new TreeMap(CoreLib.itemStackComparator);
    private static TreeMap achievebyalloy = new TreeMap(CoreLib.itemStackComparator);

    public static void registerAchievement(int n, String string, int n2, int n3, ItemStack itemStack, Object object, boolean bl) {
	Achievement achievement = null;
	if (object instanceof Achievement) {
	    achievement = (Achievement) object;
	} else if (object instanceof String) {
	    achievement = (Achievement) achievelist.get((String) object);
	}
	Achievement achievement2 = new Achievement(n, string, n2, n3, itemStack, achievement);
	achievement2.c();
	if (bl) {
	    achievement2.b();
	}
	achievelist.put(string, achievement2);
	achievepage.getAchievements().add(achievement2);
    }

    public static void registerAchievement(int n, String string, int n2, int n3, ItemStack itemStack, Object object) {
	AchieveLib.registerAchievement(n, string, n2, n3, itemStack, object, false);
    }

    public static void addCraftingAchievement(ItemStack itemStack, String string) {
	Achievement achievement = (Achievement) achievelist.get(string);
	if (achievement == null) {
	    return;
	}
	achievebycraft.put(itemStack, achievement);
    }

    public static void addAlloyAchievement(ItemStack itemStack, String string) {
	Achievement achievement = (Achievement) achievelist.get(string);
	if (achievement == null) {
	    return;
	}
	achievebyalloy.put(itemStack, achievement);
    }

    public static void addFurnaceAchievement(ItemStack itemStack, String string) {
	Achievement achievement = (Achievement) achievelist.get(string);
	if (achievement == null) {
	    return;
	}
	achievebyfurnace.put(itemStack, achievement);
    }

    public static void triggerAchievement(EntityHuman entityHuman, String string) {
	Achievement achievement = (Achievement) achievelist.get(string);
	if (achievement == null) {
	    return;
	}
	entityHuman.a((Statistic) achievement);
    }

    public static void onCrafting(EntityHuman entityHuman, ItemStack itemStack) {
	Achievement achievement = (Achievement) achievebycraft.get((Object) itemStack);
	if (achievement == null) {
	    return;
	}
	entityHuman.a((Statistic) achievement);
    }

    public static void onFurnace(EntityHuman entityHuman, ItemStack itemStack) {
	Achievement achievement = (Achievement) achievebyfurnace.get((Object) itemStack);
	if (achievement == null) {
	    return;
	}
	entityHuman.a((Statistic) achievement);
    }

    public static void onAlloy(EntityHuman entityHuman, ItemStack itemStack) {
	Achievement achievement = (Achievement) achievebyalloy.get((Object) itemStack);
	if (achievement == null) {
	    return;
	}
	entityHuman.a((Statistic) achievement);
    }
}
