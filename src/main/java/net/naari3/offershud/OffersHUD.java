package net.naari3.offershud;

import java.util.ArrayList;
import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.village.Merchant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.village.VillagerProfession;
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
        var mc = MinecraftClient.getInstance();

        ClientTickEvents.END_WORLD_TICK.register(e -> {
            if (!config.enabled)
                return;
            // If the player is in a screen, do nothing.
            // this fixes #35 (The screen closes when a villager crosses the crosshair)
            // see: https://github.com/naari3/offers-hud/issues/35
            if (mc.currentScreen != null)
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
                            .sendPacket(PlayerInteractEntityC2SPacket.interact(entity,
                                    mc.player.isSneaking(),
                                    Hand.MAIN_HAND));
                }
            } else {
                MerchantInfo.getInfo().setLastId(null);
            }
        });

        HudRenderCallback.EVENT.register(new OffersHUDRenderer());
    }

    private Entity getUpdatableEntity(MinecraftClient mc) {
        if (OffersHUD.getOpenWindow()) {
            return null;
        }

        // TODO: more scaler https://fabricmc.net/wiki/tutorial:pixel_raycast
        var crosshairTarget = mc.crosshairTarget;

        if (Objects.isNull(crosshairTarget) || crosshairTarget.getType() != Type.ENTITY) {
            return null;
        }

        var entityHit = (EntityHitResult) crosshairTarget;
        var entity = entityHit.getEntity();
        if (!(entity instanceof Merchant)) {
            return null;
        }

        if (entity instanceof VillagerEntity villager) {
            /* if >= 1.21.5 {*/
            RegistryEntry<VillagerProfession> professionEntry = villager.getVillagerData().profession();
            if (config.ignoreNoProfession
                    && (Objects.equals(professionEntry.getIdAsString(), VillagerProfession.NONE.getValue().toString()) || Objects.equals(professionEntry.getIdAsString(), VillagerProfession.NITWIT.getValue().toString()))) {
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
                item = player.getMainHandStack();
                if (item.isOf(Items.VILLAGER_SPAWN_EGG) || item.isOf(Items.NAME_TAG)) {
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
