package net.naari3.offershud.renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.mojang.blaze3d.systems.RenderSystem;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.naari3.offershud.MerchantInfo;
import net.naari3.offershud.OffersHUD;
import net.naari3.offershud.config.ModConfig;

public class OffersHUDRenderer implements HudRenderCallback {
    private static final Identifier TEXTURE = new Identifier(OffersHUD.MODID, "textures/gui/container/villager2.png");

    public void onHudRender(DrawContext context, float tickDelta) {
        var config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        if (!config.enabled)
            return;

        final var client = MinecraftClient.getInstance();
        final var player = client.player;
        if (player == null)
            return;

        final var textRenderer = client.textRenderer;
        // final var itemRenderer = client.getItemRenderer();

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);

        var modelMatrices = context.getMatrices();
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

                context.drawItem(firstBuy, baseX, baseY);
                context.drawItemInSlot(textRenderer, firstBuy, baseX, baseY);

                context.drawItem(secondBuy, baseX + 20, baseY);
                context.drawItemInSlot(textRenderer, secondBuy, baseX + 20, baseY);

                context.drawItem(sell, baseX + 53, baseY);
                context.drawItemInSlot(textRenderer, sell, baseX + 53, baseY);

                this.renderArrow(context, offer, baseX + -20, baseY);

                List<String> enchantments = new ArrayList<>();

                var map = EnchantmentHelper.get(offer.getSellItem());

                for (Entry<Enchantment, Integer> entry : map.entrySet()) {
                    enchantments.add(entry.getKey().getName(entry.getValue()).getString());
                }

                context.drawTextWithShadow(textRenderer, String.join(", ", enchantments), (baseX + 75), (baseY + 5),
                        0xFFFFFF);
                i += 1;
            }
        });

        modelMatrices.pop();
        modelMatrices.pop();
    }

    // from MerchantScreen
    private void renderArrow(DrawContext context, TradeOffer tradeOffer, int x, int y) {
        if (tradeOffer.isDisabled()) {
            context.drawTexture(TEXTURE, x + 5 + 35 + 20, y + 3, 0, 25.0F, 171.0F, 10, 9, 512,
                    256);
        } else {
            context.drawTexture(TEXTURE, x + 5 + 35 + 20, y + 3, 0, 15.0F, 171.0F, 10, 9, 512,
                    256);
        }
    }
}
