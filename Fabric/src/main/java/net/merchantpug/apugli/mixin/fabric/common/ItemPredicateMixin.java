package net.merchantpug.apugli.mixin.fabric.common;

import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ItemPredicate.class)
public class ItemPredicateMixin {
    @Unique
    private ItemStack apugli$itemStack;

    @Inject(method = "matches", at = @At("HEAD"))
    private void captureItemStack(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        apugli$itemStack = itemStack;
    }

    @ModifyArg(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;deserializeEnchantments(Lnet/minecraft/nbt/ListTag;)Ljava/util/Map;"))
    private ListTag getEnchantmentsForEachEnchantment(ListTag original) {
        return ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEnchantments(apugli$itemStack, original);
    }

}