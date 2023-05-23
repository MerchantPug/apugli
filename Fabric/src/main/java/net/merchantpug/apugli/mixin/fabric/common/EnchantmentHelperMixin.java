package net.merchantpug.apugli.mixin.fabric.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    private static ItemStack apugli$runIterationOnItem;

    @Inject(method = "runIterationOnItem", at = @At(value = "HEAD"))
    private static void getEnchantmentItemStack(EnchantmentHelper.EnchantmentVisitor enchantmentVisitor, ItemStack itemStack, CallbackInfo ci) {
        apugli$runIterationOnItem = itemStack;
    }

    @ModifyVariable(method = "runIterationOnItem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ItemStack;getEnchantmentTags()Lnet/minecraft/nbt/ListTag;"))
    private static ListTag getEnchantmentsForEachEnchantment(ListTag original) {
        return ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEnchantments(apugli$runIterationOnItem, original);
    }

    private static ItemStack apugli$itemEnchantmentLevelStack;

    @Inject(method = "getItemEnchantmentLevel", at = @At("HEAD"))
    private static void getEnchantmentItemStack(Enchantment enchantment, ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
        apugli$itemEnchantmentLevelStack = itemStack;
    }

    @ModifyExpressionValue(method = "getItemEnchantmentLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private static boolean getLevelIsEmpty(boolean original) {
        if (((ItemStackAccess) (Object) apugli$itemEnchantmentLevelStack).getEntity() instanceof LivingEntity living && ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEntityItemEnchants().containsKey(living)) {
            return false;
        }
        return original;
    }

    @ModifyVariable(method = "getItemEnchantmentLevel", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ItemStack;getEnchantmentTags()Lnet/minecraft/nbt/ListTag;"))
    private static ListTag getEnchantmentsGetLevel(ListTag original) {
        return ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEnchantments(apugli$itemEnchantmentLevelStack, original);
    }

}
