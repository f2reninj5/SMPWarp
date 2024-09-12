package xyz.f2reninj5.smpwarp.persistentDataType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class LocationDataType implements PersistentDataType<String[], Location> {

    @Override
    public @NotNull Class<String[]> getPrimitiveType() {
        return String[].class;
    }

    @Override
    public @NotNull Class<Location> getComplexType() {
        return Location.class;
    }

    @Override
    public String @NotNull [] toPrimitive(@NotNull Location complex, @NotNull PersistentDataAdapterContext context) {
        List<String> list = List.of(
            complex.getWorld().getUID().toString(),
            Double.toString(complex.getX()),
            Double.toString(complex.getY()),
            Double.toString(complex.getZ()),
            Float.toString(complex.getYaw()),
            Float.toString(complex.getPitch())
        );
        return list.toArray(new String[0]);
    }

    @Override
    public @NotNull Location fromPrimitive(String @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        Location location = new Location(
            Bukkit.getWorld(UUID.fromString(primitive[0])),
            Double.parseDouble(primitive[1]),
            Double.parseDouble(primitive[2]),
            Double.parseDouble(primitive[3]),
            Float.parseFloat(primitive[4]),
            Float.parseFloat(primitive[5])
        );
        return location;
    }
}
