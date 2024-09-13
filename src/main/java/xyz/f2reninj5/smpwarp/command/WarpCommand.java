package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.event.TeleportEvent;
import xyz.f2reninj5.smpwarp.model.Warp;
import xyz.f2reninj5.smpwarp.SMPWarp;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length == 0) {
            try {
                return SMPWarp.getWarpDatabase().getWarpGroups();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (args.length == 1) {
            try {
                return SMPWarp.getWarpDatabase().getWarpGroups(args[0]);
            } catch (SQLException exception) {
                throw new RuntimeException(exception.getMessage());
            }
        } else if (args.length == 2) {
            try {
                return SMPWarp.getWarpDatabase().getWarpNames(args[0], args[1]);
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        }
        return List.of();
    }
}
