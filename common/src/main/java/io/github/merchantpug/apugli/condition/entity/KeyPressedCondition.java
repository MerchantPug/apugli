package io.github.merchantpug.apugli.condition.entity;

import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.ApugliClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class KeyPressedCondition {
    public static boolean condition(SerializableData.Instance data, LivingEntity entity) {
        if (entity.world.isClient && ApugliClient.keysToCheck.stream().noneMatch(key -> key.equals(data.get("key")))) {
            ApugliClient.keysToCheck.add(data.get("key"));
        }
        if (entity instanceof PlayerEntity && Apugli.currentlyUsedKeys.containsKey(entity)) {
            return Apugli.currentlyUsedKeys.get(entity).stream().anyMatch(key -> key.equals(data.get("key")));
        }
        return false;
    }

    public static ConditionFactory<LivingEntity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("key_pressed"), new SerializableData()
                .add("key", SerializableDataType.KEY),
                KeyPressedCondition::condition
        );
    }
}
