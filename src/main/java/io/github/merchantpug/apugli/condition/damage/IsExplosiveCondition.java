package io.github.merchantpug.apugli.condition.damage;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;

public class IsExplosiveCondition {
    public static boolean condition(SerializableData.Instance data, Pair<DamageSource, Float> damage) {
        return damage.getLeft().isExplosive();
    }

    public static ConditionFactory<Pair<DamageSource, Float>> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("explosive"), new SerializableData(),
                IsExplosiveCondition::condition
        );
    }
}

