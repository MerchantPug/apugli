package com.github.merchantpug.apugli.condition.entity;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Predicate;

public class RidingCondition {
    public static boolean condition(SerializableData.Instance data, LivingEntity entity) {
        if(entity.hasVehicle()) {
            if(data.isPresent("entity_condition")) {
                Predicate<LivingEntity> condition = data.get("entity_condition");
                Entity vehicle = entity.getVehicle();
                return vehicle instanceof LivingEntity && condition.test((LivingEntity) vehicle);
            }
            return true;
        }
        return false;
    }

    public static ConditionFactory<LivingEntity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("riding"), new SerializableData()
                .add("entity_condition", SerializableDataType.ENTITY_CONDITION, null),
                RidingCondition::condition
        );
    }
}
