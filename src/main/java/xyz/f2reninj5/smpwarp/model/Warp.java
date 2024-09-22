package xyz.f2reninj5.smpwarp.model;

import org.bukkit.Location;

public class Warp {
    private WarpIdentifier identifier;
    private Location location;
    private String createdBy;

    public Warp(WarpIdentifier identifier, Location location, String createdBy) {
        this.identifier = identifier;
        this.location = location;
        this.createdBy = createdBy;
    }

    public Warp(String group, String name, Location location, String createdBy) {
        this(new WarpIdentifier(group, name), location, createdBy);
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
