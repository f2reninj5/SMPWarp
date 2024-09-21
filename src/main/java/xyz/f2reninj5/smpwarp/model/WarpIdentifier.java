package xyz.f2reninj5.smpwarp.model;

import org.jetbrains.annotations.NotNull;

public class WarpIdentifier {
    private final String group;
    private final String name;

    public static WarpIdentifier commandArgumentsToWarpIdentifier(@NotNull String @NotNull [] arguments) {
        if (arguments.length < 1) {
            throw new IllegalArgumentException("Arguments must contain at least one argument");
        } else if (arguments.length == 1) {
            return new WarpIdentifier("", arguments[0]);
        } else {
            return new WarpIdentifier(arguments[0], arguments[1]);
        }
    }

    public WarpIdentifier(@NotNull String group, @NotNull String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        this.group = group.strip().toLowerCase();
        this.name = name.strip().toLowerCase();
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public boolean hasGroup() {
        return !group.isEmpty();
    }
}
