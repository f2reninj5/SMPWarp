package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.event.TeleportEvent;
import xyz.f2reninj5.smpwarp.model.Warp;
import xyz.f2reninj5.smpwarp.SMPWarp;

import java.sql.SQLException;

public class WarpCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length != 2) {
            return;
        }

        try {
            Warp warp = SMPWarp.getWarpDatabase().getWarp(args[1], args[0]);
            if (warp == null) {
                return;
            }

            TeleportEvent teleportEvent = new TeleportEvent((Player) stack.getExecutor(), stack.getExecutor().getLocation(), warp.location);
            teleportEvent.callEvent();
            stack.getExecutor().teleport(warp.location);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
