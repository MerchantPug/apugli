package net.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.EntityAccess;
import net.minecraft.entity.Entity;

public class MovingCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        return ((EntityAccess)entity).apugli$isMoving();
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("moving"), new SerializableData(),
                MovingCondition::condition
        );
    }
}
