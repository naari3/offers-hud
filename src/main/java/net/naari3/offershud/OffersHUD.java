package net.naari3.offershud;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
//? if fabric {
import net.fabricmc.api.ClientModInitializer;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
/*? if >= 1.21.11 {*/
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
/*?} else {*/
/*import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
*//*?}*/
import net.minecraft.world.item.Items;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.naari3.offershud.config.ModConfig;
import net.naari3.offershud.platform.Platform;
import net.naari3.offershud.renderer.OffersHUDRenderer;

//? if fabric {
public class OffersHUD implements ClientModInitializer {
//?} else {
/*public class OffersHUD {
*//*?}*/
    public static final String MODID = "offershud";
    public static final Logger logger = LogManager.getLogger(MODID);
    public static boolean openWindow = false;
    public static List<MerchantOffer> SAMPLE_OFFERS = new ArrayList<>();
    public static final List<String> SAMPLE_ENCHANT_TEXTS = List.of("", "Fortune III", "");
    private static ModConfig config;

    //? if fabric {
    @Override
    public void onInitializeClient() {
        init();
        net.naari3.offershud.config.ModConfigGui.registerGui();
    }
    //?}

    public static void init() {
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        logger.info("Hello from OffersHUD!");
        var platform = Platform.getInstance();

        platform.registerClientTickHandler(() -> {
            if (!config.enabled)
                return;
            var mc = Minecraft.getInstance();
            if (mc == null)
                return;
            // If the player is in a screen, do nothing.
            // this fixes #35 (The screen closes when a villager crosses the crosshair)
            // see: https://github.com/naari3/offers-hud/issues/35
            /*? if >= 26.2 {*/
            if (mc.gui.screen() != null)
                return;
            /*?} else {*/
            /*if (mc.screen != null)
                return;
            *//*?}*/
            var entity = getUpdatableEntity(mc);
            if (entity != null) {
                if (MerchantInfo.getInfo().getLastId().isPresent()
                        && MerchantInfo.getInfo().getLastId().get() == entity.getId()) {
                    return;
                }

                MerchantInfo.getInfo().setOffers(new ArrayList<>());
                MerchantInfo.getInfo().setLastId(entity.getId());

                if (mc.player != null) {
                    /*? if >= 26.1 {*/
                    platform.sendPacketToServer(
                            new ServerboundInteractPacket(entity.getId(),
                                    InteractionHand.MAIN_HAND,
                                    net.minecraft.world.phys.Vec3.ZERO,
                                    mc.player.isShiftKeyDown()));
                    /*?} else {*/
                    /*platform.sendPacketToServer(
                            ServerboundInteractPacket.createInteractionPacket(entity,
                                    mc.player.isShiftKeyDown(),
                                    InteractionHand.MAIN_HAND));
                    *//*?}*/
                }
            } else {
                MerchantInfo.getInfo().setLastId(null);
                MerchantInfo.getInfo().setTradeProfession(null);
            }
        });

        platform.registerHudRenderer(new OffersHUDRenderer());

        SAMPLE_OFFERS = buildSampleOffers();
        logger.info("Pre-generated {} sample offer(s)", SAMPLE_OFFERS.size());
    }

    public static List<MerchantOffer> buildSampleOffers() {
        try {
            List<MerchantOffer> list = new ArrayList<>();
            /*? if >= 26.2 {*/
            var pred = net.minecraft.core.component.DataComponentExactPredicate.EMPTY;
            // Holder.direct avoids "Components not bound yet" on the title screen,
            // but ITEM_MODEL must be included so the renderer can find the model.
            java.util.function.Function<net.minecraft.world.item.Item, net.minecraft.core.Holder<net.minecraft.world.item.Item>> h = item ->
                net.minecraft.core.Holder.direct(item,
                    net.minecraft.core.component.DataComponentMap.builder()
                        .set(net.minecraft.core.component.DataComponents.ITEM_MODEL,
                            net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item))
                        .build());
            var emerald = h.apply(Items.EMERALD);
            var book = h.apply(Items.BOOK);
            var diamond = h.apply(Items.DIAMOND);
            var enchBook = h.apply(Items.ENCHANTED_BOOK);
            var bread = h.apply(Items.BREAD);
            list.add(new MerchantOffer(new ItemCost(emerald, 5, pred), Optional.empty(),
                    new ItemStack(diamond, 1), 12, 5, 0.05f));
            list.add(new MerchantOffer(new ItemCost(emerald, 20, pred), Optional.of(new ItemCost(book, 1, pred)),
                    new ItemStack(enchBook, 1), 3, 10, 0.2f));
            list.add(new MerchantOffer(new ItemCost(emerald, 1, pred), Optional.empty(),
                    new ItemStack(bread, 6), 16, 1, 0.05f));
            /*?} else {*/
            /*list.add(new MerchantOffer(new ItemCost(Items.EMERALD, 5), Optional.empty(),
                    new ItemStack(Items.DIAMOND), 12, 5, 0.05f));
            list.add(new MerchantOffer(new ItemCost(Items.EMERALD, 20), Optional.of(new ItemCost(Items.BOOK)),
                    buildEnchantedBook(), 3, 10, 0.2f));
            list.add(new MerchantOffer(new ItemCost(Items.EMERALD, 1), Optional.empty(),
                    new ItemStack(Items.BREAD, 6), 16, 1, 0.05f));
            *//*?}*/
            return list;
        } catch (Exception e) {
            logger.debug("buildSampleOffers failed: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /*? if < 26.2 {*/
    /*private static ItemStack buildEnchantedBook() {
        return new ItemStack(Items.ENCHANTED_BOOK);
    }
    *//*?}*/

    private static Entity getUpdatableEntity(Minecraft mc) {
        if (OffersHUD.getOpenWindow()) {
            return null;
        }

        // TODO: more scaler https://fabricmc.net/wiki/tutorial:pixel_raycast
        var hitResult = mc.hitResult;

        if (Objects.isNull(hitResult) || hitResult.getType() != Type.ENTITY) {
            return null;
        }

        var entityHit = (EntityHitResult) hitResult;
        var entity = entityHit.getEntity();
        if (!(entity instanceof Merchant)) {
            return null;
        }

        // getUpdatableEntity guarantees a Merchant; a non-Villager Merchant is the wandering trader.
        TradeProfession tradeProfession = TradeProfession.WANDERING_TRADER;
        if (entity instanceof Villager villager) {
            /*? if >= 1.21.5 {*/
            Holder<VillagerProfession> professionEntry = villager.getVillagerData().profession();
            if (config.ignoreNoProfession
                    && (professionEntry.is(VillagerProfession.NONE) || professionEntry.is(VillagerProfession.NITWIT))) {
                return null;
            }
            tradeProfession = toTradeProfession(professionEntry);
            /*?} else {*/
            /*var profession = villager.getVillagerData().getProfession();
            if (config.ignoreNoProfession
                    && (profession == VillagerProfession.NONE || profession == VillagerProfession.NITWIT)) {
                return null;
            }
            tradeProfession = toTradeProfession(profession);
            *//*?}*/

            var player = mc.player;
            ItemStack item = null;
            if (player != null) {
                item = player.getMainHandItem();
                if (item.is(Items.VILLAGER_SPAWN_EGG) || item.is(Items.NAME_TAG)) {
                    return null;
                }
            }
        }

        // Record the trader's profession alongside the resolved entity so the renderer can resolve
        // the per-(profession, item) baseEmeraldCost of enchanted-equipment trades.
        MerchantInfo.getInfo().setTradeProfession(tradeProfession);
        return entity;
    }

    // Map the trader's profession to the version-independent key used by the base-cost table.
    // Returns null for professions that have no enchanted-equipment trades.
    /*? if >= 1.21.5 {*/
    private static TradeProfession toTradeProfession(Holder<VillagerProfession> profession) {
        if (profession.is(VillagerProfession.ARMORER)) return TradeProfession.ARMORER;
        if (profession.is(VillagerProfession.WEAPONSMITH)) return TradeProfession.WEAPONSMITH;
        if (profession.is(VillagerProfession.TOOLSMITH)) return TradeProfession.TOOLSMITH;
        if (profession.is(VillagerProfession.FISHERMAN)) return TradeProfession.FISHERMAN;
        if (profession.is(VillagerProfession.FLETCHER)) return TradeProfession.FLETCHER;
        return null;
    }
    /*?} else {*/
    /*private static TradeProfession toTradeProfession(VillagerProfession profession) {
        if (profession == VillagerProfession.ARMORER) return TradeProfession.ARMORER;
        if (profession == VillagerProfession.WEAPONSMITH) return TradeProfession.WEAPONSMITH;
        if (profession == VillagerProfession.TOOLSMITH) return TradeProfession.TOOLSMITH;
        if (profession == VillagerProfession.FISHERMAN) return TradeProfession.FISHERMAN;
        if (profession == VillagerProfession.FLETCHER) return TradeProfession.FLETCHER;
        return null;
    }
    *//*?}*/

    public static boolean getOpenWindow() {
        return OffersHUD.openWindow;
    }

    public static void setOpenWindow(boolean newValue) {
        OffersHUD.openWindow = newValue;
    }

    public static ModConfig getConfig() {
        return config;
    }
}
