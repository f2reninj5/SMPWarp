package xyz.f2reninj5.smpwarp.model;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Warp {
    private WarpIdentifier identifier;
    private Location location;
    private Player createdBy;

    public Warp(WarpIdentifier identifier, Location location, Player createdBy) {
        this.identifier = identifier;
        this.location = location;
        this.createdBy = createdBy;
    }

    public WarpIdentifier getIdentifier() {
        return identifier;
    }

    public String getGroup() {
        return identifier.getGroup();
    }

    public String getName() {
        return identifier.getName();
    }

    public Location getLocation() {
        return location;
    }

    public Player getCreatedBy() {
        return createdBy;
    }
}
