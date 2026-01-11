package net.naari3.offershud.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
/*? if >= 1.21.11 {*/
import net.minecraft.resources.Identifier;
/*?} else if >= 1.21 {*/
/*import net.minecraft.resources.ResourceLocation;
*//*?} else {*/
/*import net.minecraft.resources.ResourceLocation;
*//*?}*/
import net.naari3.offershud.OffersHUD;
//? if fabric {
import net.fabricmc.loader.api.FabricLoader;
//?} else {
/*import net.neoforged.fml.loading.FMLPaths;
*//*?}*/

public class ModConfig {
    public static ConfigClassHandler<ModConfig> HANDLER = ConfigClassHandler.createBuilder(ModConfig.class)
            /*? if >= 1.21.11 {*/
            .id(Identifier.fromNamespaceAndPath(OffersHUD.MODID, "config"))
            /*?} else if >= 1.21 {*/
            /*.id(ResourceLocation.fromNamespaceAndPath(OffersHUD.MODID, "config"))
            *//*?} else {*/
            /*.id(new ResourceLocation(OffersHUD.MODID, "config"))
            *//*?}*/
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    //? if fabric {
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("offershud.json"))
                    //?} else {
                    /*.setPath(FMLPaths.CONFIGDIR.get().resolve("offershud.json"))
                    *//*?}*/
                    .build())
            .build();

    @SerialEntry
    public boolean enabled = true;

    @SerialEntry
    public boolean ignoreNoProfession = true;

    @SerialEntry
    public boolean suppressVillagerHeadRolling = false;

    @SerialEntry
    public int offsetX = 5;

    @SerialEntry
    public int offsetY = 5;

    @SerialEntry
    public float scale = 1.0f;

    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("offershud.config.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("offershud.config.general"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("offershud.config.enabled"))
                                .description(OptionDescription.of(Component.translatable("offershud.config.enabled.description")))
                                .binding(true, () -> HANDLER.instance().enabled, v -> HANDLER.instance().enabled = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("offershud.config.ignoreNoProfession"))
                                .description(OptionDescription.of(Component.translatable("offershud.config.ignoreNoProfession.description")))
                                .binding(true, () -> HANDLER.instance().ignoreNoProfession, v -> HANDLER.instance().ignoreNoProfession = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("offershud.config.suppressVillagerHeadRolling"))
                                .description(OptionDescription.of(Component.translatable("offershud.config.suppressVillagerHeadRolling.description")))
                                .binding(false, () -> HANDLER.instance().suppressVillagerHeadRolling, v -> HANDLER.instance().suppressVillagerHeadRolling = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("offershud.config.offsetX"))
                                .description(OptionDescription.of(Component.translatable("offershud.config.offsetX.description")))
                                .binding(5, () -> HANDLER.instance().offsetX, v -> HANDLER.instance().offsetX = v)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 500)
                                        .step(1))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("offershud.config.offsetY"))
                                .description(OptionDescription.of(Component.translatable("offershud.config.offsetY.description")))
                                .binding(5, () -> HANDLER.instance().offsetY, v -> HANDLER.instance().offsetY = v)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 500)
                                        .step(1))
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Component.translatable("offershud.config.scale"))
                                .description(OptionDescription.of(Component.translatable("offershud.config.scale.description")))
                                .binding(1.0f, () -> HANDLER.instance().scale, v -> HANDLER.instance().scale = v)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .range(0.5f, 3.0f)
                                        .step(0.1f))
                                .build())
                        .build())
                .save(() -> HANDLER.save())
                .build()
                .generateScreen(parent);
    }
}
