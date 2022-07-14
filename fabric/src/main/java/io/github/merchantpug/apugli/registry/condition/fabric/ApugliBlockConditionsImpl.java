package io.github.merchantpug.apugli.registry.condition.fabric;

import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.registry.ModRegistries;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.registry.Registry;

public class ApugliBlockConditionsImpl {
    public static void register(ConditionFactory<CachedBlockPosition> conditionFactory) {
        Registry.register(ModRegistries.BLOCK_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
