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
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;


@Mod(value = OffersHUD.MODID, dist = Dist.CLIENT)
public class OffersHUDNeoForge {
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
}
*///?}
