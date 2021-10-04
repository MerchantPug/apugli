package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.merchantpug.apugli.condition.damage.IsExplosiveCondition;
import io.github.merchantpug.apugli.condition.damage.IsMagicCondition;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

public class ApugliDamageConditions {
    public static void register() {
        register(IsExplosiveCondition.getFactory());
        register(IsMagicCondition.getFactory());
    }

    private static void register(ConditionFactory<Pair<DamageSource, Float>> conditionFactory) {
        Registry.register(ApoliRegistries.DAMAGE_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
