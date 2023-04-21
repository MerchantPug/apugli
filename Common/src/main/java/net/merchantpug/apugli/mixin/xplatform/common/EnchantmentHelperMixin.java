package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Redirect(method = "runIterationOnItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private static boolean forEachIsEmpty(ItemStack self) {
        return false;
    }

    @Redirect(method = "runIterationOnItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getEnchantmentTags()Lnet/minecraft/nbt/ListTag;"))
    private static ListTag getEnchantmentsForEachEnchantment(ItemStack self) {
        return ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEnchantments(self);
    }

    @Redirect(method = "getItemEnchantmentLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private static boolean getLevelIsEmpty(ItemStack instance) {
        return false;
    }

    @Redirect(method = "getItemEnchantmentLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getEnchantmentTags()Lnet/minecraft/nbt/ListTag;"))
    private static ListTag getEnchantmentsGetLevel(ItemStack self) {
        return ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEnchantments(self);
    }

    @Inject(method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("RETURN"), cancellable = true)
    private static void getEquipmentLevel(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        int originalReturn = cir.getReturnValue();
        int newEnchantLevel = (int) Services.PLATFORM.applyModifiers(entity, ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get(), originalReturn, p -> ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().doesApply(p, entity, enchantment));
        cir.setReturnValue(newEnchantLevel);
    }

}