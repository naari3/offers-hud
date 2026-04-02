//? if fabric {
package net.naari3.offershud.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/*? if >= 26.1 {*/
import me.shedaniel.autoconfig.AutoConfigClient;
/*?} else {*/
/*import me.shedaniel.autoconfig.AutoConfig;
*//*?}*/
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        /*? if >= 26.1 {*/
        return parent -> AutoConfigClient.getConfigScreen(ModConfig.class, parent).get();
        /*?} else {*/
        /*return parent -> AutoConfig.getConfigScreen(ModConfig.class, parent).get();
        *//*?}*/
    }
}
//?}
