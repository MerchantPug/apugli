package net.merchantpug.apugli.access;

import net.minecraft.entity.Entity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.UseAction;

import javax.annotation.Nullable;

public interface ItemStackAccess {
    void setEntity(Entity entity);
    Entity getEntity();

    @Nullable FoodComponent getItemStackFoodComponent();
    void setItemStackFoodComponent(FoodComponent stackFoodComponent);
}