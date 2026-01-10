package net.naari3.offershud.renderer;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
/*? if >= 1.21.3 && < 1.21.6 {*/
/*import net.minecraft.client.renderer.RenderType;
*//*?}*/
/*? if >= 1.21 {*/
import net.minecraft.client.DeltaTracker;
/*?}*/
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.util.CommonColors;
/*? if >= 1.21.11 {*/
import net.minecraft.resources.Identifier;
/*?} else {*/
/*import net.minecraft.resources.ResourceLocation;
*//*?}*/
import net.minecraft.world.item.trading.MerchantOffer;
import net.naari3.offershud.MerchantInfo;
import net.naari3.offershud.OffersHUD;
import net.naari3.offershud.config.ModConfig;
import org.joml.Matrix3x2f;

/*? if >= 1.21.6 {*/
import net.minecraft.client.renderer.RenderPipelines;
/*?}*/

public class OffersHUDRenderer implements HudRenderCallback {
    /*? if >= 1.21.11 {*/
    private static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath(OffersHUD.MODID, "textures/gui/container/villager2.png");
    /*?} else if >= 1.21 {*/
    /*private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(OffersHUD.MODID, "textures/gui/container/villager2.png");
    *//*?} else {*/
    /*private static final ResourceLocation TEXTURE =
        new ResourceLocation(OffersHUD.MODID, "textures/gui/container/villager2.png");
    *//*?}*/


    /*? if >=1.21 {*/
    @Override
    public void onHudRender(GuiGraphics context, DeltaTracker tickCounter) {
    /*?} else {*/
     /*public void onHudRender(GuiGraphics context, float tickDelta) {
    *//*?}*/
        var config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        if (!config.enabled)
            return;

        final var client = Minecraft.getInstance();
        final var player = client.player;
        if (player == null)
            return;

        final var textRenderer = client.font;
        // final var itemRenderer = client.getItemRenderer();

        /*? if < 1.21.5 {*/
        /*RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, AbstractContainerScreen.INVENTORY_LOCATION);
        *//*?}*/

        var modelMatrices = context.pose();

        /*? if >= 1.21.6 {*/
        modelMatrices.pushMatrix();
        /*?} else {*/
        /*modelMatrices.pushPose();
         *//*?}*/;

        /*? if >= 1.21.6 {*/
        modelMatrices.translate(config.offsetX, config.offsetY);
        /*?} else {*/
        /*modelMatrices.translate(config.offsetX, config.offsetY, 1.0);
         *//*?}*/;

        /*? if >= 1.21.6 {*/
        modelMatrices.pushMatrix();
        /*?} else {*/
        /*modelMatrices.pushPose();
         *//*?}*/;


        /*? if >= 1.21.6 {*/
        modelMatrices.scale(config.scale, config.scale);
        /*?} else {*/
        /*modelMatrices.scale(config.scale, config.scale, 1.0f);
         *//*?}*/;

        //? if <1.21.3
        /*RenderSystem.applyModelViewMatrix();*/

        MerchantInfo.getInfo().getLastId().ifPresent(lastId -> {
            var offers = MerchantInfo.getInfo().getOffers();
            var i = 0;

            for (MerchantOffer offer : offers) {
                var baseX = 0;
                var baseY = 0 + i * 20;

                var firstBuy = offer.getCostA().copy();
                var secondBuy = offer.getCostB().copy();
                var sell = offer.getResult().copy();

                context.renderItem(firstBuy, baseX, baseY);
                //? if >=1.21.3 {
                context.renderItemDecorations(textRenderer, firstBuy, baseX, baseY);
                //?} elif <1.21.3 {
                 /*context.renderItemDecorations(textRenderer, firstBuy, baseX, baseY);
                *///?}

                context.renderItem(secondBuy, baseX + 20, baseY);
                //? if >=1.21.3 {
                context.renderItemDecorations(textRenderer, secondBuy, baseX + 20, baseY);
                //?} elif <1.21.3 {
                 /*context.renderItemDecorations(textRenderer, secondBuy, baseX + 20, baseY);
                *///?}

                context.renderItem(sell, baseX + 53, baseY);
                //? if >=1.21.3 {
                context.renderItemDecorations(textRenderer, sell, baseX + 53, baseY);
                //?} elif <1.21.3 {
                 /*context.renderItemDecorations(textRenderer, sell, baseX + 53, baseY);
                *///?}

                this.renderArrow(context, offer, baseX + -20, baseY);

                List<String> enchantments = new ArrayList<>();

                /*? if >= 1.21 {*/
                var itemEnchantmentsComponent = EnchantmentHelper.getEnchantmentsForCrafting(offer.getResult());
                if (EnchantmentHelper.hasAnyEnchantments(offer.getResult())) {
                    for (var entry : itemEnchantmentsComponent.entrySet()) {
                        var level = entry.getIntValue();
                        enchantments.add(Enchantment.getFullname(entry.getKey(), level).getString());
                    }
                }
                /*?} else if >= 1.20.6 {*/
                /*var itemEnchantmentsComponent = EnchantmentHelper.getEnchantmentsForCrafting(offer.getResult());
                if (EnchantmentHelper.hasAnyEnchantments(offer.getResult())) {
                    for (var entry : itemEnchantmentsComponent.entrySet()) {
                        var enchantment = entry.getKey().value();
                        var level = entry.getIntValue();
                        enchantments.add(enchantment.getFullname(level).getString());
                    }
                }
                *//*?} else {*/
                /*var map = EnchantmentHelper.getEnchantments(offer.getResult());
                for (var entry : map.entrySet()) {
                    var enchantment = entry.getKey();
                    var level = entry.getValue();
                    enchantments.add(enchantment.getFullname(level).getString());
                }
                *//*?}*/

                context.drawString(textRenderer, String.join(", ", enchantments), (baseX + 75), (baseY + 5), CommonColors.WHITE);
                i += 1;
            }
        });

        /*? if >= 1.21.6 {*/
        modelMatrices.popMatrix();
        /*?} else {*/
        /*modelMatrices.popPose();
         *//*?}*/;

        /*? if >= 1.21.6 {*/
        modelMatrices.popMatrix();
        /*?} else {*/
        /*modelMatrices.popPose();
         *//*?}*/;
    }

    // from MerchantScreen
    private void renderArrow(GuiGraphics context, MerchantOffer tradeOffer, int x, int y) {
        if (tradeOffer.isOutOfStock()) {
            //? if >=1.21.6 {
            context.blit(
                    RenderPipelines.GUI_TEXTURED, TEXTURE,
                    x + 5 + 35 + 20, y + 3,
                    25.0F, 171.0F,
                    10, 9,
                    512, 256);
            //?} elif >=1.21.3 {
            /*context.blit(
                    RenderType::guiTextured, TEXTURE,
                    x + 5 + 35 + 20, y + 3,
                    25.0F, 171.0F,
                    10, 9,
                    512, 256);
            *///?} elif <1.21.3 {
             /*context.blit(TEXTURE, x + 5 + 35 + 20, y + 3, 0, 25.0F, 171.0F, 10, 9, 512, 256);
            *///?}
        } else {
            //? if >=1.21.6 {
            context.blit(
                    RenderPipelines.GUI_TEXTURED, TEXTURE,
                    x + 5 + 35 + 20, y + 3,
                    15.0F, 171.0F,
                    10, 9,
                    512, 256);
            //?} elif >=1.21.3 {
            /*context.blit(
                    RenderType::guiTextured, TEXTURE,
                    x + 5 + 35 + 20, y + 3,
                    15.0F, 171.0F, 10, 9,
                    512, 256);
            *///?} elif <1.21.3 {
             /*context.blit(TEXTURE, x + 5 + 35 + 20, y + 3, 0, 15.0F, 171.0F, 10, 9, 512, 256);
            *///?}
        }
    }
}
