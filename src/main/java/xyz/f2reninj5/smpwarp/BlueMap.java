package xyz.f2reninj5.smpwarp;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import org.bukkit.World;
import xyz.f2reninj5.smpwarp.model.Warp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlueMap {

    public static void onEnable(BlueMapAPI api) {
        Map<World, List<Warp>> warps = new HashMap<>();

        try {
            SMPWarp.getWarpDatabase().getAllWarps().forEach(warp -> {
                if (!warps.containsKey(warp.location.getWorld())) {
                    warps.put(warp.location.getWorld(), new ArrayList<>());
                }
                warps.get(warp.location.getWorld()).add(warp);
            });

            warps.keySet().forEach(w -> {
                api.getWorld(w).ifPresent(world -> {
                    MarkerSet markerSet = MarkerSet.builder().label("Warps").build();
//                        markerSet.getMarkers().put("id", POIMarker.builder().label(w.getName()).position(0.0, 0.0, 0.0).build());

                    for (Warp warp : warps.get(w)) {
                        markerSet.getMarkers().put(warp.group + warp.name,
                                POIMarker.builder().label(warp.group + warp.name).position(warp.location.getX(),
                                        warp.location.getY(), warp.location.getZ()).build());
                    }

                    for (BlueMapMap map : world.getMaps()) {
                        map.getMarkerSets().put("someid", markerSet);
                    }
                });
            });
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
