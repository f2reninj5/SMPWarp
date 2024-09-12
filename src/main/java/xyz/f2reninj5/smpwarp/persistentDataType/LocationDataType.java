package xyz.f2reninj5.smpwarp.persistentDataType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LocationDataType implements PersistentDataType<String, Location> {

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<Location> getComplexType() {
        return Location.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull Location complex, @NotNull PersistentDataAdapterContext context) {
        return String.join(" ",
            complex.getWorld().getUID().toString(),
            Double.toString(complex.getX()),
            Double.toString(complex.getY()),
            Double.toString(complex.getZ()),
            Float.toString(complex.getYaw()),
            Float.toString(complex.getPitch())
        );
    }

    @Override
    public @NotNull Location fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        String[] arguments = primitive.split(" ", 6);
        Location complex = new Location(
            Bukkit.getWorld(UUID.fromString(arguments[0])),
            Double.parseDouble(arguments[1]),
            Double.parseDouble(arguments[2]),
            Double.parseDouble(arguments[3]),
            Float.parseFloat(arguments[4]),
            Float.parseFloat(arguments[5])
        );
        return complex;
    }
}
