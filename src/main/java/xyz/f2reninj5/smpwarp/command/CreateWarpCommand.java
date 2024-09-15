package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.SMPWarp;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class CreateWarpCommand implements BasicCommand {

    private Component getSuccessMessage(String warpGroup, String warpName) {
        TextComponent.Builder builder = text()
            .content("Created warp ").color(GOLD);

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

    private Component getFailureMessage(String warpGroup, String warpName) {
        TextComponent.Builder builder = text()
            .content("Warp ").color(GOLD);

        if (warpGroup != "") {
            builder
                .append(text(warpGroup, RED))
                .append(text(": ", GOLD));
        }

        return builder
            .append(text(warpName, RED))
            .append(text(" already exists.", GOLD))
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
            if (SMPWarp.getWarpDatabase().warpExists(group, name)) {
                stack.getExecutor().sendMessage(getSuccessMessage(group, name));
                return;
            } else {
                SMPWarp.getWarpDatabase().createWarp(name, group, stack.getLocation(),
                        stack.getExecutor().getUniqueId().toString());
                stack.getExecutor().sendMessage(getSuccessMessage(group, name));
            }
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
        }
        return List.of();
    }
}
