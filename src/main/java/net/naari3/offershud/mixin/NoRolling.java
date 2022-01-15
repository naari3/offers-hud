package net.naari3.offershud.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.MerchantEntity;
import net.naari3.offershud.OffersHUD;
import net.naari3.offershud.config.ModConfig;

@Mixin(MerchantEntity.class)
@Environment(EnvType.CLIENT)
public abstract class NoRolling {
    @Inject(at = @At("HEAD"), method = "getHeadRollingTimeLeft", cancellable = true)
    public void getHeadRollingTimeLeft(CallbackInfoReturnable<Integer> ci) {
        var config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (config.suppressVillagerHeadRolling) {
            ci.setReturnValue(0);
        }
    }
}
