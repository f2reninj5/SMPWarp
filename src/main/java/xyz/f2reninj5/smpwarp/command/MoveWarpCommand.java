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
import xyz.f2reninj5.smpwarp.model.Warp;
import xyz.f2reninj5.smpwarp.model.WarpIdentifier;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static xyz.f2reninj5.smpwarp.common.Command.getWarpSuggestions;
import static xyz.f2reninj5.smpwarp.common.Command.handleDatabaseError;
import static xyz.f2reninj5.smpwarp.common.CommandResponse.*;

public class MoveWarpCommand implements BasicCommand {

    private static Component getSuccessResponse(WarpIdentifier identifier) {
        return getSuccessSerialiser().deserialize(
            "<primary>Moved warp <warp> to your location.</primary>",
            identifierToWarpPlaceholder(identifier)
        );
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        final CommandSender sender = stack.getSender();
        final Player player = (Player) stack.getExecutor();
        final Location location = stack.getLocation();
        assert player != null;

        if (args.length < 1) {
            sender.sendMessage(getNoWarpGivenResponse());
            return;
        }

        WarpIdentifier identifier = WarpIdentifier.commandArgumentsToWarpIdentifier(args);
        Warp warp;

        try {
            warp = SMPWarp.getWarpDatabase().getWarp(identifier);
        } catch (SQLException exception) {
            handleDatabaseError(player, exception);
            return;
        }
        
        if (warp == null) {
            sender.sendMessage(getWarpNotFoundResponse(identifier));
            return;
        }

        if (SMPWarp.getPlugin().getConfig().getBoolean("enable-bluemap-markers")) {
            BlueMap.removeMarker(identifier);
            BlueMap.addMarker(new Warp(
                identifier,
                location,
                player
            ));
        }

        try {
            SMPWarp.getWarpDatabase().moveWarp(identifier, location);
        } catch (SQLException exception) {
            handleDatabaseError(player, exception);
            return;
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
