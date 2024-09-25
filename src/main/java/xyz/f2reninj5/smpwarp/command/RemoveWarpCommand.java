package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.BlueMap;
import xyz.f2reninj5.smpwarp.SMPWarp;
import xyz.f2reninj5.smpwarp.model.WarpIdentifier;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static xyz.f2reninj5.smpwarp.common.Command.getWarpSuggestions;
import static xyz.f2reninj5.smpwarp.common.Command.handleDatabaseError;
import static xyz.f2reninj5.smpwarp.common.CommandResponse.*;

public class RemoveWarpCommand implements BasicCommand {

    private static Component getSuccessResponse(WarpIdentifier identifier) {
        return getSuccessSerialiser().deserialize(
            "<primary>Removed warp <warp>.</primary>",
            identifierToWarpPlaceholder(identifier)
        );
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        final CommandSender sender = stack.getSender();
        final Player player = (Player) stack.getExecutor();
        assert player != null;

        if (args.length < 1) {
            sender.sendMessage(getNoWarpGivenResponse());
            return;
        }

        WarpIdentifier identifier = WarpIdentifier.commandArgumentsToWarpIdentifier(args);

        try {
            if (!SMPWarp.getWarpDatabase().warpExists(identifier)) {
                sender.sendMessage(getWarpNotFoundResponse(identifier));
                return;
            }
        } catch (SQLException exception) {
            handleDatabaseError(player, exception);
            return;
        }

        try {
            SMPWarp.getWarpDatabase().removeWarp(identifier);
        } catch (SQLException exception) {
            handleDatabaseError(player, exception);
            return;
        }

        if (SMPWarp.getPlugin().getConfig().getBoolean("enable-bluemap-markers")) {
            BlueMap.removeMarker(identifier);
        }

        sender.sendMessage(getSuccessResponse(identifier));
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        return getWarpSuggestions(stack, args);
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }
}
