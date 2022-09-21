package net.merchantpug.apugli.action.entity;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
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
                .add("value", SerializableDataTypes.BOOLEAN, null),
                SetNoGravityAction::action
        );
    }
}
