/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  net.minecraft.server.EntityPlayer
 *  net.minecraft.server.World
 *  org.bukkit.craftbukkit.CraftServer
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 *  org.bukkit.plugin.PluginManager
 */
package eloraam.machine;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class FakePlayer {
    private static EntityPlayer fakePlayer = null;
    public static String name = "[RedPower]";
    public static boolean doLogin = false;

    public static EntityPlayer get(World world) {
        if (fakePlayer == null) {
            fakePlayer = new EntityPlayerFake(name, world);
            System.out.println("[RedPower] FakePlayer created.");
            if (doLogin) {
                PlayerLoginEvent playerLoginEvent = new PlayerLoginEvent((Player) fakePlayer.getBukkitEntity());
                world.getServer().getPluginManager().callEvent((Event) playerLoginEvent);
                if (playerLoginEvent.getResult() != PlayerLoginEvent.Result.ALLOWED) {
                    System.err.println("[RedPower] Warning: FakePlayer login event was disallowed. Ignoring, but this may cause confused plugins.");
                }
                CraftPlayer craftPlayer = fakePlayer.getBukkitEntity();
                PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent((Player) craftPlayer, "");
                System.out.println("[RedPower] Sending PlayerJoinEvent=" + (Object) playerJoinEvent + " with CraftPlayer=" + (Object) craftPlayer);
                world.getServer().getPluginManager().callEvent((Event) playerJoinEvent);
            } else {
                System.out.println("[RedPower] Login is disabled.");
            }
        }
        return fakePlayer;
    }

    public static CraftPlayer getBukkitEntity(World world) {
        return FakePlayer.get(world).getBukkitEntity();
    }
}

