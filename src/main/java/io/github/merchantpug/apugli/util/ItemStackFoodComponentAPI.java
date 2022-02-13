package io.github.merchantpug.apugli.util;

import io.github.merchantpug.apugli.access.ItemStackAccess;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.UseAction;
import org.jetbrains.annotations.Nullable;

public class ItemStackFoodComponentAPI {

    public static void removeStackFood(ItemStack stack) {
        ((ItemStackAccess)(Object)stack).setItemStackFoodComponent(null);
        ((ItemStackAccess)(Object)stack).setFoodUseAction(null);
        ((ItemStackAccess)(Object)stack).setReturnStack(null);
        ((ItemStackAccess)(Object)stack).setStackEatSound(null);
    }

    public static void setStackFood(ItemStack stack, @Nullable FoodComponent component, @Nullable UseAction action, @Nullable ItemStack returnStack, @Nullable SoundEvent sound) {
        ((ItemStackAccess)(Object)stack).setItemStackFoodComponent(component);
        ((ItemStackAccess)(Object)stack).setFoodUseAction(action);
        ((ItemStackAccess)(Object)stack).setReturnStack(returnStack);
        ((ItemStackAccess)(Object)stack).setStackEatSound(sound);
    }

    public static void setFoodComponent(ItemStack stack, FoodComponent component) {
        ((ItemStackAccess)(Object)stack).setItemStackFoodComponent(component);
    }

    public static void removeFoodComponent(ItemStack stack) {
        ((ItemStackAccess)(Object)stack).setItemStackFoodComponent(null);
    }

    public static void setUseAction(ItemStack stack, UseAction action) {
        ((ItemStackAccess)(Object)stack).setFoodUseAction(action);
    }

    public static void removeUseAction(ItemStack stack) {
        ((ItemStackAccess)(Object)stack).setFoodUseAction(null);
    }

    public static void setReturnStack(ItemStack stack, ItemStack returnStack) {
        ((ItemStackAccess)(Object)stack).setReturnStack(returnStack);
    }

    public static void removeReturnStack(ItemStack stack) {
        ((ItemStackAccess)(Object)stack).setReturnStack(null);
    }
}
