package io.github.merchantpug.apugli.registry.condition.forge;

import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class ApugliEntityConditionsImpl {
    public static void register(ConditionFactory<LivingEntity> conditionFactory) {
        ModRegistriesArchitectury.ENTITY_CONDITION.register(conditionFactory.getSerializerId(), () -> conditionFactory);
    }
}
