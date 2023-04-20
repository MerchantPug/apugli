package net.merchantpug.apugli.mixin.fabric.common;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.factory.action.ItemActions;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.access.ItemStackAccess;
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
    @Inject(method = "lambda$register$5", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getDamage()I"))
    private static void handleIncreaseDecreaseAction(SerializableData.Instance data, Tuple<Level, ItemStack> worldAndStack, CallbackInfo ci) {
        int amount = data.getInt("amount");
        LivingEntity stackHolder = (LivingEntity) ((ItemStackAccess) (Object) worldAndStack.getB()).getEntity();
        if(amount < 0) {
            PowerHolderComponent.getPowers(stackHolder, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply(worldAndStack.getB())).forEach(ActionOnDurabilityChange::executeIncreaseAction);
        } else {
            PowerHolderComponent.getPowers(stackHolder, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply(worldAndStack.getB())).forEach(ActionOnDurabilityChange::executeDecreaseAction);
        }
    }

    @Inject(method = "lambda$register$3(Lio/github/apace100/calio/data/SerializableData$Instance;Lnet/minecraft/util/Pair;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private static void handleBreakAction(SerializableData.Instance data, Tuple<Level, ItemStack> worldAndStack, CallbackInfo ci) {
        LivingEntity stackHolder = (LivingEntity) ((ItemStackAccess) (Object) worldAndStack.getB()).getEntity();
        PowerHolderComponent.getPowers(stackHolder, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply(worldAndStack.getB())).forEach(ActionOnDurabilityChange::executeBreakAction);
    }
}
