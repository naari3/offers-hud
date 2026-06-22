package net.naari3.offershud.renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;

import me.shedaniel.autoconfig.AutoConfig;
//? if fabric && < 26.1 {
/*import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
*///?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
/*? if >= 26.1 {*/
import net.minecraft.client.gui.GuiGraphicsExtractor;
/*?} else {*/
/*import net.minecraft.client.gui.GuiGraphics;
*//*?}*/
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
/*? if >= 1.21.3 && < 1.21.6 {*/
/*import net.minecraft.client.renderer.RenderType;
*//*?}*/
/*? if >= 1.21 {*/
import net.minecraft.client.DeltaTracker;
/*?}*/
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
/*? if >= 1.21 {*/
import net.minecraft.tags.EnchantmentTags;
/*?}*/
import net.minecraft.util.CommonColors;
/*? if >= 1.21.11 {*/
import net.minecraft.resources.Identifier;
/*?} else {*/
/*import net.minecraft.resources.ResourceLocation;
*//*?}*/
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.naari3.offershud.MerchantInfo;
import net.naari3.offershud.OffersHUD;
import net.naari3.offershud.TradeProfession;
import net.naari3.offershud.config.ModConfig;
import net.naari3.offershud.platform.Platform;

/*? if >= 1.21.6 {*/
import net.minecraft.client.renderer.RenderPipelines;
/*?}*/

/*? if >= 26.1 {*/
public class OffersHUDRenderer implements Platform.HudRenderer {
/*?} else if fabric {*/
/*public class OffersHUDRenderer implements HudRenderCallback, Platform.HudRenderer {
*//*?} else {*/
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


    /*? if >= 26.1 {*/
    @Override
    public void extractRenderState(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
    /*?} else if >=1.21 {*/
    /*//? if fabric {
    @Override
    public void onHudRender(GuiGraphics context, DeltaTracker tickCounter) {
        render(context, tickCounter);
    }
    //?}

    @Override
    public void render(GuiGraphics context, DeltaTracker tickCounter) {
    *//*?} else {*/
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

        MerchantInfo.getInfo().getLastId().ifPresent(lastId -> {
            var offers = MerchantInfo.getInfo().getOffers();

            /*? if >= 26.1 {*/
            int screenWidth = context.guiWidth();
            int screenHeight = context.guiHeight();
            /*?} else {*/
            /*int screenWidth = client.getWindow().getGuiScaledWidth();
            int screenHeight = client.getWindow().getGuiScaledHeight();
            *//*?}*/
            float scale = config.scale;

            float translateX = config.alignment.isRight() ?
                    screenWidth - (calcWidth(offers, textRenderer) * scale) - config.offsetX :
                    config.offsetX;
            float translateY = config.alignment.isBottom() ?
                    screenHeight - (calcHeight(offers) * scale) - config.offsetY :
                    config.offsetY;

            renderOffers(context, textRenderer, offers, translateX, translateY, scale, config.highlightExtremePrices);
        });
    }

    /*? if >= 26.1 {*/
    public static void renderOffers(GuiGraphicsExtractor context, Font textRenderer, List<MerchantOffer> offers, float originX, float originY, float scale, boolean highlightExtremePrices) {
        renderOffers(context, textRenderer, offers, null, originX, originY, scale, highlightExtremePrices);
    }

    public static void renderOffers(GuiGraphicsExtractor context, Font textRenderer, List<MerchantOffer> offers, List<String> enchantTexts, float originX, float originY, float scale, boolean highlightExtremePrices) {
        var modelMatrices = context.pose();
    /*?} else {*/
    /*public static void renderOffers(GuiGraphics context, Font textRenderer, List<MerchantOffer> offers, float originX, float originY, float scale, boolean highlightExtremePrices) {
        renderOffers(context, textRenderer, offers, null, originX, originY, scale, highlightExtremePrices);
    }

    public static void renderOffers(GuiGraphics context, Font textRenderer, List<MerchantOffer> offers, List<String> enchantTexts, float originX, float originY, float scale, boolean highlightExtremePrices) {
        var modelMatrices = context.pose();
    *//*?}*/

        /*? if >= 1.21.6 {*/
        modelMatrices.pushMatrix();
        /*?} else {*/
        /*modelMatrices.pushPose();
        *//*?}*/;

        /*? if >= 1.21.6 {*/
        modelMatrices.translate(originX, originY);
        /*?} else {*/
        /*modelMatrices.translate(originX, originY, 1.0);
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

            int firstBuyColor = highlightExtremePrices
                    ? computeExtremePriceColor(offer)
                    : COLOR_NORMAL_COST;

            /*? if >= 26.1 {*/
            context.item(firstBuy, baseX, baseY);
            renderFirstBuyDecorations(context, textRenderer, firstBuy, baseX, baseY, firstBuyColor);

            context.item(secondBuy, baseX + 20, baseY);
            context.itemDecorations(textRenderer, secondBuy, baseX + 20, baseY);

            context.item(sell, baseX + 53, baseY);
            context.itemDecorations(textRenderer, sell, baseX + 53, baseY);
            /*?} else {*/
            /*context.renderItem(firstBuy, baseX, baseY);
            renderFirstBuyDecorations(context, textRenderer, firstBuy, baseX, baseY, firstBuyColor);

            context.renderItem(secondBuy, baseX + 20, baseY);
            context.renderItemDecorations(textRenderer, secondBuy, baseX + 20, baseY);

            context.renderItem(sell, baseX + 53, baseY);
            context.renderItemDecorations(textRenderer, sell, baseX + 53, baseY);
            *//*?}*/

            renderArrow(context, offer, baseX + -20, baseY);

            var enchantments = (enchantTexts != null && i < enchantTexts.size())
                    ? enchantTexts.get(i) : getEnchantmentText(offer);

            /*? if >= 26.1 {*/
            context.text(textRenderer, enchantments, (baseX + 75), (baseY + 5), CommonColors.WHITE);
            /*?} else {*/
            /*context.drawString(textRenderer, enchantments, (baseX + 75), (baseY + 5), CommonColors.WHITE);
            *//*?}*/
            i += 1;
        }

        /*? if >= 1.21.6 {*/
        modelMatrices.popMatrix();
        /*?} else {*/
        /*modelMatrices.popPose();
        *//*?}*/;
    }

    private static final int COLOR_NORMAL_COST = 0xFFFFFFFF;
    private static final int COLOR_LOWEST_COST = 0xFF55FF55;
    private static final int COLOR_HIGHEST_COST = 0xFFFF5555;

    // Enchanted-equipment baseEmeraldCost keyed by (profession, item). Verified identical across
    // 1.20.6, 1.21.x and 26.1.x vanilla VillagerTrades. The client never receives this value (it is
    // part of the server-side trade definition), so it must be hardcoded. Rebalanced (experimental)
    // trades use different values and are intentionally out of scope.
    private static final Map<TradeProfession, Map<Item, Integer>> EQUIPMENT_BASE_COSTS = Map.of(
            TradeProfession.TOOLSMITH, Map.of(
                    Items.IRON_AXE, 1, Items.IRON_SHOVEL, 2, Items.IRON_PICKAXE, 3,
                    Items.DIAMOND_AXE, 12, Items.DIAMOND_SHOVEL, 5, Items.DIAMOND_PICKAXE, 13),
            TradeProfession.WEAPONSMITH, Map.of(
                    Items.IRON_SWORD, 2, Items.DIAMOND_AXE, 12, Items.DIAMOND_SWORD, 8),
            TradeProfession.ARMORER, Map.of(
                    Items.DIAMOND_LEGGINGS, 14, Items.DIAMOND_BOOTS, 8,
                    Items.DIAMOND_HELMET, 8, Items.DIAMOND_CHESTPLATE, 16),
            TradeProfession.FISHERMAN, Map.of(Items.FISHING_ROD, 3),
            TradeProfession.FLETCHER, Map.of(Items.BOW, 2, Items.CROSSBOW, 3),
            TradeProfession.WANDERING_TRADER, Map.of(Items.IRON_PICKAXE, 1));

    // Both enchanted books and enchanted equipment price buyA's emeralds with a level-dependent
    // random component, so baseCostA at the low/high bound of its range flags the best/worst roll.
    private static int computeExtremePriceColor(MerchantOffer offer) {
        var result = offer.getResult();
        if (result.is(Items.ENCHANTED_BOOK)) {
            return computeEnchantedBookColor(offer, result);
        }
        return computeEnchantedEquipmentColor(offer, result);
    }

    // Enchanted books: baseCost = (2 + rand[0..4+10k] + 3k), doubled for #double_trade_price
    // (treasure) enchantments, capped at 64. The bounds for a fixed level k are [2+3k, 6+13k].
    private static int computeEnchantedBookColor(MerchantOffer offer, ItemStack result) {
        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(result);
        if (enchantments.size() != 1) {
            return COLOR_NORMAL_COST;
        }
        var entry = enchantments.entrySet().iterator().next();
        int level = entry.getIntValue();
        int min = 2 + 3 * level;
        int max = 6 + 13 * level;
        if (isDoubleTradePrice(entry.getKey())) {
            min *= 2;
            max *= 2;
        }
        min = Math.min(min, 64);
        max = Math.min(max, 64);
        int base = offer.getBaseCostA().getCount();
        if (base <= min) {
            return COLOR_LOWEST_COST;
        }
        if (base >= max) {
            return COLOR_HIGHEST_COST;
        }
        return COLOR_NORMAL_COST;
    }

    // Enchanted equipment: baseCost = min(baseEmeraldCost + i, 64) with i in [5,19] and no treasure
    // multiplier. baseEmeraldCost is not in the offer, so it is resolved from (profession, item).
    private static int computeEnchantedEquipmentColor(MerchantOffer offer, ItemStack result) {
        TradeProfession profession = MerchantInfo.getInfo().getTradeProfession();
        if (profession == null) {
            return COLOR_NORMAL_COST;
        }
        Map<Item, Integer> byItem = EQUIPMENT_BASE_COSTS.get(profession);
        if (byItem == null) {
            return COLOR_NORMAL_COST;
        }
        Integer baseEmeraldCost = byItem.get(result.getItem());
        if (baseEmeraldCost == null) {
            return COLOR_NORMAL_COST;
        }
        // Skip the fixed-price non-enchanted variants (e.g. fletcher's level-2 plain bow).
        if (EnchantmentHelper.getEnchantmentsForCrafting(result).isEmpty()) {
            return COLOR_NORMAL_COST;
        }
        int min = baseEmeraldCost + 5;
        int max = Math.min(baseEmeraldCost + 19, 64);
        int cost = offer.getBaseCostA().getCount();
        if (cost <= min) {
            return COLOR_LOWEST_COST;
        }
        if (cost >= max) {
            return COLOR_HIGHEST_COST;
        }
        return COLOR_NORMAL_COST;
    }

    /*? if >= 1.21 {*/
    private static boolean isDoubleTradePrice(Holder<Enchantment> enchantment) {
        return enchantment.is(EnchantmentTags.DOUBLE_TRADE_PRICE);
    }
    /*?} else {*/
    /*private static boolean isDoubleTradePrice(Holder<Enchantment> enchantment) {
        return enchantment.value().isTreasureOnly();
    }
    *//*?}*/

    /*? if >= 26.1 {*/
    private static void renderFirstBuyDecorations(GuiGraphicsExtractor context, Font font,
            ItemStack stack, int x, int y, int color) {
        if (color == COLOR_NORMAL_COST) {
            context.itemDecorations(font, stack, x, y);
            return;
        }
        var decoStack = stack.copy();
        decoStack.setCount(1);
        context.itemDecorations(font, decoStack, x, y);
        String countText = String.valueOf(stack.getCount());
        int textX = x + 19 - 2 - font.width(countText);
        int textY = y + 6 + 3;
        context.text(font, countText, textX, textY, color);
    }
    /*?} else {*/
    /*private static void renderFirstBuyDecorations(GuiGraphics context, Font font,
            ItemStack stack, int x, int y, int color) {
        if (color == COLOR_NORMAL_COST) {
            context.renderItemDecorations(font, stack, x, y);
            return;
        }
        var decoStack = stack.copy();
        decoStack.setCount(1);
        context.renderItemDecorations(font, decoStack, x, y);
        String countText = String.valueOf(stack.getCount());
        int textX = x + 19 - 2 - font.width(countText);
        int textY = y + 6 + 3;
        context.drawString(font, countText, textX, textY, color);
    }
    *//*?}*/

    // from MerchantScreen
    /*? if >= 26.1 {*/
    private static void renderArrow(GuiGraphicsExtractor context, MerchantOffer tradeOffer, int x, int y) {
    /*?} else {*/
    /*private static void renderArrow(GuiGraphics context, MerchantOffer tradeOffer, int x, int y) {
    *//*?}*/
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

    private static String getEnchantmentText(MerchantOffer offer) {
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

    public static int calcWidth(List<MerchantOffer> offers, Font textRenderer) {
        return calcWidth(offers, null, textRenderer);
    }

    public static int calcWidth(List<MerchantOffer> offers, List<String> enchantTexts, Font textRenderer) {
        int maxWidth = 0;
        for (int i = 0; i < offers.size(); i++) {
            var enchantments = (enchantTexts != null && i < enchantTexts.size())
                    ? enchantTexts.get(i) : getEnchantmentText(offers.get(i));
            int width = 75 + textRenderer.width(enchantments);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    public static int calcHeight(List<MerchantOffer> offers) {
        return offers.size() * 20;
    }
}
