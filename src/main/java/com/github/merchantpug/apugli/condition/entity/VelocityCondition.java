package com.github.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import com.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;

import java.util.EnumSet;

public class VelocityCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        Comparison comparison = data.get("comparison");
        EnumSet<Direction.Axis> axes = data.get("axes");

        boolean doesXMatch = true;
        boolean doesYMatch = true;
        boolean doesZMatch = true;

        if (data.isPresent("x")) {
            doesXMatch = comparison.compare(data.getDouble("x"), entity.getVelocity().x);
        }
        if (axes != null && axes.contains(Direction.Axis.X) && data.isPresent("compare_to")) {
            doesYMatch = comparison.compare(data.getDouble("compare_to"), entity.getVelocity().x);
        }
        if (data.isPresent("y")) {
            doesYMatch = comparison.compare(data.getDouble("y"), entity.getVelocity().y);
        }
        if (axes != null && axes.contains(Direction.Axis.Y) && data.isPresent("compare_to")) {
            doesYMatch = comparison.compare(data.getDouble("compare_to"), entity.getVelocity().y);
        }
        if (data.isPresent("z")) {
            doesZMatch = comparison.compare(data.getDouble("z"), entity.getVelocity().z);
        }
        if (axes != null && axes.contains(Direction.Axis.Z) && data.isPresent("compare_to")) {
            doesYMatch = comparison.compare(data.getDouble("compare_to"), entity.getVelocity().z);
        }

        return doesXMatch && doesYMatch && doesZMatch;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("velocity"), new SerializableData()
                .add("x", SerializableDataTypes.DOUBLE, null)
                .add("y", SerializableDataTypes.DOUBLE, null)
                .add("z", SerializableDataTypes.DOUBLE, null)
                .add("axes", SerializableDataTypes.AXIS_SET, null)
                .add("compare_to", SerializableDataTypes.DOUBLE, null)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
                VelocityCondition::condition
        );
    }
}
