package net.naari3.offershud.config;

import java.util.Collections;

/*? if >= 1.21.11 {*/
import me.shedaniel.autoconfig.AutoConfigClient;
/*?} else {*/
/*import me.shedaniel.autoconfig.AutoConfig;
*//*?}*/
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.network.chat.Component;

/**
 * Registers a custom Cloth Config GUI provider so that the {@code editPosition}
 * marker field is rendered as a button that opens the graphical position editor.
 * Client-only; must be called once during client init.
 */
public final class ModConfigGui {
    private ModConfigGui() {
    }

    public static void registerGui() {
        /*? if >= 1.21.11 {*/
        AutoConfigClient.getGuiRegistry(ModConfig.class).registerPredicateProvider(
        /*?} else {*/
        /*AutoConfig.getGuiRegistry(ModConfig.class).registerPredicateProvider(
        *//*?}*/
                (i18n, field, config, defaults, registry) ->
                        Collections.<AbstractConfigListEntry>singletonList(
                                new PositionButtonEntry(
                                        Component.translatable("text.autoconfig.offershud.option.editPosition.button"))),
                field -> field.getName().equals("editPosition"));
    }
}
