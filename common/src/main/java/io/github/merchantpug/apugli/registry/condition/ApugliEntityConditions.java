package io.github.merchantpug.apugli.registry.condition;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.merchantpug.apugli.condition.entity.*;
import net.minecraft.entity.LivingEntity;

public class ApugliEntityConditions {
    public static void register() {
        register(CanHaveEffectCondition.getFactory());
        register(CompareResourceCondition.getFactory());
        register(EntityInRadiusCondition.getFactory());
        register(JoinInvulnerabilityTicksCondition.getFactory());
        register(KeyPressedCondition.getFactory());
        register(ParticleInRadiusCondition.getFactory());
        register(PlayerModelTypeCondition.getFactory());
        register(RaycastCondition.getFactory());
        register(RidingCondition.getFactory());
        register(StructureCondition.getFactory());
        register(VelocityCondition.getFactory());
    }

    @ExpectPlatform
    private static void register(ConditionFactory<LivingEntity> conditionFactory) {
        throw new AssertionError();
    }
}
