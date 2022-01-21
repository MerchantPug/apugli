package io.github.merchantpug.apugli.mixin;

import io.github.merchantpug.apugli.access.ItemStackAccess;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
    @Shadow public abstract void add(int food, float saturationModifier);

    @Inject(method = "eat", at = @At("HEAD"), cancellable = true)
    private void eat(Item item, ItemStack stack, CallbackInfo ci) {
        if (((ItemStackAccess)(Object)stack).isItemStackFood()) {
            FoodComponent foodComponent = ((ItemStackAccess)(Object)stack).getItemStackFoodComponent();
            this.add(foodComponent.getHunger(), foodComponent.getSaturationModifier());
            if (item.isFood()) {
                ci.cancel();
            }
        }
    }
}
