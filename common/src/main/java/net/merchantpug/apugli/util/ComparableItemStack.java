package net.merchantpug.apugli.util;

import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public record ComparableItemStack(ItemStack stack) {

    public boolean equals(Object other) {
        if (!(other instanceof ComparableItemStack comparable)) {
           return false;
        }
        return ItemStack.matches(this.stack(), comparable.stack());
    }

    public int hashCode() {
        return Objects.hash(this.stack().getItem(), this.stack().getCount(), this.stack().getTag());
    }

}
