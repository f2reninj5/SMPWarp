package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.SMPWarp;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class CreateWarpCommand implements BasicCommand {

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
            SMPWarp.getWarpDatabase().createWarp(name, group, stack.getLocation(),
                    stack.getExecutor().getUniqueId().toString());
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
