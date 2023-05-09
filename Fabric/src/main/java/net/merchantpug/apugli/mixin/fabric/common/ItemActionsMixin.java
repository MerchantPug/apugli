package net.merchantpug.apugli.mixin.fabric.common;

import io.github.apace100.apoli.power.factory.action.ItemActions;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemActions.class)
public class ItemActionsMixin {
    @Inject(method = "lambda$register$3", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private static void handleBreakAction(SerializableData.Instance data, Tuple<Level, ItemStack> worldAndStack, CallbackInfo ci) {
        LivingEntity stackHolder = (LivingEntity) ((ItemStackAccess) (Object) worldAndStack.getB()).getEntity();
        Services.POWER.getPowers(stackHolder, ApugliPowers.ACTION_ON_DURABILITY_CHANGE.get()).stream().filter(p -> p.doesApply(worldAndStack.getB())).forEach(p -> p.executeBreakAction(worldAndStack.getB()));
    }
}