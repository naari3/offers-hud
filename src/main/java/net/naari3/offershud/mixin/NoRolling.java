package net.naari3.offershud.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
/*? if >= 1.21.11 {*/
import net.minecraft.world.entity.npc.villager.AbstractVillager;
/*?} else {*/
/*import net.minecraft.world.entity.npc.AbstractVillager;
*//*?}*/
import net.naari3.offershud.config.ModConfig;

@Mixin(AbstractVillager.class)
@Environment(EnvType.CLIENT)
public abstract class NoRolling {
    @Inject(at = @At("HEAD"), method = "getUnhappyCounter", cancellable = true)
    public void getUnhappyCounter(CallbackInfoReturnable<Integer> ci) {
        var config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (config.suppressVillagerHeadRolling) {
            ci.setReturnValue(0);
        }
    }
}
