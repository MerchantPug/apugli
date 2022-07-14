package com.github.merchantpug.apugli.condition.damage;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;

public class IsMagicCondition {
    public static boolean condition(SerializableData.Instance data, Pair<DamageSource, Float> damage) {
        return damage.getLeft().getMagic();
    }

    public static ConditionFactory<Pair<DamageSource, Float>> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("magic"), new SerializableData(),
                IsMagicCondition::condition
        );
    }
}

