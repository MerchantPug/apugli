package io.github.merchantpug.apugli.registry.condition.forge;

import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.merchantpug.apugli.registry.ApugliRegistriesArchitectury;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;

public class ApugliDamageConditionsImpl {
    public static void register(ConditionFactory<Pair<DamageSource, Float>> conditionFactory) {
        ApugliRegistriesArchitectury.DAMAGE_CONDITION.register(conditionFactory.getSerializerId(), () -> conditionFactory);
    }
}
