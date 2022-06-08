package com.github.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import com.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;

public class VelocityCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        Comparison comparison = data.get("comparison");
        boolean doesXMatch = !data.isPresent("x") || comparison.compare(data.getFloat("x"), entity.getVelocity().x);
        boolean doesYMatch = !data.isPresent("y") || comparison.compare(data.getFloat("y"), entity.getVelocity().y);
        boolean doesZMatch = !data.isPresent("z") || comparison.compare(data.getFloat("z"), entity.getVelocity().z);
        return doesXMatch && doesYMatch && doesZMatch;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("velocity"), new SerializableData()
                .add("x", SerializableDataTypes.FLOAT, null)
                .add("y", SerializableDataTypes.FLOAT, null)
                .add("z", SerializableDataTypes.FLOAT, null)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
                VelocityCondition::condition
        );
    }
}
