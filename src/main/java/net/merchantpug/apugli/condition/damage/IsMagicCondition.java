package net.merchantpug.apugli.condition.damage;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.Apugli;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;

public class IsMagicCondition {
    public static boolean condition(SerializableData.Instance data, Pair<DamageSource, Float> damage) {
        return damage.getLeft().isMagic();
    }

    public static ConditionFactory<Pair<DamageSource, Float>> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("magic"), new SerializableData(),
                IsMagicCondition::condition
        );
    }
}

