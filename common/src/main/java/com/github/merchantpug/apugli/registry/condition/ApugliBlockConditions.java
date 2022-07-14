package com.github.merchantpug.apugli.registry.condition;

import com.github.merchantpug.apugli.condition.block.IsAirCondition;
import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import net.minecraft.block.pattern.CachedBlockPosition;

public class ApugliBlockConditions {
    public static void register() {
        register(IsAirCondition.getFactory());
    }

    @ExpectPlatform
    private static void register(ConditionFactory<CachedBlockPosition> conditionFactory) {
        throw new AssertionError();
    }
}