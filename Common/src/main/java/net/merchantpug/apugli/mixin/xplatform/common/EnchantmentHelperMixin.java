package net.merchantpug.apugli.mixin.xplatform.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        if (apugli$common$runIterationOnItem != null && Services.PLATFORM.getEntityFromItemStack(apugli$common$runIterationOnItem) instanceof LivingEntity living && ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEntityItemEnchants().containsKey(living)) {
            apugli$common$runIterationOnItem = null;
            return false;
        }
        apugli$common$runIterationOnItem = null;
        return original;
    }

}