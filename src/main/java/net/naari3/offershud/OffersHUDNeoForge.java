//? if neoforge {
/*package net.naari3.offershud;

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
        container.registerExtensionPoint(IConfigScreenFactory.class,
            (modContainer, parent) -> ModConfig.createScreen(parent));
    }
}
*///?}
