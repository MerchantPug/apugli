package io.github.merchantpug.apugli.condition.entity;

import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.ApugliClient;
import net.minecraft.entity.LivingEntity;

public class KeyPressedCondition {
    public static boolean condition(SerializableData.Instance data, LivingEntity entity) {
        if (entity.world.isClient && ApugliClient.keysToCheck.stream().noneMatch(key -> key.equals(data.get("key")))) {
            Apugli.LOGGER.info("key!");
            ApugliClient.keysToCheck.add(data.get("key"));
        }
        return Apugli.currentlyUsedKeys.stream().anyMatch(key -> key.equals(data.get("key")));
    }

    public static ConditionFactory<LivingEntity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("key_pressed"), new SerializableData()
                .add("key", SerializableDataType.KEY),
                KeyPressedCondition::condition
        );
    }
}
