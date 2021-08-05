package net.fabricmc.example.mixin;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.fabricmc.api.Environment;
import net.fabricmc.example.MerchantInfo;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(InGameHud.class)
@Environment(EnvType.CLIENT)
public class InGameHudMixin {
    @Overwrite()
    public void renderStatusEffectOverlay(MatrixStack matrices) {
        final var client = MinecraftClient.getInstance();
        final var player = client.player;
        final var textRenderer = client.textRenderer;

        if (player == null)
            return;

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
        var offers = MerchantInfo.getInfo().offers.iterator();
        var i = 0;
        while (offers.hasNext()) {
            var offer = offers.next();
            var sellItem = offer.getSellItem().getName().getString();
            var x = 3;
            var y = 3 + i * textRenderer.fontHeight;
            textRenderer.drawWithShadow(matrices, sellItem, x, y, 0xFFFFFF);
            i += 1;
        }
    }
}
