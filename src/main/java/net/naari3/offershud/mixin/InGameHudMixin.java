package net.naari3.offershud.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.naari3.offershud.MerchantInfo;
import net.naari3.offershud.OffersHUD;
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
    private static final Identifier TEXTURE = new Identifier(OffersHUD.MODID, "textures/gui/container/villager2.png");

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
            var offers = MerchantInfo.getInfo().getOffers();
            var i = 0;

            for (TradeOffer offer : offers) {
                var baseX = 5;
                var baseY = 5 + i * 20;

                var firstBuy = offer.getAdjustedFirstBuyItem().copy();
                var secondBuy = offer.getSecondBuyItem().copy();
                var sell = offer.getSellItem().copy();

                itemRenderer.renderInGui(firstBuy, baseX, baseY);
                itemRenderer.renderGuiItemOverlay(textRenderer, firstBuy, baseX, baseY,
                        firstBuy.getCount() == 1 ? "1" : null);

                itemRenderer.renderInGui(secondBuy, baseX + 20, baseY);
                itemRenderer.renderGuiItemOverlay(textRenderer, secondBuy, baseX + 20, baseY,
                        secondBuy.getCount() == 1 ? "1" : null);

                itemRenderer.renderInGui(sell, baseX + 53, baseY);
                itemRenderer.renderGuiItemOverlay(textRenderer, sell, baseX + 53, baseY,
                        sell.getCount() == 1 ? "1" : null);

                this.renderArrow(matrices, offer, baseX + -20, baseY);

                List<String> enchantments = new ArrayList<>();

                var map = EnchantmentHelper.get(offer.getSellItem());

                for (Entry<Enchantment, Integer> entry : map.entrySet()) {
                    enchantments.add(entry.getKey().getName(entry.getValue()).getString());
                }

                textRenderer.drawWithShadow(matrices, String.join(", ", enchantments), (baseX + 75), (baseY + 5),
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
            DrawableHelper.drawTexture(matrices, x + 5 + 35 + 20, y + 3, 0, 25.0F, 171.0F, 10, 9, 512,
                    256);
        } else {
            DrawableHelper.drawTexture(matrices, x + 5 + 35 + 20, y + 3, 0, 15.0F, 171.0F, 10, 9, 512,
                    256);
        }

    }
}
