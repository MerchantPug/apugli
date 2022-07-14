package com.github.merchantpug.apugli.mixin;

import com.github.merchantpug.apugli.access.ItemStackAccess;
import io.github.apace100.origins.power.factory.condition.ItemConditions;
import io.github.apace100.origins.util.SerializableData;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemConditions.class)
public class ItemConditionsMixin {
    @Inject(method = "lambda$register$10", at = @At("HEAD"), remap = false, cancellable = true)
    private static void isNibblesMeat(SerializableData.Instance data, ItemStack stack, CallbackInfoReturnable cir) {
        if (((ItemStackAccess)(Object)stack).isItemStackFood()) {
            cir.setReturnValue(((ItemStackAccess)(Object)stack).getItemStackFoodComponent().isMeat());
        }
    }
}