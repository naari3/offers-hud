package net.naari3.offershud;

import java.util.ArrayList;
import java.util.Objects;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.world.item.trading.Merchant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
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
import net.naari3.offershud.renderer.OffersHUDRenderer;

public class OffersHUD implements ClientModInitializer {
    public static final String MODID = "offershud";
    public static final Logger logger = LogManager.getLogger(MODID);
    public static boolean openWindow = false;
    private static ModConfig config;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        logger.info("Hello Fabric world!");
        var mc = Minecraft.getInstance();

        ClientTickEvents.END_WORLD_TICK.register(e -> {
            if (!config.enabled)
                return;
            // If the player is in a screen, do nothing.
            // this fixes #35 (The screen closes when a villager crosses the crosshair)
            // see: https://github.com/naari3/offers-hud/issues/35
            if (mc.screen != null)
                return;
            var entity = this.getUpdatableEntity(mc);
            if (entity != null) {
                if (MerchantInfo.getInfo().getLastId().isPresent()
                        && MerchantInfo.getInfo().getLastId().get() == entity.getId()) {
                    return;
                }

                MerchantInfo.getInfo().setOffers(new ArrayList<>());
                MerchantInfo.getInfo().setLastId(entity.getId());

                if (mc.player != null) {
                    ClientPlayNetworking.getSender()
                            .sendPacket(ServerboundInteractPacket.createInteractionPacket(entity,
                                    mc.player.isShiftKeyDown(),
                                    InteractionHand.MAIN_HAND));
                }
            } else {
                MerchantInfo.getInfo().setLastId(null);
            }
        });

        HudRenderCallback.EVENT.register(new OffersHUDRenderer());
    }

    private Entity getUpdatableEntity(Minecraft mc) {
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

        if (entity instanceof Villager villager) {
            /*? if >= 1.21.5 {*/
            Holder<VillagerProfession> professionEntry = villager.getVillagerData().profession();
            if (config.ignoreNoProfession
                    && (professionEntry.is(VillagerProfession.NONE) || professionEntry.is(VillagerProfession.NITWIT))) {
                return null;
            }
            /*?} else {*/
            /*var profession = villager.getVillagerData().getProfession();
            if (config.ignoreNoProfession
                    && (profession == VillagerProfession.NONE || profession == VillagerProfession.NITWIT)) {
                return null;
            }
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

        return entity;
    }

    public static boolean getOpenWindow() {
        return OffersHUD.openWindow;
    }

    public static void setOpenWindow(boolean newValue) {
        OffersHUD.openWindow = newValue;
    }
}
