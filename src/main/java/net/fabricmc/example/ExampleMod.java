package net.fabricmc.example;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;

public class ExampleMod implements ClientModInitializer {
    public static final Logger logger = LogManager.getLogger("villagertradinglist");

    @Override
    public void onInitializeClient() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        logger.info("Hello Fabric world!");
        ClientTickEvents.END_CLIENT_TICK.register(e -> {
            var mc = MinecraftClient.getInstance();
            var crosshairTarget = mc.crosshairTarget;

            if (Objects.isNull(crosshairTarget) || crosshairTarget.getType() != Type.ENTITY) {
                return;
            }

            var entityHit = (EntityHitResult) crosshairTarget;
            var entity = entityHit.getEntity();
            if (entity.getType() != EntityType.VILLAGER) { // TODO: Support MerchantEntity
                return;
            }

            var villager = (VillagerEntity) entity;
            villager.getOffers().forEach(o -> logger.info(o.getSellItem().getItem()));
        });
    }
}
