package io.github.merchantpug.apugli.action.entity;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unchecked")
public class DropItemAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        if (!(entity instanceof LivingEntity)) return;
        ConditionFactory<ItemStack>.Instance condition = (ConditionFactory<ItemStack>.Instance) data.get("item_condition");
        ItemStack equippedItem = ((LivingEntity) entity).getEquippedStack((EquipmentSlot) data.get("equipment_slot"));
        if (!equippedItem.isEmpty() && condition.test(equippedItem)) {
            entity.dropStack(equippedItem, entity.getEyeHeight(entity.getPose()));
            entity.equipStack((EquipmentSlot) data.get("equipment_slot"), ItemStack.EMPTY);
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("drop_item"), new SerializableData()
                .add("hand", SerializableDataTypes.HAND),
                SwingHandAction::action
        );
    }
}
