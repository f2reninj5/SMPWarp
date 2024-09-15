package xyz.f2reninj5.smpwarp;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.f2reninj5.smpwarp.event.TeleportEvent;

public class Teleport {

    public static boolean teleport(Player player, Location location) {
        TeleportEvent teleportEvent = new TeleportEvent(player, player.getLocation(), location);
        teleportEvent.callEvent();
        return player.teleport(location);
    }
}
