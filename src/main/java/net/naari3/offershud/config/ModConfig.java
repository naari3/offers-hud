package net.naari3.offershud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.naari3.offershud.OffersHUD;

@Config(name = OffersHUD.MODID)
public class ModConfig implements ConfigData {
    public boolean enabled = true;
    public boolean ignoreNoProfession = true;
    public boolean suppressVillagerHeadRolling = false;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public Alignment alignment = Alignment.TOP_LEFT;

    public int offsetX = 5;
    public int offsetY = 5;
    public float scale = 1.0f;

    public enum Alignment {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
        public boolean isRight() {
            return this == TOP_RIGHT || this == BOTTOM_RIGHT;
        }
        public boolean isBottom() {
            return this == BOTTOM_LEFT || this == BOTTOM_RIGHT;
        }
    }
}
