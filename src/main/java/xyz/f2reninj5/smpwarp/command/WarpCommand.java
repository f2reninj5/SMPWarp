package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.event.TeleportEvent;
import xyz.f2reninj5.smpwarp.model.Warp;
import xyz.f2reninj5.smpwarp.SMPWarp;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class WarpCommand implements BasicCommand {

    private Component getSuccessMessage(String warpGroup, String warpName) {
        TextComponent.Builder builder = text()
            .content("Warped to ").color(GOLD);

        if (warpGroup != "") {
            builder
                .append(text(warpGroup, RED))
                .append(text(": ", GOLD));
        }

        return builder
            .append(text(warpName, RED))
            .append(text(".", GOLD))
            .build();
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length < 1) {
            return;
        }

        String group = "";
        String name = args[0];

        if (args.length > 1) {
            group = args[0];
            name = args[1];
        }

        try {
            Warp warp = SMPWarp.getWarpDatabase().getWarp(name, group);
            if (warp == null) {
                return;
            }

            TeleportEvent teleportEvent = new TeleportEvent((Player) stack.getExecutor(), stack.getExecutor().getLocation(), warp.location);
            teleportEvent.callEvent();
            stack.getExecutor().teleport(warp.location);
            stack.getExecutor().sendMessage(getSuccessMessage(group, name));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length == 0) {
            try {
                List<String> suggestions = SMPWarp.getWarpDatabase().getWarpGroups();
                suggestions.addAll(SMPWarp.getWarpDatabase().getWarpNames("", ""));
                return suggestions;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (args.length == 1) {
            try {
                List<String> suggestions = SMPWarp.getWarpDatabase().getWarpGroups(args[0]);
                suggestions.addAll(SMPWarp.getWarpDatabase().getWarpNames("", args[0]));
                return suggestions;
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
