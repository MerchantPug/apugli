package com.github.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import com.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;

public class KeyPressedCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if (!Apugli.keysToCheck.containsKey(entity.getUuid())) {
            Apugli.keysToCheck.put(entity.getUuid(), new HashSet<>());
        }
        if (Apugli.keysToCheck.get(entity.getUuid()).stream().noneMatch(key -> key.equals(data.get("key")))) {
            Apugli.keysToCheck.get(entity.getUuid()).add(data.get("key"));
        }
        if (entity instanceof PlayerEntity && Apugli.currentlyUsedKeys.containsKey(entity.getUuid())) {
            return Apugli.currentlyUsedKeys.get(entity.getUuid()).stream().anyMatch(key -> key.equals(data.get("key")));
        }
        return false;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("key_pressed"), new SerializableData()
                .add("key", ApoliDataTypes.KEY),
                KeyPressedCondition::condition
        );
    }
}
