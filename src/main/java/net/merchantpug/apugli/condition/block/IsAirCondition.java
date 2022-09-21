package net.merchantpug.apugli.condition.block;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.Apugli;
import net.minecraft.block.pattern.CachedBlockPosition;

public class IsAirCondition {
    public static boolean condition(SerializableData.Instance data, CachedBlockPosition block) {
        return block.getBlockState().isAir();
    }

    public static ConditionFactory<CachedBlockPosition> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("air"), new SerializableData(),
                IsAirCondition::condition
        );
    }
}
