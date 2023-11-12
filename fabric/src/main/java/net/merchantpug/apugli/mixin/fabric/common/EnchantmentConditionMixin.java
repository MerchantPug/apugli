package net.merchantpug.apugli.mixin.fabric.common;

import io.github.apace100.apoli.power.factory.condition.item.EnchantmentCondition;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(EnchantmentCondition.class)
public class EnchantmentConditionMixin {

    /**
     This is a @Redirect as I cannot call the original EnchantmentHelper method within this condition, otherwise it will recurse with {@link net.merchantpug.apugli.power.factory.ModifyEnchantmentLevelPowerFactory}.
     */
    @Redirect(method = "condition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"))
    private static int apugli$useModifiedEnchantmentLevelSum(Enchantment enchantment, ItemStack stack) {
        return ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getItemEnchantmentLevel(enchantment, stack);
    }

    /**
     This is a @Redirect as I cannot call the original EnchantmentHelper method within this condition, otherwise it will recurse with {@link net.merchantpug.apugli.power.factory.ModifyEnchantmentLevelPowerFactory}.
     */
    @Redirect(method = "condition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantments(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;"))
    private static Map<Enchantment, Integer> apugli$useModifiedEnchantmentLevelTotal(ItemStack stack) {
        return ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getItemEnchantments(stack);
    }

}
