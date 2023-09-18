package net.merchantpug.apugli.mixin.forge.common;

import io.github.edwinmindcraft.apoli.common.condition.configuration.EnchantmentConfiguration;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(EnchantmentConfiguration.class)
public class EnchantmentConfigurationMixin {

    @Redirect(method = "lambda$applyCheck$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantments(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;"))
    private static Map<Enchantment, Integer> useModifiedEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.deserializeEnchantments(ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEnchantments(stack, stack.getEnchantmentTags()));
    }

    @Redirect(method = "lambda$applyCheck$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"))
    private int useModifiedEnchantmentLevel(Enchantment enchantment, ItemStack stack) {
        return ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getItemEnchantmentLevel(enchantment, stack);
    }

}
