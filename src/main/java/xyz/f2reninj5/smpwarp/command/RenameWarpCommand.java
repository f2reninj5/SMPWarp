package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
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

public class RenameWarpCommand implements BasicCommand {

    private Component getSuccessResponse(WarpIdentifier oldIdentifier, WarpIdentifier newIdentifier) {
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

    private Component getCancelResponse() {
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
                if (SMPWarp.getWarpDatabase().warpExists(identifier.getGroup(), identifier.getName())) {
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
        if (args.length < 1) {
            stack.getExecutor().sendMessage(getNoWarpGivenResponse());
            return;
        }

        WarpIdentifier identifer = WarpIdentifier.commandArgumentsToWarpIdentifier(args);

        try {
            Warp warp = SMPWarp.getWarpDatabase().getWarp(identifer.getName(), identifer.getGroup());
            if (warp == null) {
                stack.getExecutor().sendMessage(getWarpNotFoundResponse(identifer));
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
                            SMPWarp.getWarpDatabase().renameWarp(
                                identifer.getGroup(),
                                identifer.getName(),
                                newIdentifier.getGroup(),
                                newIdentifier.getName()
                            );
                        } catch (SQLException exception) {
                            throw new RuntimeException(exception);
                        }
                        if (SMPWarp.getPlugin().getConfig().getBoolean("enable-bluemap-markers")) {
                            BlueMap.removeMarker(identifer.getGroup(), identifer.getName());
                            BlueMap.addMarker(new Warp(
                                newIdentifier.getName(),
                                newIdentifier.getGroup(),
                                warp.location,
                                warp.createdBy
                            ));
                        }
                        stack.getSender().sendMessage(getSuccessResponse(identifer, newIdentifier));
                    } else {
                        stack.getSender().sendMessage(getCancelResponse());
                    }
                });

            Conversation conversation = conversationFactory.buildConversation((Player) stack.getSender());
            conversation.begin();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length == 0) {
            try {
                List<String> suggestions = SMPWarp.getWarpDatabase().getWarpGroups();
                suggestions.addAll(SMPWarp.getWarpDatabase().getWarpNames("", ""));
                return suggestions;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (args.length == 1) {
            try {
                List<String> suggestions = SMPWarp.getWarpDatabase().getWarpGroups(args[0]);
                suggestions.addAll(SMPWarp.getWarpDatabase().getWarpNames("", args[0]));
                return suggestions;
            } catch (SQLException exception) {
                throw new RuntimeException(exception.getMessage());
            }
        } else if (args.length == 2) {
            try {
                return SMPWarp.getWarpDatabase().getWarpNames(args[0], args[1]);
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        }
        return List.of();
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }
}
