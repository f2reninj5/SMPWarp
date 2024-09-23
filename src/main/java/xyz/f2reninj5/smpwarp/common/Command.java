package xyz.f2reninj5.smpwarp.common;

import org.bukkit.command.CommandSender;
import xyz.f2reninj5.smpwarp.SMPWarp;

import java.sql.SQLException;

import static xyz.f2reninj5.smpwarp.common.CommandResponse.getDatabaseErrorResponse;

public class Command {

    public static void handleDatabaseError(CommandSender sender, SQLException exception) {
        SMPWarp.getPlugin().getLogger().severe(exception.getMessage());
        sender.sendMessage(getDatabaseErrorResponse());
    }
}
