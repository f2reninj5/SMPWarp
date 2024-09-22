package xyz.f2reninj5.smpwarp.model;

import org.bukkit.Location;

public class Warp {
    private String name;
    private String group;
    private Location location;
    public String createdBy;

    public Warp(String name, String group, Location location, String createdBy) {
        this.name = name;
        this.group = group;
        this.location = location;
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public Location getLocation() {
        return location;
    }
}
