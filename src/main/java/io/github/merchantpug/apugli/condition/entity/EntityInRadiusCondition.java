package io.github.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Predicate;

public class EntityInRadiusCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        Predicate<LivingEntity> entityCondition = ((ConditionFactory<LivingEntity>.Instance) data.get("entity_condition"));
        int stopAt = -1;
        Comparison comparison = ((Comparison) data.get("comparison"));
        int compareTo = data.getInt("compare_to");
        switch (comparison) {
            case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN -> stopAt = compareTo + 1;
            case LESS_THAN, GREATER_THAN_OR_EQUAL -> stopAt = compareTo;
        }
        int count = 0;
        for (Entity target : entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(data.getDouble("radius")))) {
            if (target instanceof LivingEntity) {
                if (entityCondition.test((LivingEntity) target)) {
                    count++;
                    if (count == stopAt) {
                        break;
                    }
                }
            }
        }
        return comparison.compare(count, compareTo);
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("entity_in_radius"), new SerializableData()
                .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION)
                .add("radius", SerializableDataTypes.DOUBLE)
                .add("compare_to", SerializableDataTypes.INT, 1)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
                EntityInRadiusCondition::condition
        );
    }
}
