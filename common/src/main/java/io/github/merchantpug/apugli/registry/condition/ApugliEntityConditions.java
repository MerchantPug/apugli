package io.github.merchantpug.apugli.registry.condition;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.merchantpug.apugli.condition.entity.*;
import net.minecraft.entity.LivingEntity;

public class ApugliEntityConditions {
    public static void register() {
        register(CanHaveEffectCondition.getFactory());
        register(EntityInRadiusCondition.getFactory());
        register(JoinInvulnerabilityTicksCondition.getFactory());
        register(ParticleInRadiusCondition.getFactory());
        register(RaycastCondition.getFactory());
        register(StructureCondition.getFactory());
    }

    @ExpectPlatform
    private static void register(ConditionFactory<LivingEntity> conditionFactory) {
        throw new AssertionError();
    }
}
