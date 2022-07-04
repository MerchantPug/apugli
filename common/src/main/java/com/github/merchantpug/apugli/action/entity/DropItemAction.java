package com.github.merchantpug.apugli.action.entity;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@SuppressWarnings("unchecked")
public class DropItemAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        if (!(entity instanceof LivingEntity)) return;
        ConditionFactory<ItemStack>.Instance condition = data.get("item_condition");
        ItemStack equippedItem = ((LivingEntity) entity).getEquippedStack(data.get("equipment_slot"));
        if (!equippedItem.isEmpty() && condition.test(equippedItem)) {
            entity.dropStack(equippedItem, entity.getEyeHeight(entity.getPose()));
            entity.equipStack(data.get("equipment_slot"), ItemStack.EMPTY);
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("drop_item"), new SerializableData()
                .add("hand", SerializableDataType.enumValue(Hand.class)),
                DropItemAction::action
        );
    }
}
