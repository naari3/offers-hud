package net.fabricmc.example;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class OfferFormatter {
    private ItemStack firstBuy;
    @Nullable
    private ItemStack secondBuy;
    private ItemStack sell;

    public OfferFormatter(ItemStack firstBuy, @Nullable ItemStack secondBuy, ItemStack sell) {
        this.firstBuy = firstBuy;
        this.secondBuy = secondBuy;
        this.sell = sell;
    }

    private String makeInfoString(ItemStack item) {
        if (item.getCount() <= 1) {
            return String.format("%s", item.getName().getString());
        } else {
            return String.format("%s x %s", item.getName().getString(), item.getCount());
        }
    }

    @Override
    public String toString() {
        if (this.secondBuy == null || this.secondBuy.isOf(Items.AIR)) {
            return String.format("%s -> %s", this.makeInfoString(this.firstBuy), this.makeInfoString(this.sell));
        } else {
            return String.format("%s + %s -> %s", this.makeInfoString(this.firstBuy),
                    this.makeInfoString(this.secondBuy), this.makeInfoString(this.sell));
        }
    }
}
