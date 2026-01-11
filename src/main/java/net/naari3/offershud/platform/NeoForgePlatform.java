//? if neoforge {
/*package net.naari3.offershud.platform;

//? if >= 1.20.5 {
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
//?} else {
/^import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.event.TickEvent;

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
        NeoForge.EVENT_BUS.addListener(TickEvent.ClientTickEvent.class, event -> {
            if (event.phase == TickEvent.Phase.END) {
                var mc = Minecraft.getInstance();
                if (mc.level != null) {
                    handler.run();
                }
            }
        });
    }

    @Override
    public void registerHudRenderer(HudRenderer renderer) {
        NeoForge.EVENT_BUS.addListener(RenderGuiOverlayEvent.Post.class, event -> {
            if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
                renderer.render(event.getGuiGraphics(), event.getPartialTick());
            }
        });
    }
}
^///?}
*///?}