package net.naari3.offershud.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.naari3.offershud.config.ModConfig;

@Mixin(Entity.class)
@Environment(EnvType.CLIENT)
public abstract class NoRollingSound {
    @Inject(at = @At("HEAD"), method = "playSound", cancellable = true)
    public void playSound(SoundEvent sound, float volume, float pitch, CallbackInfo ci) {
        var config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (config.suppressVillagerHeadRolling && SoundEvents.VILLAGER_NO == sound) {
            ci.cancel();
        }
    }
}
