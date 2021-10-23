package io.github.merchantpug.apugli.registry.condition;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.merchantpug.apugli.condition.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.registry.Registry;

public class ApugliEntityConditions {
    public static void register() {
        register(ApugliEntityGroupCondition.getFactory());
        register(CanHaveEffectCondition.getFactory());
        register(EntityInRadiusCondition.getFactory());
        register(JoinInvulnerabilityTicksCondition.getFactory());
        register(RaycastCondition.getFactory());
        register(StructureCondition.getFactory());
    }

    private static void register(ConditionFactory<Entity> conditionFactory) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
