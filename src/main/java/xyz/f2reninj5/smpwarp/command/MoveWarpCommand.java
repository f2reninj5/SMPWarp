package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.BlueMap;
import xyz.f2reninj5.smpwarp.SMPWarp;
import xyz.f2reninj5.smpwarp.model.Warp;
import xyz.f2reninj5.smpwarp.model.WarpIdentifier;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static xyz.f2reninj5.smpwarp.common.CommandResponse.getSuccessSerialiser;
import static xyz.f2reninj5.smpwarp.common.CommandResponse.identiferToWarpPlaceholder;

public class MoveWarpCommand implements BasicCommand {

    private Component getSuccessResponse(WarpIdentifier identifier) {
        return getSuccessSerialiser().deserialize(
            "<primary>Moved warp <warp> to your location.</primary>",
            identiferToWarpPlaceholder(identifier)
        );
    }

    private Component getFailureMessage(String warpGroup, String warpName) {
        TextComponent.Builder builder = text()
            .content("Warp ").color(RED);

        if (warpGroup != "") {
            builder
                .append(text(warpGroup, GOLD))
                .append(text(": ", RED));
        }

        return builder
            .append(text(warpName, GOLD))
            .append(text(" not found.", RED))
            .build();
    }

    private Component getFailureMessage() {
        return text()
            .content("No warp given.")
            .color(RED)
            .build();
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length < 1) {
            stack.getExecutor().sendMessage(getFailureMessage());
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
                stack.getExecutor().sendMessage(getFailureMessage(group, name));
                return;
            }

            if (SMPWarp.getPlugin().getConfig().getBoolean("enable-bluemap-markers")) {
                BlueMap.removeMarker(group, name);
                BlueMap.addMarker(new Warp(name, group, stack.getLocation(), stack.getExecutor().getUniqueId().toString()));
            }

            SMPWarp.getWarpDatabase().moveWarp(group, name, stack.getLocation());
            stack.getExecutor().sendMessage(getSuccessResponse(new WarpIdentifier(group, name)));
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
