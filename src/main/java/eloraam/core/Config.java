/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.CoreProxy;
import eloraam.core.TagFile;
import forge.MinecraftForge;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;
import net.minecraft.server.Block;
import net.minecraft.server.Item;
import net.minecraft.server.LocaleLanguage;
import net.minecraft.server.RedPowerCore;

public class Config {

    static boolean[] reservedIds = new boolean[32768];
    static File configDir = null;
    static File configFile = null;
    static TagFile config = null;
    static Properties translateTable = null;
    static Properties rpTranslateTable = null;
    static boolean autoAssign = true;

    public static void loadConfig() {
	File object;
	config = new TagFile();
	InputStream inputStream = RedPowerCore.class.getResourceAsStream("/eloraam/core/default.cfg");
	config.readStream(inputStream);
	if (configDir == null) {
	    object = CoreProxy.getMinecraftDir();
	    object = new File(object, "/redpower/");
	    object.mkdir();
	    configDir = object;
	    configFile = new File(object, "redpower.cfg");
	}
	if (configFile.exists()) {
	    config.readFile(configFile);
	}
	config.commentFile("RedPower 2 Configuration");
	for (Object object2 : config.query("blocks.%.%.id")) {
	    Config.reservedIds[Config.config.getInt((String) object2)] = true;
	}
	for (Object object2 : config.query("items.%.%.id")) {
	    Config.reservedIds[Config.config.getInt((String) object2) + 256] = true;
	}
	if (rpTranslateTable == null) {
	    rpTranslateTable = new Properties();
	}
	try {
	    rpTranslateTable.load(RedPowerCore.class.getResourceAsStream("/eloraam/core/redpower.lang"));
	    object = new File(configDir, "redpower.lang");
	    if (object.exists()) {
		Object object2;
		object2 = new FileInputStream((File) object);
		rpTranslateTable.load((InputStream) object2);
	    }
	} catch (IOException iOException) {
	    iOException.printStackTrace();
	}
	Config.getTranslateTable().putAll(rpTranslateTable);
	autoAssign = config.getInt("settings.core.autoAssign") > 0;
	config.addInt("settings.core.autoAssign", 0);
	config.commentTag("settings.core.autoAssign", "Automatically remap conflicting IDs.\nWARNING: May corrupt existing worlds");
    }

    public static void saveConfig() {
	config.saveFile(configFile);
	try {
	    File file = new File(configDir, "redpower.lang");
	    FileOutputStream fileOutputStream = new FileOutputStream(file);
	    rpTranslateTable.store(fileOutputStream, "RedPower Language File");
	} catch (IOException iOException) {
	    iOException.printStackTrace();
	}
    }

    public static void addName(String string, String string2) {
	if (rpTranslateTable.get(string) != null) {
	    return;
	}
	rpTranslateTable.put(string, string2);
	translateTable.put(string, string2);
    }

    public static Properties getTranslateTable() {
	if (translateTable != null) {
	    return translateTable;
	}
	try {
	    Field field = LocaleLanguage.class.getDeclaredFields()[1];
	    field.setAccessible(true);
	    translateTable = (Properties) field.get((Object) LocaleLanguage.a());
	} catch (IllegalAccessException illegalAccessException) {
	    illegalAccessException.printStackTrace();
	}
	return translateTable;
    }

    public static int getItemID(String string) {
	int n = config.getInt(string);
	if (Item.byId[256 + n] == null) {
	    return n;
	}
	if (!autoAssign) {
	    MinecraftForge.killMinecraft((String) "RedPowerCore", (String) String.format("ItemID %d exists, autoAssign is disabled.", n));
	    return -1;
	}
	for (int i = 1024; i < 32000; ++i) {
	    if (reservedIds[i] || Item.byId[i] != null)
		continue;
	    config.addInt(string, i - 256);
	    return i;
	}
	MinecraftForge.killMinecraft((String) "RedPowerCore", (String) "Out of available ItemIDs, could not autoassign!");
	return -1;
    }

    public static int getBlockID(String string) {
	int n = config.getInt(string);
	if (Block.byId[n] == null) {
	    return n;
	}
	if (!autoAssign) {
	    MinecraftForge.killMinecraft((String) "RedPowerCore", (String) String.format("BlockID %d occupied by %s, autoAssign is disabled.", n, Block.byId[n].getClass().getName()));
	    return -1;
	}
	for (int i = 255; i >= 20; --i) {
	    if (reservedIds[i] || Block.byId[i] != null)
		continue;
	    config.addInt(string, i);
	    return i;
	}
	MinecraftForge.killMinecraft((String) "RedPowerCore", (String) "Out of available BlockIDs, could not autoassign!");
	return -1;
    }

    public static int getInt(String string) {
	return config.getInt(string);
    }

    public static String getString(String string) {
	return config.getString(string);
    }
}
