package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class WearableItemStackPower extends Power {
    private final ItemStack itemStack;
    private final float scale;

    public WearableItemStackPower(PowerType<?> type, LivingEntity entity, ItemStack itemStack, float scale) {
        super(type, entity);
        this.itemStack = itemStack;
        this.scale = scale;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public float getScale() {
        return scale;
    }
}
