package io.github.merchantpug.apugli.registry.condition.forge;

import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.merchantpug.apugli.registry.forge.ApugliRegistriesArchitectury;
import net.minecraft.block.pattern.CachedBlockPosition;

public class ApugliBlockConditionsImpl {
    public static void register(ConditionFactory<CachedBlockPosition> conditionFactory) {
        ApugliRegistriesArchitectury.BLOCK_CONDITION.register(conditionFactory.getSerializerId(), () -> conditionFactory);
    }
}
