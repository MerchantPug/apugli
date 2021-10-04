package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.merchantpug.apugli.condition.block.IsAirCondition;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.registry.Registry;

public class ApugliBlockConditions {
    public static void register() {
        register(IsAirCondition.getFactory());
    }

    private static void register(ConditionFactory<CachedBlockPosition> conditionFactory) {
        Registry.register(ApoliRegistries.BLOCK_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
