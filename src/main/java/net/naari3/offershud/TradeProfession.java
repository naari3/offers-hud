package net.naari3.offershud;

/**
 * Version-independent identifier for the trader behind the offers currently shown.
 * Used as a key into the enchanted-equipment base-cost table, decoupling our logic
 * from Minecraft's {@code VillagerProfession} representation (which changed from an
 * enum to a {@code Holder} in 1.21.5).
 */
public enum TradeProfession {
    ARMORER,
    WEAPONSMITH,
    TOOLSMITH,
    FISHERMAN,
    FLETCHER,
    WANDERING_TRADER;
}
