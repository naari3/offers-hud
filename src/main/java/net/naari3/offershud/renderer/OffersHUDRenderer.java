package net.naari3.offershud.renderer;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import me.shedaniel.autoconfig.AutoConfig;
//? if fabric {
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
import net.naari3.offershud.platform.Platform;

/*? if >= 1.21.6 {*/
import net.minecraft.client.renderer.RenderPipelines;
/*?}*/

//? if fabric {
public class OffersHUDRenderer implements HudRenderCallback, Platform.HudRenderer {
//?} else {
/*public class OffersHUDRenderer implements Platform.HudRenderer {
*//*?}*/
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
    //? if fabric {
    @Override
    public void onHudRender(GuiGraphics context, DeltaTracker tickCounter) {
        render(context, tickCounter);
    }
    //?}

    @Override
    public void render(GuiGraphics context, DeltaTracker tickCounter) {
    /*?} else {*/
    /*//? if fabric {
    @Override
    public void onHudRender(GuiGraphics context, float tickDelta) {
        render(context, tickDelta);
    }
    //?}

    @Override
    public void render(GuiGraphics context, float tickDelta) {
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

        MerchantInfo.getInfo().getLastId().ifPresent(lastId -> {
            var offers = MerchantInfo.getInfo().getOffers();

            int screenWidth = client.getWindow().getGuiScaledWidth();
            int screenHeight = client.getWindow().getGuiScaledHeight();
            float scale = config.scale;

            float translateX = config.alignment.isRight() ?
                    screenWidth - (calcWidth(offers, textRenderer) * scale) - config.offsetX :
                    config.offsetX;
            float translateY = config.alignment.isBottom() ?
                    screenHeight - (calcHeight(offers) * scale) - config.offsetY :
                    config.offsetY;

            /*? if >= 1.21.6 {*/
            modelMatrices.pushMatrix();
            /*?} else {*/
            /*modelMatrices.pushPose();
            *//*?}*/;

            /*? if >= 1.21.6 {*/
            modelMatrices.translate(translateX, translateY);
            /*?} else {*/
            /*modelMatrices.translate(translateX, translateY, 1.0);
            *//*?}*/;

            /*? if >= 1.21.6 {*/
            modelMatrices.scale(scale, scale);
            /*?} else {*/
            /*modelMatrices.scale(scale, scale, 1.0f);
            *//*?}*/;

            //? if <1.21.3
            /*RenderSystem.applyModelViewMatrix();*/

            var i = 0;

            for (MerchantOffer offer : offers) {
                var baseX = 0;
                var baseY = 0 + i * 20;

                var firstBuy = offer.getCostA().copy();
                var secondBuy = offer.getCostB().copy();
                var sell = offer.getResult().copy();

                context.renderItem(firstBuy, baseX, baseY);
                context.renderItemDecorations(textRenderer, firstBuy, baseX, baseY);

                context.renderItem(secondBuy, baseX + 20, baseY);
                context.renderItemDecorations(textRenderer, secondBuy, baseX + 20, baseY);

                context.renderItem(sell, baseX + 53, baseY);
                context.renderItemDecorations(textRenderer, sell, baseX + 53, baseY);

                this.renderArrow(context, offer, baseX + -20, baseY);

                var enchantments = getEnchantmentText(offer);

                context.drawString(textRenderer, enchantments, (baseX + 75), (baseY + 5), CommonColors.WHITE);
                i += 1;
            }

            /*? if >= 1.21.6 {*/
            modelMatrices.popMatrix();
            /*?} else {*/
            /*modelMatrices.popPose();
            *//*?}*/;
        });
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

    private String getEnchantmentText(MerchantOffer offer) {
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
        return String.join(", ", enchantments);
    }

    private int calcWidth(List<MerchantOffer> offers, Font textRenderer) {
        int maxWidth = 0;
        for (var offer : offers) {
            var enchantments = getEnchantmentText(offer);
            int width = 75 + textRenderer.width(enchantments);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    private int calcHeight(List<MerchantOffer> offers) {
        return offers.size() * 20;
    }
}
