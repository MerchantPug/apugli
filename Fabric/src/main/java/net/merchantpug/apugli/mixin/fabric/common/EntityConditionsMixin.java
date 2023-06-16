package net.merchantpug.apugli.mixin.fabric.common;

import io.github.apace100.apoli.power.factory.condition.EntityConditions;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityConditions.class)
public class EntityConditionsMixin {

    /**
     This is a @Redirect as I cannot call the original EnchantmentHelper method within this condition, otherwise it will recurse with {@link net.merchantpug.apugli.power.factory.ModifyEnchantmentLevelPowerFactory}.
     */
    @Redirect(method = "lambda$register$51", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"))
    private static int useModifiedEnchantmentLevelSum(Enchantment enchantment, ItemStack stack) {
        return ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getItemEnchantmentLevel(enchantment, stack);
    }

    /**
    This is a @Redirect as I cannot call the original EnchantmentHelper method within this condition, otherwise it will recurse with {@link net.merchantpug.apugli.power.factory.ModifyEnchantmentLevelPowerFactory}.
     */
    @Redirect(method = "lambda$register$51", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I"))
    private static int useModifiedEnchantmentLevelTotal(Enchantment enchantment, LivingEntity entity) {
        return ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEnchantmentLevel(enchantment, entity);
    }

}
