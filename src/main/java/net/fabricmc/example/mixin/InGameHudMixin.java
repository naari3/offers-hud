package net.fabricmc.example.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.fabricmc.example.MerchantInfo;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.util.Identifier;

@Mixin(InGameHud.class)
@Environment(EnvType.CLIENT)
public abstract class InGameHudMixin {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager2.png");

    @Inject(at = @At("HEAD"), method = "renderStatusEffectOverlay")
    public void renderStatusEffectOverlay(MatrixStack matrices, CallbackInfo ci) {
        final var client = MinecraftClient.getInstance();
        final var player = client.player;
        if (player == null)
            return;

        final var textRenderer = client.textRenderer;
        final var itemRenderer = client.getItemRenderer();

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
        MerchantInfo.getInfo().getLastId().ifPresent(lastId -> {
            var offers = MerchantInfo.getInfo().offers.iterator();
            var i = 0;
            while (offers.hasNext()) {
                var offer = offers.next();

                var x = 0;
                var y = 5;

                itemRenderer.renderInGui(offer.getOriginalFirstBuyItem(), x + 5, y + i * 20);
                itemRenderer.renderGuiItemOverlay(textRenderer, offer.getOriginalFirstBuyItem(), x + 5, y + i * 20);

                itemRenderer.renderInGui(offer.getSecondBuyItem(), x + 25, y + i * 20);
                itemRenderer.renderGuiItemOverlay(textRenderer, offer.getSecondBuyItem(), x + 25, y + i * 20);

                itemRenderer.renderInGui(offer.getSellItem(), x + 58, y + i * 20);
                itemRenderer.renderGuiItemOverlay(textRenderer, offer.getSellItem(), x + 58, y + i * 20);

                this.renderArrow(matrices, offer, x + -15, y + i * 20);

                List<String> enchantments = new ArrayList<>();

                var map = EnchantmentHelper.get(offer.getSellItem());

                for (Entry<Enchantment, Integer> entry : map.entrySet()) {
                    enchantments.add(entry.getKey().getName(entry.getValue()).getString());
                }

                textRenderer.drawWithShadow(matrices, String.join(", ", enchantments), (x + 80), (y + 5 + i * 20),
                        0xFFFFFF);
                i += 1;
            }
        });
    }

    // from MerchantScreen
    private void renderArrow(MatrixStack matrices, TradeOffer tradeOffer, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        if (tradeOffer.isDisabled()) {
            DrawableHelper.drawTexture(matrices, x + 5 + 35 + 20, y + 3, 0, 25.0F, 171.0F, 10, 9, 256,
                    512);
        } else {
            DrawableHelper.drawTexture(matrices, x + 5 + 35 + 20, y + 3, 0, 15.0F, 171.0F, 10, 9, 256,
                    512);
        }

    }
}
