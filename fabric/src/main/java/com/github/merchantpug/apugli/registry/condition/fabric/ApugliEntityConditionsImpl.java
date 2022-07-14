package com.github.merchantpug.apugli.registry.condition.fabric;

import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.registry.ModRegistries;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.registry.Registry;

public class ApugliEntityConditionsImpl {
    public static void register(ConditionFactory<LivingEntity> conditionFactory) {
        Registry.register(ModRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
