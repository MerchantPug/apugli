package net.merchantpug.apugli.mixin.forge.common;

import net.merchantpug.apugli.util.CoreUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class FoodDataMixin {
    ItemStack apugli$capturedStack;
    LivingEntity apugli$capturedEntity;

    @Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"), remap = false)
    private void captureEatLocals(Item pItem, ItemStack pStack, LivingEntity entity, CallbackInfo ci) {
        apugli$capturedStack = pStack;
        apugli$capturedEntity = entity;
    }

    // We cannot use a ModifyExpressionValue from MixinExtras here because that does not seem to like non remapped methods.
    @Redirect(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isEdible()Z"), remap = false)
    private boolean isEdibleWithPower(Item instance) {
        return instance.isEdible() || CoreUtil.doEdibleItemPowersApply(apugli$capturedStack, apugli$capturedEntity);
    }

}
