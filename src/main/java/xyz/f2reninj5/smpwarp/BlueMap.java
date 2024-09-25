package xyz.f2reninj5.smpwarp;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.model.Warp;
import xyz.f2reninj5.smpwarp.model.WarpIdentifier;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BlueMap {

    private static String warpIdentifierToLabel(@NotNull WarpIdentifier identifier) {
        return identifier.join(" ");
    }

    public static POIMarker warpToMarker(@NotNull Warp warp) {
        String label = warpIdentifierToLabel(warp.getIdentifier());

        return POIMarker.builder()
            .label(label)
            .position(
                warp.getLocation().getX(),
                warp.getLocation().getY(),
                warp.getLocation().getZ()
            )
            .build();
    }

    public static void addMarker(@NotNull Warp warp) {
        BlueMapAPI.getInstance().flatMap(api -> api.getWorld(warp.getLocation().getWorld())).ifPresent(mapWorld -> {
            for (BlueMapMap map : mapWorld.getMaps()) {
                POIMarker marker = warpToMarker(warp);
                Map<String, MarkerSet> markerSets = map.getMarkerSets();
                MarkerSet markerSet = markerSets.get("warps");
                if (markerSet == null) {
                    markerSet = MarkerSet.builder()
                        .label("Warps")
                        .defaultHidden(SMPWarp.getPlugin().getConfig().getBoolean("hide-markers-by-default"))
                        .build();
                }
                markerSets.put("warps", markerSet);
                markerSet.put(marker.getLabel(), marker);
            }
        });
    }

    public static void removeMarker(@NotNull WarpIdentifier identifier) {
        BlueMapAPI.getInstance().ifPresent(api ->
            api.getMaps().forEach(map -> {
                MarkerSet markerSet = map.getMarkerSets().get("warps");
                if (markerSet != null) {
                    String label = warpIdentifierToLabel(identifier);
                    markerSet.remove(label);
                }
            })
        );
    }

    public static Map<World, MarkerSet> warpsToMarkerSets(List<Warp> warps) {
        Map<World, MarkerSet> markerSets = new HashMap<>();

        for (Warp warp : warps) {
            final World world = warp.getLocation().getWorld();

            if (!markerSets.containsKey(world)) {
                MarkerSet markerSet = MarkerSet.builder().label("Warps").build();
                markerSets.put(world, markerSet);
            }

            POIMarker marker = warpToMarker(warp);
            markerSets.get(world).put(marker.getLabel(), marker);
        }

        return markerSets;
    }

    public static Map<World, MarkerSet> getMarkerSets() {
        try {
            return warpsToMarkerSets(SMPWarp.getWarpDatabase().getAllWarps());
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void onEnable(BlueMapAPI api) {
        getMarkerSets().forEach((world, markerSet) ->
            api.getWorld(world).ifPresent(mapWorld -> {
                for (BlueMapMap map : mapWorld.getMaps()) {
                    map.getMarkerSets().put("warps", markerSet);
                }
            })
        );
    }
}
