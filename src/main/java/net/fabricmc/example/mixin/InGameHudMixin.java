package net.fabricmc.example.mixin;

import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.fabricmc.example.MerchantInfo;
import net.fabricmc.example.OfferFormatter;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(InGameHud.class)
@Environment(EnvType.CLIENT)
public class InGameHudMixin {
    @Inject(at = @At("HEAD"), method = "renderStatusEffectOverlay")
    public void renderStatusEffectOverlay(MatrixStack matrices, CallbackInfo ci) {
        final var client = MinecraftClient.getInstance();
        final var player = client.player;
        final var textRenderer = client.textRenderer;

        if (player == null)
            return;

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
        MerchantInfo.getInfo().getLastId().ifPresent(lastId -> {
            var offers = MerchantInfo.getInfo().offers.iterator();
            var i = 0;
            while (offers.hasNext()) {
                var offer = offers.next();
                var msg = new OfferFormatter(offer.getOriginalFirstBuyItem(), offer.getSecondBuyItem(),
                        offer.getSellItem()).toString();
                var x = 3;
                var y = 3 + i * textRenderer.fontHeight;

                textRenderer.drawWithShadow(matrices, msg, x, y, 0xFFFFFF);
                i += 1;
            }
        });
    }
}
