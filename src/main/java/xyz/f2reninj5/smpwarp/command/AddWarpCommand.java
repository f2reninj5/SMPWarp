package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.SMPWarp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNumeric;
import static xyz.f2reninj5.smpwarp.common.Command.getWarpGroupSuggestions;
import static xyz.f2reninj5.smpwarp.common.Command.handleDatabaseError;
import static xyz.f2reninj5.smpwarp.common.CommandResponse.getNoWarpGivenResponse;

public class AddWarpCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        final CommandSender sender = stack.getSender();
        final Player player = (Player) stack.getExecutor();
        assert player != null;

        if (args.length == 0) {
            sender.sendMessage(getNoWarpGivenResponse());
            return;
        }

        List<String> warpNames;

        try {
            warpNames = SMPWarp.getWarpDatabase().getWarpNames(args[0], "");
        } catch (SQLException exception) {
            handleDatabaseError(sender, exception);
            return;
        }

        int lastWarpName = 0;

        for (String warpName : warpNames) {
            if (isNumeric(warpName)) {
                lastWarpName = Math.max(lastWarpName, Integer.parseInt(warpName));
            }
        }

        lastWarpName ++;

        ((Player) sender).performCommand("createwarp " + args[0] + " " + lastWarpName);
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        return getWarpGroupSuggestions(stack, args);
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }
}
