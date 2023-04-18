package com.github.merchantpug.apugli.util;

import com.github.merchantpug.apugli.access.ItemStackAccess;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class ItemStackFoodComponentUtil {

    public static void applyFoodEffects(ItemStack stack, Level world, LivingEntity targetEntity) {
        if(((ItemStackAccess)(Object)stack).isItemStackFood()) {
            List<Pair<MobEffectInstance, Float>> list = ((ItemStackAccess)(Object)stack).getItemStackFoodComponent().getEffects();
            for(Pair<MobEffectInstance, Float> pair : list) {
                if(world.isClientSide || pair.getFirst() == null || !(world.random.nextFloat() < pair.getSecond().floatValue())) continue;
                targetEntity.addEffect(new MobEffectInstance(pair.getFirst()));
            }
        }
    }

    public static void removeStackFood(ItemStack stack) {
        ((ItemStackAccess)(Object)stack).setItemStackFoodComponent(null);
        ((ItemStackAccess)(Object)stack).setFoodUseAction(null);
        ((ItemStackAccess)(Object)stack).setReturnStack(null);
        ((ItemStackAccess)(Object)stack).setStackEatSound(null);
    }

    public static void setStackFood(ItemStack stack, @Nullable FoodProperties component, @Nullable UseAnim action, @Nullable ItemStack returnStack, @Nullable SoundEvent sound) {
        ((ItemStackAccess)(Object)stack).setItemStackFoodComponent(component);
        ((ItemStackAccess)(Object)stack).setFoodUseAction(action);
        ((ItemStackAccess)(Object)stack).setReturnStack(returnStack);
        ((ItemStackAccess)(Object)stack).setStackEatSound(sound);
    }

    public static void setFoodComponent(ItemStack stack, FoodProperties component) {
        ((ItemStackAccess)(Object)stack).setItemStackFoodComponent(component);
    }

    public static void removeFoodComponent(ItemStack stack) {
        ((ItemStackAccess)(Object)stack).setItemStackFoodComponent(null);
    }

    public static void setUseAction(ItemStack stack, UseAnim action) {
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
