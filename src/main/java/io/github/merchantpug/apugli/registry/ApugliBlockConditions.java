package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.registry.Registry;

public class ApugliBlockConditions {
    @SuppressWarnings("unchecked")
    public static void register() {
        register(new ConditionFactory<>(Apugli.identifier("air"), new SerializableData(),
                (data, block) -> block.getBlockState().isAir()));
    }

    private static void register(ConditionFactory<CachedBlockPosition> conditionFactory) {
        Registry.register(ApoliRegistries.BLOCK_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
