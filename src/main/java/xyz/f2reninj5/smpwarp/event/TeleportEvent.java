package xyz.f2reninj5.smpwarp.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeleportEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player teleportee;
    private final Location destination;

    public TeleportEvent(final Player teleportee, final Location destination) {
        this.teleportee = teleportee;
        this.destination = destination;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public Player getTeleportee() {
        return teleportee;
    }

    public Location getDestination() {
        return destination;
    }
}
