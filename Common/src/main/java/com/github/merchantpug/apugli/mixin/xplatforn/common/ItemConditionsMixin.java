<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/ItemConditionsMixin.java
package net.merchantpug.apugli.mixin;
========
package com.github.merchantpug.apugli.mixin.xplatforn.common;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/ItemConditionsMixin.java

import net.merchantpug.apugli.access.ItemStackAccess;
import io.github.apace100.apoli.power.factory.condition.ItemConditions;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemConditions.class)
public class ItemConditionsMixin {
    @Inject(method = "lambda$register$10", at = @At("HEAD"), remap = false, cancellable = true)
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/ItemConditionsMixin.java
    private static void isPowerFoodMeat(SerializableData.Instance data, ItemStack stack, CallbackInfoReturnable cir) {
        if (((ItemStackAccess)(Object)stack).getItemStackFoodComponent() != null) {
========
    private static void isNibblesMeat(SerializableData.Instance data, ItemStack stack, CallbackInfoReturnable cir) {
        if(((ItemStackAccess)(Object)stack).isItemStackFood()) {
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/ItemConditionsMixin.java
            cir.setReturnValue(((ItemStackAccess)(Object)stack).getItemStackFoodComponent().isMeat());
        }
    }
}
