<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/HungerManagerMixin.java
package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.access.ItemStackAccess;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
========
package com.github.merchantpug.apugli.mixin.xplatforn.common;

import com.github.merchantpug.apugli.access.ItemStackAccess;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/HungerManagerMixin.java
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class HungerManagerMixin {
    @Shadow
    public abstract void add(int food, float saturationModifier);

    @Inject(method = "eat", at = @At("HEAD"), cancellable = true)
    private void eat(Item item, ItemStack stack, CallbackInfo ci) {
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/HungerManagerMixin.java
        if (((ItemStackAccess)(Object)stack).getItemStackFoodComponent() != null) {
            FoodComponent foodComponent = ((ItemStackAccess)(Object)stack).getItemStackFoodComponent();
            this.add(foodComponent.getHunger(), foodComponent.getSaturationModifier());
========
        if(((ItemStackAccess)(Object)stack).isItemStackFood()) {
            FoodProperties foodComponent = ((ItemStackAccess)(Object)stack).getItemStackFoodComponent();
            this.add(foodComponent.getNutrition(), foodComponent.getSaturationModifier());
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/HungerManagerMixin.java
            ci.cancel();
        }
    }
}