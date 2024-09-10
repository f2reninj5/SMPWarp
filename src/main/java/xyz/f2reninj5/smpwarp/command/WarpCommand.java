package xyz.f2reninj5.smpwarp.command;

import com.earth2me.essentials.AsyncTeleport;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.api.IAsyncTeleport;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.model.Warp;
import xyz.f2reninj5.smpwarp.SMPWarp;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class WarpCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length != 2) {
            return;
        }

        try {
            Warp warp = SMPWarp.getWarpDatabase().getWarp(args[1], args[0]);
            if (warp == null) {
                return;
            }

            IEssentials essentials = SMPWarp.getEssentialsPlugin();
            IUser user = essentials.getUser(stack.getExecutor().getUniqueId());
            IAsyncTeleport asyncTeleport = user.getAsyncTeleport();
            asyncTeleport.teleport(
                warp.location,
                new Trade(0, essentials),
                PlayerTeleportEvent.TeleportCause.COMMAND,
                CompletableFuture.completedFuture(true)
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
