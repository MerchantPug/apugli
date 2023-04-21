package net.merchantpug.apugli.mixin.fabric.common;

import net.merchantpug.apugli.access.ItemStackAccess;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class HungerManagerMixin {
    @Shadow
    public abstract void eat(int food, float saturationModifier);

    @Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private void eat(Item item, ItemStack stack, CallbackInfo ci) {
        if (((ItemStackAccess)(Object)stack).getItemStackFoodComponent() != null) {
            FoodProperties foodComponent = ((ItemStackAccess)(Object)stack).getItemStackFoodComponent();
            this.eat(foodComponent.getNutrition(), foodComponent.getSaturationModifier());
            ci.cancel();
        }
    }
}