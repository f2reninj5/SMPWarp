package xyz.f2reninj5.smpwarp;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.f2reninj5.smpwarp.command.SetWarpCommand;

public final class SMPWarp extends JavaPlugin {

    @Override
    public void onEnable() {
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("setwarp", "errr errr", new SetWarpCommand());
        });

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
