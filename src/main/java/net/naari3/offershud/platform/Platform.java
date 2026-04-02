package net.naari3.offershud.platform;

/*? if >= 26.1 {*/
import net.minecraft.client.gui.GuiGraphicsExtractor;
/*?} else {*/
/*import net.minecraft.client.gui.GuiGraphics;
*//*?}*/
import net.minecraft.network.protocol.Packet;

public interface Platform {
    void sendPacketToServer(Packet<?> packet);
    void registerClientTickHandler(Runnable handler);
    void registerHudRenderer(HudRenderer renderer);

    @FunctionalInterface
    interface HudRenderer {
        /*? if >= 26.1 {*/
        void extractRenderState(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker deltaTracker);
        /*?} else if >= 1.21 {*/
        /*void render(GuiGraphics graphics, net.minecraft.client.DeltaTracker deltaTracker);
        *//*?} else {*/
        /*void render(GuiGraphics graphics, float tickDelta);
        *//*?}*/
    }

    static Platform getInstance() {
        //? if fabric
        return FabricPlatform.INSTANCE;
        //? if neoforge
        /*return NeoForgePlatform.INSTANCE;*/
    }
}
