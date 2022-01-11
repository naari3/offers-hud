package net.naari3.offershud.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.naari3.offershud.OffersHUD;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Mixin(ClientPlayerInteractionManager.class)
abstract class ValidTrade {

    // ClientPlayerInteractionManager
    @Inject(at = @At("HEAD"), method = "interactEntity")
    public void interactEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> ci) {
        if (!(entity instanceof MerchantEntity)) {
            return;
        }

        var merchant = (MerchantEntity) entity;
        if (merchant.getOffers().isEmpty()) {
            return;
        }
        OffersHUD.setOpenWindow(true);
    }
}
