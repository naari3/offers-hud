package net.naari3.offershud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.naari3.offershud.OffersHUD;

@Config(name = OffersHUD.MODID)
public class ModConfig implements ConfigData {
    public boolean enabled = true;
    public boolean ignoreNoProfession = true;
    public boolean suppressVillagerHeadRolling = false;
}
