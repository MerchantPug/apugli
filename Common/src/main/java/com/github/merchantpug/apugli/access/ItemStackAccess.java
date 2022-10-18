package com.github.merchantpug.apugli.access;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public interface ItemStackAccess {
    void setEntity(Entity entity);
    Entity getEntity();

    boolean isItemStackFood();

    FoodProperties getItemStackFoodComponent();

    void setItemStackFoodComponent(FoodProperties stackFoodComponent);

    UseAnim getFoodUseAction();

    void setFoodUseAction(UseAnim useAction);

    ItemStack getReturnStack();

    void setReturnStack(ItemStack stack);

    SoundEvent getStackEatSound();

    void setStackEatSound(SoundEvent sound);
}