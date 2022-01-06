package net.naari3.villagertradinglist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

public class MerchantInfo {
    private static MerchantInfo info = new MerchantInfo();

    @Nullable
    private Integer lastId;

    @NotNull
    public List<TradeOffer> offers = new ArrayList<>();

    public static MerchantInfo getInfo() {
        return info;
    }

    public void setOffers(TradeOfferList offerlist) {
        this.offers = offerlist;
    }

    public Optional<Integer> getLastId() {
        return Optional.ofNullable(this.lastId);
    }

    public void setLastId(Integer id) {
        this.lastId = id;
    }
}
