package xyz.f2reninj5.smpwarp.common;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.SMPWarp;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static xyz.f2reninj5.smpwarp.common.CommandResponse.getDatabaseErrorResponse;

public class Command {

    public static void handleDatabaseError(CommandSender sender, SQLException exception) {
        SMPWarp.getPlugin().getLogger().severe(exception.getMessage());
        sender.sendMessage(getDatabaseErrorResponse());
    }

    public static @NotNull Collection<String> getWarpSuggestions(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        final CommandSender sender = stack.getSender();

        try {
            if (args.length == 0) {
                List<String> suggestions = SMPWarp.getWarpDatabase().getAllWarpGroups();
                suggestions.addAll(SMPWarp.getWarpDatabase().getWarpNames("", ""));
                return suggestions;
            } else if (args.length == 1) {
                List<String> suggestions = SMPWarp.getWarpDatabase().getWarpGroups(args[0]);
                suggestions.addAll(SMPWarp.getWarpDatabase().getWarpNames("", args[0]));
                return suggestions;
            } else if (args.length == 2) {
                return SMPWarp.getWarpDatabase().getWarpNames(args[0], args[1]);
            }
        } catch (SQLException exception) {
            handleDatabaseError(sender, exception);
        }

        return List.of();
    }

    public static @NotNull Collection<String> getWarpGroupSuggestions(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        final CommandSender sender = stack.getSender();

        try {
            if (args.length == 0) {
                return SMPWarp.getWarpDatabase().getAllWarpGroups();
            } else if (args.length == 1) {
                return SMPWarp.getWarpDatabase().getWarpGroups(args[0]);
            }
        } catch(SQLException exception) {
            handleDatabaseError(sender, exception);
        }

        return List.of();
    }
}
