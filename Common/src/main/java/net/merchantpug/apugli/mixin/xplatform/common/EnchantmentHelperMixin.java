package net.merchantpug.apugli.mixin.xplatform.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Unique
    private static ItemStack apugli$common$runIterationOnItem;

    @Inject(method = "runIterationOnItem", at = @At(value = "HEAD"))
    private static void getEnchantmentItemStack(EnchantmentHelper.EnchantmentVisitor enchantmentVisitor, ItemStack itemStack, CallbackInfo ci) {
        apugli$common$runIterationOnItem = itemStack;
    }

    @ModifyExpressionValue(method = "runIterationOnItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private static boolean forEachIsEmpty(boolean original) {
        if (Services.PLATFORM.getItemStackLinkedEntity(apugli$common$runIterationOnItem) instanceof LivingEntity living && ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEntityItemEnchants().containsKey(living)) {
            return false;
        }
        return original;
    }

    @Inject(method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("HEAD"), cancellable = true)
    private static void getEnchantmentLevel(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEntityItemEnchants().containsKey(entity.getStringUUID())) {
            int i = 0;
            for (ItemStack stack : entity.getAllSlots()) {
                ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEnchantments(stack, stack.getEnchantmentTags());
                int j = ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getItemEnchantmentLevel(enchantment, stack);
                if (j > i) {
                    i = j;
                }
            }
            cir.setReturnValue(i);
        }
    }

}