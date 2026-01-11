package net.naari3.offershud;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.trading.MerchantOffer;

public class MerchantInfo {
    private static MerchantInfo info = new MerchantInfo();

    @Nullable
    private Integer lastId;

    @NotNull
    private List<MerchantOffer> offers = new ArrayList<>();

    public static MerchantInfo getInfo() {
        return info;
    }

    public List<MerchantOffer> getOffers() {
        return this.offers;
    }

    public void setOffers(List<MerchantOffer> offerlist) {
        this.offers = offerlist;
    }

    public Optional<Integer> getLastId() {
        return Optional.ofNullable(this.lastId);
    }

    public void setLastId(Integer id) {
        this.lastId = id;
    }
}
