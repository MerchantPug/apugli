package io.github.merchantpug.apugli.util;

import com.mojang.datafixers.util.Pair;
import io.github.merchantpug.apugli.access.ItemStackAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemStackFoodComponentUtil {

    public static void applyFoodEffects(ItemStack stack, World world, LivingEntity targetEntity) {
        if (((ItemStackAccess)(Object)stack).isItemStackFood()) {
            List<Pair<StatusEffectInstance, Float>> list = ((ItemStackAccess)(Object)stack).getItemStackFoodComponent().getStatusEffects();
            for (Pair<StatusEffectInstance, Float> pair : list) {
                if (world.isClient || pair.getFirst() == null || !(world.random.nextFloat() < pair.getSecond().floatValue())) continue;
                targetEntity.addStatusEffect(new StatusEffectInstance(pair.getFirst()));
            }
        }
    }

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
