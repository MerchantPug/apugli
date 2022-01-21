package io.github.merchantpug.apugli.registry.condition;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.merchantpug.apugli.condition.damage.IsExplosiveCondition;
import io.github.merchantpug.apugli.condition.damage.IsMagicCondition;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;

public class ApugliDamageConditions {
    public static void register() {
        register(IsExplosiveCondition.getFactory());
        register(IsMagicCondition.getFactory());
    }

    @ExpectPlatform
    private static void register(ConditionFactory<Pair<DamageSource, Float>> conditionFactory) {
        throw new AssertionError();
    }
}