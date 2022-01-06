package net.fabricmc.example;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.village.VillagerProfession;

public class ExampleMod implements ClientModInitializer {
    public static final Logger logger = LogManager.getLogger("villagertradinglist");
    public static boolean openWindow = false;

    @Override
    public void onInitializeClient() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        logger.info("Hello Fabric world!");

        ClientTickEvents.END_WORLD_TICK.register(e -> {
            if (ExampleMod.getOpenWindow()) {
                MerchantInfo.getInfo().setLastId(null);
                return;
            }

            var mc = MinecraftClient.getInstance();
            // TODO: more scaler https://fabricmc.net/wiki/tutorial:pixel_raycast
            var crosshairTarget = mc.crosshairTarget;

            if (Objects.isNull(crosshairTarget) || crosshairTarget.getType() != Type.ENTITY) {
                MerchantInfo.getInfo().setLastId(null);
                return;
            }

            var entityHit = (EntityHitResult) crosshairTarget;
            var entity = entityHit.getEntity();
            if (!(entity instanceof MerchantEntity)) {
                MerchantInfo.getInfo().setLastId(null);
                return;
            }

            var merchant = (MerchantEntity) entity;
            if (MerchantInfo.getInfo().getLastId().isPresent()
                    && MerchantInfo.getInfo().getLastId().get() == entity.getId()) {
                return;
            }

            if (entity instanceof VillagerEntity villager
                    && villager.getVillagerData().getProfession() == VillagerProfession.NONE) {
                MerchantInfo.getInfo().setLastId(null);
                return;
            }

            MerchantInfo.getInfo().setLastId(entity.getId());

            ClientPlayNetworking.getSender()
                    .sendPacket(PlayerInteractEntityC2SPacket.interact(merchant,
                            mc.player.isSneaking(),
                            Hand.MAIN_HAND));
        });
    }

    public static boolean getOpenWindow() {
        return ExampleMod.openWindow;
    }

    public static void setOpenWindow(boolean newValue) {
        ExampleMod.openWindow = newValue;
    }
}
