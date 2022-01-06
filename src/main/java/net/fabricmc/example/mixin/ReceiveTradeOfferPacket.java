package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.MerchantInfo;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.screen.ScreenHandlerType;

@Mixin(ClientPlayNetworkHandler.class)
abstract class ReceiveTradeOfferPacket {
    @Inject(at = @At("HEAD"), method = "onSetTradeOffers", cancellable = true)
    public void onSetTradeOffers(SetTradeOffersS2CPacket packet, CallbackInfo ci) {
        MerchantInfo.getInfo().setOffers(packet.getOffers());
        if (!ExampleMod.getOpenWindow()) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "onOpenScreen", cancellable = true)
    public void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        var type = packet.getScreenHandlerType();

        if (!ExampleMod.getOpenWindow() && type == ScreenHandlerType.MERCHANT) {
            ci.cancel();
            ClientPlayNetworking.getSender()
                    .sendPacket(new CloseHandledScreenC2SPacket(packet.getSyncId()));
        }
    }
}
