package com.github.merchantpug.apugli.mixin.xplatforn.common;

import the.great.migration.merchantpug.apugli.power.ModifyEnchantmentLevelPower;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemPredicate.class)
public class ItemPredicateMixin {
    @Redirect(method = "test", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getEnchantments()Lnet/minecraft/nbt/NbtList;"))
    private ListTag getEnchantmentsForEachEnchantment(ItemStack self) {
        return ModifyEnchantmentLevelPower.getEnchantments(self);
    }
}