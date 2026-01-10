package net.naari3.offershud.mixin;

import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.naari3.offershud.MerchantInfo;
import net.naari3.offershud.OffersHUD;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;

@Mixin(MultiPlayerGameMode.class)
abstract class ValidTrade {

    // MultiPlayerGameMode
    @Inject(at = @At("HEAD"), method = "interact")
    public void interact(Player player, Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> ci) {
        if (!(entity instanceof Merchant)) {
            return;
        }

        // TODO: Maybe we don't need it anymore.
        // Original code was:
        // var merchant = (MerchantEntity) entity;
        // if (merchant.getOffers().isEmpty()) {
        //     return;
        // }
        // but, From 1.21, merchant.getOffers() is patched that makes it always throw `IllegalStateException` on Client side.
        // I guess that originally it was a method that you didn't want to call on the client side.
        // If this is correct, then this code is meaningless.
        var info = MerchantInfo.getInfo();
        info.getLastId().ifPresent(id -> {
            if (entity.getId() == id && !info.getOffers().isEmpty()) {
                OffersHUD.setOpenWindow(true);
            }
        });
    }
}
