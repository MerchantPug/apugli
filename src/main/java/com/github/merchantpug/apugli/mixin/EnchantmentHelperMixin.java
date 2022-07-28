package com.github.merchantpug.apugli.mixin;

import com.github.merchantpug.apugli.power.ModifyEnchantmentLevelPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @Redirect(method = "forEachEnchantment(Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private static boolean forEachIsEmpty(ItemStack self) {
        return false;
    }

    @Redirect(method = "forEachEnchantment(Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getEnchantments()Lnet/minecraft/nbt/NbtList;"))
    private static NbtList getEnchantmentsForEachEnchantment(ItemStack self) {
        return ModifyEnchantmentLevelPower.getEnchantments(self);
    }

    @Redirect(method = "getLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private static boolean getLevelIsEmpty(ItemStack instance) {
        return false;
    }

    @Redirect(method = "getLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getEnchantments()Lnet/minecraft/nbt/NbtList;"))
    private static NbtList getEnchantmentsGetLevel(ItemStack self) {
        return ModifyEnchantmentLevelPower.getEnchantments(self);
    }

    @Inject(method = "getEquipmentLevel", at = @At("RETURN"), cancellable = true)
    private static void getEquipmentLevel(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        int originalReturn = cir.getReturnValue();
        int newEnchantLevel = (int) PowerHolderComponent.modify(entity, ModifyEnchantmentLevelPower.class, originalReturn, power -> power.doesApply(enchantment));
        cir.setReturnValue(newEnchantLevel);
    }
}