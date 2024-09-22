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

import static xyz.f2reninj5.smpwarp.common.CommandResponse.*;

public class CreateWarpCommand implements BasicCommand {

    private static Component getSuccessResponse(@NotNull WarpIdentifier identifier) {
        return getSuccessSerialiser().deserialize(
            "<primary>Created warp <warp>.</primary>",
            identifierToWarpPlaceholder(identifier)
        );
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length < 1) {
            stack.getSender().sendMessage(getNoWarpGivenResponse());
            return;
        }

        WarpIdentifier identifier = WarpIdentifier.commandArgumentsToWarpIdentifier(args);

        try {
            if (SMPWarp.getWarpDatabase().warpExists(identifier.getGroup(), identifier.getName())) {
                stack.getSender().sendMessage(getWarpAlreadyExistsResponse(identifier));
            } else {
                Location location = stack.getLocation();

                SMPWarp.getWarpDatabase().createWarp(identifier.getName(), identifier.getGroup(), location,
                        stack.getExecutor().getUniqueId().toString());

                if (SMPWarp.getPlugin().getConfig().getBoolean("enable-bluemap-markers")) {
                    BlueMap.addMarker(new Warp(
                        identifier,
                        location,
                        stack.getExecutor().getUniqueId().toString()
                    ));
                }

                stack.getSender().sendMessage(getSuccessResponse(identifier));
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

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }
}
