/* X-RP - decompiled with CFR */
package eloraam.core;

import eloraam.core.IHandleGuiEvent;
import eloraam.core.IHandlePackets;
import eloraam.core.Packet211TileDesc;
import eloraam.core.Packet212GuiEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.server.Block;
import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ModLoader;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet3Chat;
import net.minecraft.server.ServerConfigurationManager;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public class CoreProxy {

    public static void AddLocalization(String arg, String arg0) {
    }

    public static void AddName(Object arg, String arg0) {
    }

    public static float getBrightness(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3) {
	return 0.0f;
    }

    public static int getLightBrightnessForSkyBlocks(IBlockAccess arg, int arg0, int arg1, int arg2, int arg3) {
	return 0;
    }

    public static File getMinecraftDir() {
	try {
	    String arg = ModLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
	    return new File(arg.substring(0, arg.lastIndexOf(47)));
	} catch (URISyntaxException arg0) {
	    return null;
	}
    }

    public static EntityHuman getViewingPlayer() {
	return null;
    }

    public static boolean isClient(World arg) {
	return false;
    }

    public static boolean isServer() {
	return true;
    }

    public static void processPacket211(Packet211TileDesc arg, NetHandler arg0) {
	if (arg0 instanceof NetServerHandler) {
	    TileEntity arg4;
	    NetServerHandler arg1 = (NetServerHandler) arg0;
	    EntityPlayer arg2 = arg1.getPlayerEntity();
	    World arg3 = arg2.world;
	    if (arg3.isLoaded(arg.xCoord, arg.yCoord, arg.zCoord) && (arg4 = arg3.getTileEntity(arg.xCoord, arg.yCoord, arg.zCoord)) instanceof IHandlePackets) {
		((IHandlePackets) arg4).handlePacket(arg);
		return;
	    }
	}
    }

    public static void processPacket212(Packet212GuiEvent arg, NetHandler arg0) {
	if (arg0 instanceof NetServerHandler) {
	    NetServerHandler arg1 = (NetServerHandler) arg0;
	    EntityPlayer arg2 = arg1.getPlayerEntity();
	    if (arg2.activeContainer != null && arg2.activeContainer.windowId == arg.windowId && arg2.activeContainer instanceof IHandleGuiEvent) {
		IHandleGuiEvent arg3 = (IHandleGuiEvent) arg2.activeContainer;
		arg3.handleGuiEvent(arg);
	    }
	}
    }

    public static void randomDisplayTick(World arg, Block arg0, int arg1, int arg2, int arg3, Random arg4) {
    }

    public static void sendPacketToPosition(Packet arg, int arg0, int arg1) {
	ChunkCoordIntPair arg2 = new ChunkCoordIntPair(arg0 >> 4, arg1 >> 4);
	List arg3 = ModLoader.getMinecraftServerInstance().serverConfigurationManager.players;
	int arg4 = 0;
	while (arg4 < arg3.size()) {
	    EntityPlayer arg5 = (EntityPlayer) arg3.get(arg4);
	    if (arg5.playerChunkCoordIntPairs.contains((Object) arg2)) {
		arg5.netServerHandler.sendPacket(arg);
	    }
	    ++arg4;
	}
    }

    public static void sendPacketToServer(Packet arg) {
    }

    public static void writeChat(EntityHuman arg, String arg0) {
	if (arg instanceof EntityPlayer) {
	    EntityPlayer arg1 = (EntityPlayer) arg;
	    arg1.netServerHandler.sendPacket((Packet) new Packet3Chat(arg0));
	}
    }
}
