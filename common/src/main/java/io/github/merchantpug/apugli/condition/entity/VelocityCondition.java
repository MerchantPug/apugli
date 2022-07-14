package io.github.merchantpug.apugli.condition.entity;

import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.LivingEntity;

public class VelocityCondition {
    public static boolean condition(SerializableData.Instance data, LivingEntity entity) {
        Comparison comparison = data.get("comparison");
        boolean doesXMatch = !data.isPresent("x") || comparison.compare(data.getFloat("x"), entity.getVelocity().x);
        boolean doesYMatch = !data.isPresent("y") || comparison.compare(data.getFloat("y"), entity.getVelocity().y);
        boolean doesZMatch = !data.isPresent("z") || comparison.compare(data.getFloat("z"), entity.getVelocity().z);
        return doesXMatch && doesYMatch && doesZMatch;
    }

    public static ConditionFactory<LivingEntity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("velocity"), new SerializableData()
                .add("x", SerializableDataType.FLOAT, null)
                .add("y", SerializableDataType.FLOAT, null)
                .add("z", SerializableDataType.FLOAT, null)
                .add("comparison", SerializableDataType.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
                VelocityCondition::condition
        );
    }
}
