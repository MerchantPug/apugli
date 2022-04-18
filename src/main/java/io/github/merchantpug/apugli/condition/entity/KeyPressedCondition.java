package io.github.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.ApugliClient;
import net.minecraft.entity.Entity;

public class KeyPressedCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if (entity.world.isClient && ApugliClient.keysToCheck.stream().noneMatch(key -> key.equals(data.get("key")))) {
            ApugliClient.keysToCheck.add(data.get("key"));
        }
        return Apugli.currentlyUsedKeys.stream().anyMatch(key -> key.equals(data.get("key")));
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("key_pressed"), new SerializableData()
                .add("key", ApoliDataTypes.KEY),
                KeyPressedCondition::condition
        );
    }
}
