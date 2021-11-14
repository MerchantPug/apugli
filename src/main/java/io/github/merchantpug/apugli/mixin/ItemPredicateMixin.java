package io.github.merchantpug.apugli.mixin;

import io.github.merchantpug.apugli.power.ModifyEnchantmentLevelPower;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.predicate.item.ItemPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemPredicate.class)
public class ItemPredicateMixin {
    @Redirect(method = "test", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getEnchantments()Lnet/minecraft/nbt/NbtList;"))
    private NbtList getEnchantmentsForEachEnchantment(ItemStack self) {
        return ModifyEnchantmentLevelPower.getEnchantments(self);
    }
}