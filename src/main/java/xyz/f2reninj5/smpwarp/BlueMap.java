package xyz.f2reninj5.smpwarp;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import org.bukkit.World;
import xyz.f2reninj5.smpwarp.model.Warp;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BlueMap {

    public static POIMarker warpToMarker(Warp warp) {
        String label = warp.name;
        if (!Objects.equals(warp.group, "")) {
            label = warp.group + " " + label;
        }

        return POIMarker.builder()
            .label(label)
            .position(
                warp.location.getX(),
                warp.location.getY(),
                warp.location.getZ()
            )
            .build();
    }

    public static void addMarker(Warp warp) {
        BlueMapAPI.getInstance().flatMap(api -> api.getWorld(warp.location.getWorld())).ifPresent(mapWorld -> {
            for (BlueMapMap map : mapWorld.getMaps()) {
                POIMarker marker = warpToMarker(warp);
                Map<String, MarkerSet> markerSets = map.getMarkerSets();
                MarkerSet markerSet = markerSets.get("warps");
                if (markerSet == null) {
                    markerSet = MarkerSet.builder().label("Warps").build();
                }
                markerSets.put("warps", markerSet);
                markerSet.put(marker.getLabel(), marker);
            }
        });
    }

    public static void removeMarker(String group, String name) {
        BlueMapAPI.getInstance().ifPresent(api ->
            api.getMaps().forEach(map -> {
                MarkerSet markerSet = map.getMarkerSets().get("warps");
                if (markerSet != null) {
                    String label = name;
                    if (!Objects.equals(group, "")) {
                        label = group + " " + label;
                    }
                    markerSet.remove(label);
                }
            })
        );
    }

    public static Map<World, MarkerSet> warpsToMarkerSets(List<Warp> warps) {
        Map<World, MarkerSet> markerSets = new HashMap<>();

        for (Warp warp : warps) {
            final World world = warp.location.getWorld();

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
