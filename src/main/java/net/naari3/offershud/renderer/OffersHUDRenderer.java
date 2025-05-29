package net.naari3.offershud.renderer;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.naari3.offershud.MerchantInfo;
import net.naari3.offershud.OffersHUD;
import net.naari3.offershud.config.ModConfig;

public class OffersHUDRenderer implements HudRenderCallback {
    private static final Identifier TEXTURE =
        /*? if <1.21 {*/ /*new Identifier(OffersHUD.MODID, "textures/gui/container/villager2.png") *//*?} else {*/
        Identifier.of(OffersHUD.MODID, "textures/gui/container/villager2.png")
        /*?}*/;


    /*? if >=1.21 {*/
    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
    /*?} else {*/
     /*public void onHudRender(DrawContext context, float tickDelta) {
    *//*?}*/
        var config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        if (!config.enabled)
            return;

        final var client = MinecraftClient.getInstance();
        final var player = client.player;
        if (player == null)
            return;

        final var textRenderer = client.textRenderer;
        // final var itemRenderer = client.getItemRenderer();

        /*? if < 1.21.5 {*/
        /*RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
        *//*?}*/

        var modelMatrices = context.getMatrices();
        modelMatrices.push();
        modelMatrices.translate(config.offsetX, config.offsetY, 1.0);
        modelMatrices.push();
        modelMatrices.scale(config.scale, config.scale, 1.0f);
        //? if <1.21.3
        /*RenderSystem.applyModelViewMatrix();*/

        MerchantInfo.getInfo().getLastId().ifPresent(lastId -> {
            var offers = MerchantInfo.getInfo().getOffers();
            var i = 0;

            for (TradeOffer offer : offers) {
                var baseX = 0;
                var baseY = 0 + i * 20;

                var firstBuy =
                        /*? if >=1.20.6 {*/
                         offer.getDisplayedFirstBuyItem().copy() 
                        /*?} else {*/
                        /*offer.getAdjustedFirstBuyItem().copy()
                        *//*?}*/;
                var secondBuy =
                        /*? if >=1.20.6 {*/
                         offer.getDisplayedSecondBuyItem().copy() 
                        /*?} else {*/
                        /*offer.getSecondBuyItem().copy()
                        *//*?}*/;
                var sell = offer.getSellItem().copy();

                context.drawItem(firstBuy, baseX, baseY);
                //? if >=1.21.3 {
                context.drawStackOverlay(textRenderer, firstBuy, baseX, baseY);
                //?} elif <1.21.3 {
                 /*context.drawItemInSlot(textRenderer, firstBuy, baseX, baseY);
                *///?}

                context.drawItem(secondBuy, baseX + 20, baseY);
                //? if >=1.21.3 {
                context.drawStackOverlay(textRenderer, secondBuy, baseX + 20, baseY);
                //?} elif <1.21.3 {
                 /*context.drawItemInSlot(textRenderer, secondBuy, baseX + 20, baseY);
                *///?}

                context.drawItem(sell, baseX + 53, baseY);
                //? if >=1.21.3 {
                context.drawStackOverlay(textRenderer, sell, baseX + 53, baseY);
                //?} elif <1.21.3 {
                 /*context.drawItemInSlot(textRenderer, sell, baseX + 53, baseY);
                *///?}

                this.renderArrow(context, offer, baseX + -20, baseY);

                List<String> enchantments = new ArrayList<>();

                /*? if >= 1.20.6 {*/
                var itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(offer.getSellItem());
                if (EnchantmentHelper.hasEnchantments(offer.getSellItem())) {
                    /*? if >=1.21 {*/
                     for (var entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
                    /*?} else {*/
                    /*for (var entry : itemEnchantmentsComponent.getEnchantmentsMap()) {
                    var enchantment = entry.getKey().value();
                    *//*?}*/
                        var level = entry.getIntValue();
                        /*? if >=1.21 {*/
                        enchantments.add(Enchantment.getName(entry.getKey(), level).getString());
                        /*?} else {*/
                         /*enchantments.add(enchantment.getName(level).getString());
                        *//*?}*/

                    }
                }
                /*?} else {*/
                /*var map = EnchantmentHelper.get(offer.getSellItem());
                for (var entry : map.entrySet()) {
                    var enchantment = entry.getKey();
                    var level = entry.getValue();
                    enchantments.add(enchantment.getName(level).getString());
                }
                *//*?}*/

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
            //? if >=1.21.3 {
            context.drawTexture(
                    RenderLayer::getGuiTextured, TEXTURE,
                    x + 5 + 35 + 20, y + 3,
                    25.0F, 171.0F,
                    10, 9,
                    512, 256);
            //?} elif <1.21.3 {
             /*context.drawTexture(TEXTURE, x + 5 + 35 + 20, y + 3, 0, 25.0F, 171.0F, 10, 9, 512, 256);
            *///?}
        } else {
            //? if >=1.21.3 {
            context.drawTexture(
                    RenderLayer::getGuiTextured, TEXTURE,
                    x + 5 + 35 + 20, y + 3,
                    15.0F, 171.0F, 10, 9,
                    512, 256);
            //?} elif <1.21.3 {
             /*context.drawTexture(TEXTURE, x + 5 + 35 + 20, y + 3, 0, 15.0F, 171.0F, 10, 9, 512, 256);
            *///?}
        }
    }
}
