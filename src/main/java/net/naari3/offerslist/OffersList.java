package net.naari3.offerslist;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.village.VillagerProfession;

public class OffersList implements ClientModInitializer {
    public static final Logger logger = LogManager.getLogger("offerslist");
    public static boolean openWindow = false;

    @Override
    public void onInitializeClient() {
        logger.info("Hello Fabric world!");
        var mc = MinecraftClient.getInstance();

        ClientTickEvents.END_WORLD_TICK.register(e -> {
            var entity = this.getUpdatableEntity(mc);
            if (entity != null) {
                if (MerchantInfo.getInfo().getLastId().isPresent()
                        && MerchantInfo.getInfo().getLastId().get() == entity.getId()) {
                    return;
                }

                MerchantInfo.getInfo().setLastId(entity.getId());

                ClientPlayNetworking.getSender()
                        .sendPacket(PlayerInteractEntityC2SPacket.interact(entity,
                                mc.player.isSneaking(),
                                Hand.MAIN_HAND));
            } else {
                MerchantInfo.getInfo().setLastId(null);
            }

        });
    }

    private Entity getUpdatableEntity(MinecraftClient mc) {
        if (OffersList.getOpenWindow()) {
            return null;
        }

        // TODO: more scaler https://fabricmc.net/wiki/tutorial:pixel_raycast
        var crosshairTarget = mc.crosshairTarget;

        if (Objects.isNull(crosshairTarget) || crosshairTarget.getType() != Type.ENTITY) {
            return null;
        }

        var entityHit = (EntityHitResult) crosshairTarget;
        var entity = entityHit.getEntity();
        if (!(entity instanceof MerchantEntity)) {
            return null;
        }

        var merchant = (MerchantEntity) entity;
        if (entity instanceof VillagerEntity villager
                && villager.getVillagerData().getProfession() == VillagerProfession.NONE) {
            return null;
        }

        return merchant;
    }

    public static boolean getOpenWindow() {
        return OffersList.openWindow;
    }

    public static void setOpenWindow(boolean newValue) {
        OffersList.openWindow = newValue;
    }
}
