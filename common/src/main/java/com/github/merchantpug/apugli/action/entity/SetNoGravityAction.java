package com.github.merchantpug.apugli.action.entity;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import net.minecraft.entity.Entity;

@SuppressWarnings("unchecked")
public class SetNoGravityAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        if (!data.isPresent("value")) {
            entity.setNoGravity(!entity.hasNoGravity());
        } else {
            entity.setNoGravity(data.getBoolean("value"));
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("set_no_gravity"), new SerializableData()
                .add("value", SerializableDataType.BOOLEAN, null),
                SetNoGravityAction::action
        );
    }
}
