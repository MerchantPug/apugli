package net.merchantpug.apugli.mixin.fabric.common;

import io.github.apace100.apoli.power.factory.condition.ItemConditions;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemConditions.class)
public class ItemConditionsMixin {

    @Inject(method = "lambda$register$10", at = @At("HEAD"), cancellable = true)
    private static void apugli$isPowerFoodMeat(SerializableData.Instance data, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (((ItemStackAccess)(Object)stack).apugli$getEntity() instanceof LivingEntity living && Services.POWER.hasPower(living, ApugliPowers.EDIBLE_ITEM.get()) &&
            Services.POWER.getPowers(living, ApugliPowers.EDIBLE_ITEM.get()).stream().anyMatch(power -> power.doesApply(living.level(), stack) && power.getFoodComponent().isMeat()))
            cir.setReturnValue(true);
    }

}
