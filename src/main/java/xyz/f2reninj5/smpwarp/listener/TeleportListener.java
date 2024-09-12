package xyz.f2reninj5.smpwarp.listener;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.f2reninj5.smpwarp.SMPWarp;
import xyz.f2reninj5.smpwarp.event.TeleportEvent;
import xyz.f2reninj5.smpwarp.persistentDataType.LocationDataType;

public class TeleportListener implements Listener {

    @EventHandler
    public void onTeleport(TeleportEvent event) {
        Player teleportee = event.getTeleportee();
        Location source = event.getSource();

        NamespacedKey key = new NamespacedKey(SMPWarp.getPlugin(), "back");
        teleportee.getPersistentDataContainer().set(key, new LocationDataType(), source);
    }
}
