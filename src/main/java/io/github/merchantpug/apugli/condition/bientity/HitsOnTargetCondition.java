package io.github.merchantpug.apugli.condition.bientity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.access.LivingEntityAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

public class HitsOnTargetCondition {
    public static boolean condition(SerializableData.Instance data, Pair<Entity, Entity> pair) {
        Apugli.LOGGER.info(pair.getRight() instanceof LivingEntity);
        if (pair.getRight() instanceof LivingEntity) {
            int hitsOnTarget = ((LivingEntityAccess)pair.getRight()).getHits().getOrDefault(pair.getLeft(), 0);
            Comparison comparison = data.get("comparison");
            int compareTo = data.getInt("compare_to");
            Apugli.LOGGER.info("Hits on Target: " + comparison);
            return comparison.compare(hitsOnTarget, compareTo);
        }
        return false;
    }

    public static ConditionFactory<Pair<Entity, Entity>> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("hits_on_target"), new SerializableData()
                .add("comparison", ApoliDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.INT),
                HitsOnTargetCondition::condition
        );
    }
}
