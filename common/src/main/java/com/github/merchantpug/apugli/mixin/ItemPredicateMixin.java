package com.github.merchantpug.apugli.mixin;

import com.github.merchantpug.apugli.powers.ModifyEnchantmentLevelPower;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.predicate.item.ItemPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemPredicate.class)
public class ItemPredicateMixin {
    @Redirect(method = "test", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getEnchantments()Lnet/minecraft/nbt/ListTag;"))
    private ListTag getEnchantmentsForEachEnchantment(ItemStack self) {
        return ModifyEnchantmentLevelPower.getEnchantments(self);
    }
}