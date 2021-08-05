package net.fabricmc.example;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.village.TradeOffer;

public class MerchantInfo {
    private static MerchantInfo info = new MerchantInfo();

    @Nullable
    int id;

    @NotNull
    public List<TradeOffer> offers = new ArrayList<>();

    public static MerchantInfo getInfo() {
        return info;
    }

    public static void clearInfo() {
        info = new MerchantInfo();
    }

    public void setFromMerchantEntity(MerchantEntity merchant) {
        if (this.id != merchant.getId()) {
            this.offers = merchant.getOffers();
            this.id = merchant.getId();
        }
    }
}
