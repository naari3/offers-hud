package net.naari3.offershud.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.naari3.offershud.OffersHUD;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
abstract class CloseScreen {
    @Inject(at = @At("HEAD"), method = "closeHandledScreen")
    public void closeHandledScreen(CallbackInfo ci) {
        OffersHUD.setOpenWindow(false);
    }
}
