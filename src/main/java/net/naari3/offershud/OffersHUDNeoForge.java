//? if neoforge {
/*package net.naari3.offershud;

//? if >= 1.21.11 {
import me.shedaniel.autoconfig.AutoConfigClient;
 //?} else {
/^import me.shedaniel.autoconfig.AutoConfig;
^///?}
import net.naari3.offershud.config.ModConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
//? if >= 1.20.5 {
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
 //?} else {
/^import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.ConfigScreenHandler;
^///?}


//? if >= 1.20.5 {
@Mod(value = OffersHUD.MODID, dist = Dist.CLIENT)
 //?} else {
/^@Mod(OffersHUD.MODID)
^///?}
public class OffersHUDNeoForge {
    //? if >= 1.20.5 {
    public OffersHUDNeoForge(IEventBus modBus, ModContainer container) {
        // Initialize common logic
        OffersHUD.init();

        // Register config screen (NeoForge native)
        //? if >= 1.21.11 {
        container.registerExtensionPoint(IConfigScreenFactory.class,
            (modContainer, parent) -> AutoConfigClient.getConfigScreen(ModConfig.class, parent).get());
        //?} else {
        /^container.registerExtensionPoint(IConfigScreenFactory.class,
            (modContainer, parent) -> AutoConfig.getConfigScreen(ModConfig.class, parent).get());
        ^///?}
    }
    //?} else {
    /^public OffersHUDNeoForge(IEventBus modBus) {
        // クライアント側でのみ初期化
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }

        // Initialize common logic
        OffersHUD.init();

        // Register config screen
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, parent) -> AutoConfig.getConfigScreen(ModConfig.class, parent).get()
                )
        );
    }
    ^///?}
}
*///?}