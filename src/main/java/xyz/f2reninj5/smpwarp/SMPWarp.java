package xyz.f2reninj5.smpwarp;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.f2reninj5.smpwarp.command.BackCommand;
import xyz.f2reninj5.smpwarp.command.CreateWarpCommand;
import xyz.f2reninj5.smpwarp.command.WarpCommand;
import xyz.f2reninj5.smpwarp.database.WarpDatabase;
import xyz.f2reninj5.smpwarp.listener.TeleportListener;
import xyz.f2reninj5.smpwarp.model.Warp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SMPWarp extends JavaPlugin {

    private static SMPWarp plugin;
    private static WarpDatabase warpDatabase;

    public static SMPWarp getPlugin() {
        return plugin;
    }

    public static WarpDatabase getWarpDatabase() {
        return warpDatabase;
    }

    @Override
    public void onEnable() {
        plugin = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        try {
            warpDatabase = new WarpDatabase(getDataFolder().getAbsolutePath() + "/smpwarp.db");
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Failed to connect to the database! " + exception.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        getServer().getPluginManager().registerEvents(new TeleportListener(), this);

        BlueMapAPI.getInstance().ifPresent(api -> {
            Map<World, List<Warp>> warps = new HashMap<>();

            try {
                warpDatabase.getAllWarps().forEach(warp -> {
                    if (!warps.containsKey(warp.location.getWorld())) {
                        warps.put(warp.location.getWorld(), new ArrayList<>());
                    }
                    warps.get(warp.location.getWorld()).add(warp);
                });

                warps.keySet().forEach(w -> {
                    api.getWorld(w).ifPresent(world -> {
                        MarkerSet markerSet = MarkerSet.builder().label("Warps").build();
                        markerSet.getMarkers().put("id", POIMarker.builder().label(w.getName()).position(0.0, 0.0, 0.0).build());

                        for (BlueMapMap map : world.getMaps()) {
                            map.getMarkerSets().put("someid", markerSet);
                        }
                    });
                });
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });

        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(
                "createwarp",
                "errr errr",
                List.of("newwarp", "setwarp", "cwarp", "nwarp", "swarp"),
                new CreateWarpCommand()
            );
            commands.register(
                    "warp",
                    "eee eee",
                    new WarpCommand()
            );
            commands.register(
                "back",
                "teleport back innit",
                new BackCommand()
            );
        });

    }

    @Override
    public void onDisable() {
        try {
            warpDatabase.closeConnection();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
