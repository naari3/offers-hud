//? if neoforge {
/*package net.naari3.offershud.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class NeoForgePlatform implements Platform {
    public static final NeoForgePlatform INSTANCE = new NeoForgePlatform();

    private NeoForgePlatform() {}

    @Override
    public void sendPacketToServer(Packet<?> packet) {
        var connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            connection.send(packet);
        }
    }

    @Override
    public void registerClientTickHandler(Runnable handler) {
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> {
            var mc = Minecraft.getInstance();
            if (mc.level != null) {
                handler.run();
            }
        });
    }

    @Override
    public void registerHudRenderer(HudRenderer renderer) {
        NeoForge.EVENT_BUS.addListener(RenderGuiLayerEvent.Post.class, event -> {
            if (event.getName().equals(VanillaGuiLayers.HOTBAR)) {
                renderer.render(event.getGuiGraphics(), event.getPartialTick());
            }
        });
    }
}
*///?}
