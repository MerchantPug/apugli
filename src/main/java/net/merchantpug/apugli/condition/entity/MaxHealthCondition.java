package net.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class MaxHealthCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        Comparison comparison = data.get("comparison");
        float compareTo = data.getFloat("compare_to");
        if (entity instanceof LivingEntity living) {
            return comparison.compare(living.getMaxHealth(), compareTo);
        }
        return false;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("max_health"), new SerializableData()
                .add("compare_to", SerializableDataTypes.FLOAT)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
                MaxHealthCondition::condition
        );
    }
}
