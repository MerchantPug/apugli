package io.github.merchantpug.apugli.registry.condition;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.merchantpug.apugli.condition.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.registry.Registry;

public class ApugliEntityConditions {
    public static void register() {
        register(CanHaveEffectCondition.getFactory());
        register(CompareResourceCondition.getFactory());
        register(EntityInRadiusCondition.getFactory());
        register(JoinInvulnerabilityTicksCondition.getFactory());
        register(KeyPressedCondition.getCondition());
        register(ParticleInRadiusCondition.getFactory());
        register(PlayerModelTypeCondition.getFactory());
        register(RaycastCondition.getFactory());
        register(StructureCondition.getFactory());
        register(VelocityCondition.getFactory());
    }

    private static void register(ConditionFactory<Entity> conditionFactory) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
