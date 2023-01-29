package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.power.ActionOnDurabilityChange;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.factory.action.ItemActions;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemActions.class)
public class ItemActionsMixin {
    @Inject(method = "lambda$register$3(Lio/github/apace100/calio/data/SerializableData$Instance;Lnet/minecraft/util/Pair;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getDamage()I"))
    private static void handleIncreaseDecreaseAction(SerializableData.Instance data, Pair<World, ItemStack> worldAndStack, CallbackInfo ci) {
        int amount = data.getInt("amount");
        LivingEntity stackHolder = (LivingEntity) ((ItemStackAccess) (Object) worldAndStack.getRight()).getEntity();
        if (amount < 0) {
            PowerHolderComponent.getPowers(stackHolder, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply(worldAndStack.getRight())).forEach(ActionOnDurabilityChange::executeIncreaseAction);
        } else {
            PowerHolderComponent.getPowers(stackHolder, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply(worldAndStack.getRight())).forEach(ActionOnDurabilityChange::executeDecreaseAction);
        }
    }

    @Inject(method = "lambda$register$3(Lio/github/apace100/calio/data/SerializableData$Instance;Lnet/minecraft/util/Pair;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private static void handleBreakAction(SerializableData.Instance data, Pair<World, ItemStack> worldAndStack, CallbackInfo ci) {
        LivingEntity stackHolder = (LivingEntity) ((ItemStackAccess) (Object) worldAndStack.getRight()).getEntity();
        PowerHolderComponent.getPowers(stackHolder, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply(worldAndStack.getRight())).forEach(ActionOnDurabilityChange::executeBreakAction);
    }
}
