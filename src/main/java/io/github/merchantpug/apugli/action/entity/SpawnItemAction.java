package io.github.merchantpug.apugli.action.entity;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
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
                .add("stack", SerializableDataTypes.ITEM_STACK),
                SpawnItemAction::action
        );
    }
}
