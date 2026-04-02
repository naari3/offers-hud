//? if fabric {
package net.naari3.offershud.platform;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
/*? if >= 26.1 {*/
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;
import net.naari3.offershud.OffersHUD;
/*?} else {*/
/*import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
*//*?}*/
import net.minecraft.network.protocol.Packet;

public class FabricPlatform implements Platform {
    public static final FabricPlatform INSTANCE = new FabricPlatform();

    private FabricPlatform() {}

    @Override
    public void sendPacketToServer(Packet<?> packet) {
        ClientPlayNetworking.getSender().sendPacket(packet);
    }

    @Override
    public void registerClientTickHandler(Runnable handler) {
        /*? if >= 26.1 {*/
        ClientTickEvents.END_LEVEL_TICK.register(world -> handler.run());
        /*?} else {*/
        /*ClientTickEvents.END_WORLD_TICK.register(world -> handler.run());
        *//*?}*/
    }

    @Override
    public void registerHudRenderer(HudRenderer renderer) {
        /*? if >= 26.1 {*/
        HudElementRegistry.attachElementAfter(
            VanillaHudElements.HOTBAR,
            Identifier.fromNamespaceAndPath(OffersHUD.MODID, "offers"),
            renderer::extractRenderState
        );
        /*?} else if >= 1.21 {*/
        /*HudRenderCallback.EVENT.register((graphics, deltaTracker) ->
            renderer.render(graphics, deltaTracker));
        *//*?} else {*/
        /*HudRenderCallback.EVENT.register((graphics, tickDelta) ->
            renderer.render(graphics, tickDelta));
        *//*?}*/
    }
}
//?}
