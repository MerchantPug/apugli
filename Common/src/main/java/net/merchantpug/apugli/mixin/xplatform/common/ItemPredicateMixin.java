package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemPredicate.class)
public class ItemPredicateMixin {
    @Redirect(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getEnchantmentTags()Lnet/minecraft/nbt/ListTag;"))
    private ListTag getEnchantmentsForEachEnchantment(ItemStack self) {
        return ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEnchantments(self);
    }
}