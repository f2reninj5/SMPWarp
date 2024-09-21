package xyz.f2reninj5.smpwarp.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

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

    public static MiniMessage getErrorSerialiser() {
        return ERROR_SERIALISER;
    }

    public static Component getWarpNotFoundResponse(@NotNull String group, @NotNull String name) {
        return getErrorSerialiser().deserialize(
        """
                <primary>Warp <contrast><group></contrast>: <contrast><name></contrast> not found.
            """,
            Placeholder.component("group", Component.text(group)),
            Placeholder.component("name", Component.text(name))
        );
    }
}
