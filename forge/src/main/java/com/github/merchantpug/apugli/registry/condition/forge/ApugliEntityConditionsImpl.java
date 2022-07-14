package com.github.merchantpug.apugli.registry.condition.forge;

import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import com.github.merchantpug.apugli.registry.forge.ApugliRegistriesArchitectury;
import net.minecraft.entity.LivingEntity;

public class ApugliEntityConditionsImpl {
    public static void register(ConditionFactory<LivingEntity> conditionFactory) {
        ApugliRegistriesArchitectury.ENTITY_CONDITION.register(conditionFactory.getSerializerId(), () -> conditionFactory);
    }
}
