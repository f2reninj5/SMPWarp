package xyz.f2reninj5.smpwarp.model;

import org.bukkit.Location;

public class Warp {
    private String name;
    public String group;
    public Location location;
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
}
