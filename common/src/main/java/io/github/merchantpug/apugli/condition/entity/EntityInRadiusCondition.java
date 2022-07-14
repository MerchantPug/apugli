package io.github.merchantpug.apugli.condition.entity;

import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Predicate;

public class EntityInRadiusCondition {
    public static boolean condition(SerializableData.Instance data, LivingEntity entity) {
        Predicate<Entity> entityCondition = (ConditionFactory<Entity>.Instance)data.get("condition");
        int stopAt = -1;
        Comparison comparison = (Comparison)data.get("comparison");
        int compareTo = data.getInt("compare_to");
        switch (comparison) {
            case EQUAL:
            case LESS_THAN_OR_EQUAL:
            case GREATER_THAN:
                stopAt = compareTo + 1;
                break;
            case LESS_THAN:
            case GREATER_THAN_OR_EQUAL:
                stopAt = compareTo;
        }
        int count = 0;
        for (Entity target : entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(data.getDouble("radius")))) {
            if (target instanceof LivingEntity) {
                if (entityCondition == null || entityCondition.test(target)) {
                    count++;
                    if (count == stopAt) {
                        break;
                    }
                }
            }
        }
        return comparison.compare(count, compareTo);
    }

    public static ConditionFactory<LivingEntity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("entity_in_radius"), new SerializableData()
                .add("condition", SerializableDataType.ENTITY_CONDITION, null)
                .add("radius", SerializableDataType.DOUBLE)
                .add("compare_to", SerializableDataType.INT, 1)
                .add("comparison", SerializableDataType.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
                EntityInRadiusCondition::condition
        );
    }
}
