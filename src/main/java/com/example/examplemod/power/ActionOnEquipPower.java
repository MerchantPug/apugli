package com.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnEquipPower extends Power {
    public final EquipmentSlot slot;
    @Nullable private final Predicate<ItemStack> itemCondition;
    private final Consumer<Entity> entityAction;

    public ActionOnEquipPower(PowerType<?> type, LivingEntity entity, EquipmentSlot slot, @Nullable Predicate<ItemStack> itemCondition, Consumer<Entity> entityAction) {
        super(type, entity);
        this.slot = slot;
        this.itemCondition = itemCondition;
        this.entityAction = entityAction;
    }

    public void fireAction(EquipmentSlot slot, ItemStack stack) {
        if(this.slot == slot && (itemCondition == null || itemCondition.test(stack))) {
            entityAction.accept(this.entity);
        }
    }
}