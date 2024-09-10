package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.SMPWarp;

import java.sql.SQLException;

public class CreateWarpCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length != 2) {
            return;
        }

        try {
            SMPWarp.getWarpDatabase().createWarp(args[1], args[0], stack.getLocation(), stack.getExecutor().getUniqueId().toString());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
