package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.BlueMap;
import xyz.f2reninj5.smpwarp.SMPWarp;
import xyz.f2reninj5.smpwarp.model.Warp;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static xyz.f2reninj5.smpwarp.Teleport.teleport;

public class RenameWarpCommand implements BasicCommand {

    private Component getSuccessMessage(String warpGroup, String warpName, String newWarpGroup, String newWarpName) {
        TextComponent.Builder builder = text()
            .content("Renamed ").color(GOLD);

        if (warpGroup != "") {
            builder
                .append(text(warpGroup, RED))
                .append(text(": ", GOLD));
        }

        builder
            .append(text(warpName, RED))
            .append(text(" to ", GOLD));

        if (newWarpGroup != "") {
            builder
                .append(text(warpGroup, RED))
                .append(text(": ", GOLD));
        }

        return builder
            .append(text(newWarpName, RED))
            .append(text(".", GOLD))
            .build();
    }

    private Component getFailureMessage(String warpGroup, String warpName) {
        TextComponent.Builder builder = text()
                .content("Warp ").color(RED);

        if (warpGroup != "") {
            builder
                    .append(text(warpGroup, GOLD))
                    .append(text(": ", RED));
        }

        return builder
                .append(text(warpName, GOLD))
                .append(text(" not found.", RED))
                .build();
    }

    private Component getFailureMessage() {
        return text()
                .content("No warp given.")
                .color(RED)
                .build();
    }

    private Component getCancelMessage() {
        return text()
            .content("Warp rename cancelled.")
            .color(RED)
            .build();
    }

    private static class NewWarpNamePrompt implements Prompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return "Please type the new warp name (type 'cancel' to exit):";
        }

        @Override
        public boolean blocksForInput(@NotNull ConversationContext context) {
            return false;
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            assert input != null;
            String[] args = input.split(" ");
            String group = "";
            String name = args[0];
            if (args.length == 2) {
                group = name;
                name = args[1];
            }
            context.setSessionData("newWarpGroup", group);
            context.setSessionData("newWarpName", name);
            return Prompt.END_OF_CONVERSATION;
        }
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length < 1) {
            stack.getExecutor().sendMessage(getFailureMessage());
            return;
        }

        String group;
        String name;

        if (args.length > 1) {
            group = args[0];
            name = args[1];
        } else {
            group = "";
            name = args[0];
        }

        try {
            Warp warp = SMPWarp.getWarpDatabase().getWarp(name, group);
            if (warp == null) {
                stack.getExecutor().sendMessage(getFailureMessage(group, name));
                return;
            }

            ConversationFactory conversationFactory = new ConversationFactory(SMPWarp.getPlugin())
                .withFirstPrompt(new NewWarpNamePrompt())
                .withEscapeSequence("cancel")
                .withTimeout(30)
                .addConversationAbandonedListener(abandonedEvent -> {
                    if (abandonedEvent.gracefulExit()) {
                        String newWarpGroup = (String) abandonedEvent.getContext().getSessionData("newWarpGroup");
                        String newWarpName = (String) abandonedEvent.getContext().getSessionData("newWarpName");
                        try {
                            SMPWarp.getWarpDatabase().renameWarp(group, name, newWarpGroup, newWarpName);
                        } catch (SQLException exception) {
                            throw new RuntimeException(exception);
                        }
                        if (SMPWarp.getPlugin().getConfig().getBoolean("enable-bluemap-markers")) {
                            BlueMap.removeMarker(group, name);
                            BlueMap.addMarker(new Warp(newWarpName, newWarpGroup, warp.location, warp.createdBy));
                        }
                        stack.getSender().sendMessage(getSuccessMessage(group, name, newWarpGroup, newWarpName));
                    } else {
                        stack.getSender().sendMessage(getCancelMessage());
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
}
