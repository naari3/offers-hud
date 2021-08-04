package net.fabricmc.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;

public class ExampleMod implements ClientModInitializer {
    public static final Logger logger = LogManager.getLogger("villagertradinglist");

    @Override
    public void onInitializeClient() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        logger.info("Hello Fabric world!");
    }
}
