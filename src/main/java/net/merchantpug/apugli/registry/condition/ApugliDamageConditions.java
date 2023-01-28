package net.merchantpug.apugli.registry.condition;

import net.merchantpug.apugli.condition.damage.IsMagicCondition;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.Registry;
import net.minecraft.util.Pair;

public class ApugliDamageConditions {
    public static void register() {
        register(IsMagicCondition.getFactory());
    }

    private static void register(ConditionFactory<Pair<DamageSource, Float>> conditionFactory) {
        Registry.register(ApoliRegistries.DAMAGE_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
