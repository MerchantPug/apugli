package io.github.merchantpug.apugli.action.entity;

import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unchecked")
public class SpawnItemAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        ItemStack stack = data.get("stack");
        entity.dropStack(stack);
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("spawn_item"), new SerializableData()
                .add("stack", SerializableDataType.ITEM_STACK),
                SpawnItemAction::action
        );
    }
}
