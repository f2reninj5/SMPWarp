package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
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

public class RenameWarpCommand implements BasicCommand {

    private static Component getSuccessResponse(WarpIdentifier oldIdentifier, WarpIdentifier newIdentifier) {
        return getSuccessSerialiser()
            .deserialize(
                "<primary>Renamed warp <warp> to </primary>",
                identifierToWarpPlaceholder(oldIdentifier)
            )
            .append(getSuccessSerialiser().deserialize(
                "<primary><warp>.</primary>",
                identifierToWarpPlaceholder(newIdentifier)
            ));
    }

    private static Component getCancelResponse() {
        return getErrorSerialiser().deserialize(
            "<primary>Warp rename cancelled.</primary>"
        );
    }

    private static class NewWarpNamePrompt implements Prompt {
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return "Please type the new warp name (type 'cancel' to exit):";
        }

        @Override
        public boolean blocksForInput(@NotNull ConversationContext context) {
            return true;
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context, @NotNull String input) {
            WarpIdentifier identifier = WarpIdentifier.stringToWarpIdentifier(input);

            try {
                if (SMPWarp.getWarpDatabase().warpExists(identifier)) {
                    Player forWhom = (Player) context.getForWhom();
                    forWhom.sendMessage(getWarpAlreadyExistsResponse(identifier));
                    return new NewWarpNamePrompt();
                }

                context.setSessionData("newWarpIdentifier", identifier);
                return Prompt.END_OF_CONVERSATION;
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        }
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

        WarpIdentifier identifer = WarpIdentifier.commandArgumentsToWarpIdentifier(args);
        Warp warp;

        try {
            warp = SMPWarp.getWarpDatabase().getWarp(identifer);
        } catch (SQLException exception) {
            handleDatabaseError(player, exception);
            return;
        }

        if (warp == null) {
            sender.sendMessage(getWarpNotFoundResponse(identifer));
            return;
        }

        ConversationFactory conversationFactory = new ConversationFactory(SMPWarp.getPlugin())
            .withFirstPrompt(new NewWarpNamePrompt())
            .withEscapeSequence("cancel")
            .withTimeout(30)
            .addConversationAbandonedListener(abandonedEvent -> {
                if (abandonedEvent.gracefulExit()) {
                    WarpIdentifier newIdentifier = (WarpIdentifier) abandonedEvent.getContext()
                        .getSessionData("newWarpIdentifier");

                    try {
                        SMPWarp.getWarpDatabase().renameWarp(identifer, newIdentifier);
                    } catch (SQLException exception) {
                        handleDatabaseError(player, exception);
                        return;
                    }

                    if (SMPWarp.getPlugin().getConfig().getBoolean("enable-bluemap-markers")) {
                        BlueMap.removeMarker(identifer);
                        BlueMap.addMarker(new Warp(
                            newIdentifier,
                            warp.getLocation(),
                            warp.getCreatedBy()
                        ));
                    }
                    sender.sendMessage(getSuccessResponse(identifer, newIdentifier));
                } else {
                    sender.sendMessage(getCancelResponse());
                }
            });

        Conversation conversation = conversationFactory.buildConversation((Conversable) sender);
        conversation.begin();
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
