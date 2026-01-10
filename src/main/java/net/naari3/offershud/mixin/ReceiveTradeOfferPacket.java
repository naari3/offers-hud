package net.naari3.offershud.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.naari3.offershud.OffersHUD;
import net.naari3.offershud.MerchantInfo;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;

@Mixin(ClientPacketListener.class)
abstract class ReceiveTradeOfferPacket {
    @Inject(at = @At("HEAD"), method = "handleMerchantOffers", cancellable = true)
    public void handleMerchantOffers(ClientboundMerchantOffersPacket packet, CallbackInfo ci) {
        var offers = packet.getOffers();
        MerchantInfo.getInfo().setOffers(offers);
    }

    @Inject(at = @At("HEAD"), method = "handleOpenScreen", cancellable = true)
    public void handleOpenScreen(ClientboundOpenScreenPacket packet, CallbackInfo ci) {
        var type = packet.getType();

        /*? if >= 1.21.5 {*/
        if (!OffersHUD.getOpenWindow() && type == MenuType.MERCHANT) {
            ci.cancel();
            ClientPlayNetworking.getSender()
                    .sendPacket(new ServerboundContainerClosePacket(packet.getContainerId()));
        }
        /*?} else {*/
        /*var instance = type.create(Integer.MAX_VALUE, new Inventory(Minecraft.getInstance().player));

        if (!OffersHUD.getOpenWindow() && instance instanceof MerchantMenu) {
            ci.cancel();
            ClientPlayNetworking.getSender()
                    .sendPacket(new ServerboundContainerClosePacket(packet.getContainerId()));
        }
        *//*?}*/
    }
}
