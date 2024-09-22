package xyz.f2reninj5.smpwarp.model;

import org.bukkit.Location;

public class Warp {
    private WarpIdentifier identifier;
    private Location location;
    private String createdBy;

    public Warp(String group, String name, Location location, String createdBy) {
        this.identifier = new WarpIdentifier(group, name);
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

    public String getCreatedBy() {
        return createdBy;
    }
}
