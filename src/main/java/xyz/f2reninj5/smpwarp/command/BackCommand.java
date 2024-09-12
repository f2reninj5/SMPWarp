package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.SMPWarp;
import xyz.f2reninj5.smpwarp.event.TeleportEvent;
import xyz.f2reninj5.smpwarp.persistentDataType.LocationDataType;

public class BackCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        NamespacedKey key = new NamespacedKey(SMPWarp.getPlugin(), "back");
        PersistentDataContainer container = stack.getExecutor().getPersistentDataContainer();
        if (container.has(key, new LocationDataType())) {
            Location destination = container.get(key, new LocationDataType());
            TeleportEvent teleportEvent = new TeleportEvent((Player) stack.getExecutor(), stack.getExecutor().getLocation(), destination);
            teleportEvent.callEvent();
            stack.getExecutor().teleport(destination);
        }
    }
}
