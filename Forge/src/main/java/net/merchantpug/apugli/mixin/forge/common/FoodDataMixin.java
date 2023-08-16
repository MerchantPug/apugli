package net.merchantpug.apugli.mixin.forge.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.merchantpug.apugli.util.CoreUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class FoodDataMixin {
    @Unique
    ItemStack apugli$capturedStack;
    @Unique
    LivingEntity apugli$capturedEntity;

    @Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"), remap = false)
    private void captureEatLocals(Item pItem, ItemStack pStack, LivingEntity entity, CallbackInfo ci) {
        apugli$capturedStack = pStack;
        apugli$capturedEntity = entity;
    }

    // We cannot use a ModifyExpressionValue from MixinExtras here because the original method does not seem to like it when the food is only on Forge's end.
    @ModifyExpressionValue(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isEdible()Z"))
    private boolean isEdibleWithPower(boolean original) {
        if (CoreUtil.doEdibleItemPowersApply(apugli$capturedStack, apugli$capturedEntity)) {
            this.apugli$capturedStack = null;
            this.apugli$capturedEntity = null;
            return true;
        }
        this.apugli$capturedStack = null;
        this.apugli$capturedEntity = null;
        return original;
    }

}
