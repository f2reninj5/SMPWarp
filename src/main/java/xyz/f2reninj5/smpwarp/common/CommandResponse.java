package xyz.f2reninj5.smpwarp.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.model.WarpIdentifier;

public class CommandResponse {
    private final static TextColor ERROR_PRIMARY_COLOUR = NamedTextColor.RED;
    private final static TextColor ERROR_CONTRAST_COLOUR = NamedTextColor.GOLD;

    private final static MiniMessage ERROR_SERIALISER = MiniMessage.builder()
        .tags(
            TagResolver.builder()
                .tag("primary", Tag.styling(ERROR_PRIMARY_COLOUR))
                .tag("contrast", Tag.styling(ERROR_CONTRAST_COLOUR))
                .build()
        ).build();

    private final static TextColor SUCCESS_PRIMARY_COLOUR = NamedTextColor.GOLD;
    private final static TextColor SUCCESS_CONTRAST_COLOUR = NamedTextColor.RED;

    private final static MiniMessage SUCCESS_SERIALISER = MiniMessage.builder()
        .tags(
            TagResolver.builder()
                .tag("primary", Tag.styling(SUCCESS_PRIMARY_COLOUR))
                .tag("contrast", Tag.styling(SUCCESS_CONTRAST_COLOUR))
                .build()
        ).build();

    public static MiniMessage getErrorSerialiser() {
        return ERROR_SERIALISER;
    }

    public static MiniMessage getSuccessSerialiser() {
        return SUCCESS_SERIALISER;
    }

    public static TagResolver identiferToWarpPlaceholder(@NotNull WarpIdentifier identifier) {
        TagResolver.Builder resolverBuilder = TagResolver.builder()
            .tag("name", Tag.selfClosingInserting(Component.text(identifier.getName())));

        if (identifier.hasGroup()) {
            resolverBuilder.tag("group", Tag.selfClosingInserting(Component.text(identifier.getGroup())));
            resolverBuilder.tag("warp", Tag.preProcessParsed(
                "<contrast><group></contrast>: <contrast><name></contrast>"
            ));
        } else {
            resolverBuilder.tag("warp", Tag.preProcessParsed(
                "<contrast><name></contrast>"
            ));
        }

        return resolverBuilder.build();
    }

    public static Component getWarpNotFoundResponse(@NotNull WarpIdentifier identifier) {
        return getErrorSerialiser().deserialize(
        "<primary>Warp <warp> not found.</primary>",
            identiferToWarpPlaceholder(identifier)
        );
    }

    public static Component getNoWarpGivenResponse() {
        return getErrorSerialiser().deserialize(
            "<primary>No warp given.</primary>"
        );
    }
}
