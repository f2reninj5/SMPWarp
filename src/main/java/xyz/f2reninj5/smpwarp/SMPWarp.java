package xyz.f2reninj5.smpwarp;

import de.bluecolored.bluemap.api.BlueMapAPI;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.f2reninj5.smpwarp.command.*;
import xyz.f2reninj5.smpwarp.database.WarpDatabase;
import xyz.f2reninj5.smpwarp.listener.TeleportListener;

import java.sql.SQLException;
import java.util.List;

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

        saveDefaultConfig();

        try {
            warpDatabase = new WarpDatabase(getDataFolder().getAbsolutePath() + "/smpwarp.db");
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Failed to connect to the database! " + exception.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        getServer().getPluginManager().registerEvents(new TeleportListener(), this);

        if (getConfig().getBoolean("enable-bluemap-markers")) {
            BlueMapAPI.onEnable(BlueMap::onEnable);
            getLogger().info("BlueMap markers enabled!");
        }

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
            commands.register(
                "removewarp",
                "removes a warp",
                new RemoveWarpCommand()
            );
            commands.register(
                "movewarp",
                "moves a warp to current location",
                new MoveWarpCommand()
            );
            commands.register(
                "renamewarp",
                "rename a warp",
                new RenameWarpCommand()
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
