/* X-RP - decompiled with CFR */
package eloraam.core;

import java.io.File;
import java.util.Random;
import net.minecraft.server.RedPowerCore;
import net.minecraft.server.World;

public class DiskLib {

    public static File getSaveDir(World world) {
	File file = new File(RedPowerCore.getSaveDir(world), "redpower");
	file.mkdirs();
	return file;
    }

    public static String generateSerialNumber(World world) {
	String string = "";
	for (int i = 0; i < 16; ++i) {
	    string = string + String.format("%01x", world.random.nextInt(16));
	}
	return string;
    }

    public static File getDiskFile(File file, String string) {
	return new File(file, String.format("disk_%s.img", string));
    }
}
