package net.naari3.offershud.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.naari3.offershud.OffersHUD;
import net.naari3.offershud.MerchantInfo;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
abstract class ReceiveTradeOfferPacket {
    @Inject(at = @At("HEAD"), method = "onSetTradeOffers", cancellable = true)
    public void onSetTradeOffers(SetTradeOffersS2CPacket packet, CallbackInfo ci) {
        var offers = packet.getOffers();
        MerchantInfo.getInfo().setOffers(offers);
        if (!OffersHUD.getOpenWindow()) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "onOpenScreen", cancellable = true)
    public void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        var type = packet.getScreenHandlerType();

        /*? if >= 1.21.5 {*/
        if (!OffersHUD.getOpenWindow() && type == ScreenHandlerType.MERCHANT) {
            ci.cancel();
            ClientPlayNetworking.getSender()
                    .sendPacket(new CloseHandledScreenC2SPacket(packet.getSyncId()));
        }
        /*?} else {*/
        /*var instance = type.create(Integer.MAX_VALUE, new PlayerInventory(MinecraftClient.getInstance().player));

        if (!OffersHUD.getOpenWindow() && instance instanceof MerchantScreenHandler) {
            ci.cancel();
            ClientPlayNetworking.getSender()
                    .sendPacket(new CloseHandledScreenC2SPacket(packet.getSyncId()));
        }
        *//*?}*/
    }
}
