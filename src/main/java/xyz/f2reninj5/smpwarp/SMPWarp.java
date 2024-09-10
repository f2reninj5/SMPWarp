package xyz.f2reninj5.smpwarp;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.f2reninj5.smpwarp.command.SetWarpCommand;
import xyz.f2reninj5.smpwarp.database.WarpDatabase;

import java.sql.SQLException;

public final class SMPWarp extends JavaPlugin {

    private static SMPWarp plugin;
    private WarpDatabase warpDatabase;

    public static SMPWarp getPlugin() {
        return plugin;
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

        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("setwarp", "errr errr", new SetWarpCommand());
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
