package net.naari3.offershud.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shedaniel.autoconfig.AutoConfig;

import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.naari3.offershud.MerchantInfo;
import net.naari3.offershud.OffersHUD;
import net.naari3.offershud.config.ModConfig;
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
        var config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        if (!config.enabled)
            return;

        final var client = MinecraftClient.getInstance();
        final var player = client.player;
        if (player == null)
            return;

        final var textRenderer = client.textRenderer;
        final var itemRenderer = client.getItemRenderer();

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);

        // Touch directly the static variable matrices, not the matrices passed as
        // arguments.
        // This is like touching a global variable directly, which I feel is not a good
        // move because we do not know the scope of the effect, but it was necessary in
        // order to affect methods that do not have matrices as arguments,
        // such as `itemRenderer.renderInGui`.
        var modelMatrices = RenderSystem.getModelViewStack();
        modelMatrices.push();
        modelMatrices.translate(config.offsetX, config.offsetY, 1.0);
        modelMatrices.push();
        modelMatrices.scale(config.scale, config.scale, 1.0f);
        RenderSystem.applyModelViewMatrix();

        MerchantInfo.getInfo().getLastId().ifPresent(lastId -> {
            var offers = MerchantInfo.getInfo().getOffers();
            var i = 0;

            for (TradeOffer offer : offers) {
                var baseX = 0;
                var baseY = 0 + i * 20;

                var firstBuy = offer.getAdjustedFirstBuyItem().copy();
                var secondBuy = offer.getSecondBuyItem().copy();
                var sell = offer.getSellItem().copy();

                itemRenderer.renderInGui(matrices, firstBuy, baseX, baseY);
                itemRenderer.renderGuiItemOverlay(matrices, textRenderer, firstBuy, baseX, baseY);

                itemRenderer.renderInGui(matrices, secondBuy, baseX + 20, baseY);
                itemRenderer.renderGuiItemOverlay(matrices, textRenderer, secondBuy, baseX + 20, baseY);

                itemRenderer.renderInGui(matrices, sell, baseX + 53, baseY);
                itemRenderer.renderGuiItemOverlay(matrices, textRenderer, sell, baseX + 53, baseY);

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

        modelMatrices.pop();
        modelMatrices.pop();
        RenderSystem.applyModelViewMatrix();
    }

    // from MerchantScreen
    private void renderArrow(MatrixStack matrices, TradeOffer tradeOffer, int x, int y) {
        RenderSystem.enableBlend();
        // RenderSystem.setShader(GameRenderer::getPositionTexShader);
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
