package net.merchantpug.apugli.access;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

import javax.annotation.Nullable;

public interface ItemStackAccess {
    void setEntity(Entity entity);
    Entity getEntity();

    @Nullable
    FoodProperties getItemStackFoodComponent();
    void setItemStackFoodComponent(FoodProperties stackFoodComponent);
}