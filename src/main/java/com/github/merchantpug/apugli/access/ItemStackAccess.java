package com.github.merchantpug.apugli.access;

import net.minecraft.entity.Entity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.UseAction;

public interface ItemStackAccess {
    void setEntity(Entity entity);
    Entity getEntity();

    boolean isItemStackFood();

    FoodComponent getItemStackFoodComponent();

    void setItemStackFoodComponent(FoodComponent stackFoodComponent);

    UseAction getFoodUseAction();

    void setFoodUseAction(UseAction useAction);

    ItemStack getReturnStack();

    void setReturnStack(ItemStack stack);

    SoundEvent getStackEatSound();

    void setStackEatSound(SoundEvent sound);
}