package net.merchantpug.apugli.condition.bientity;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.LivingEntityAccess;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

public class HitsOnTargetCondition {
    public static boolean condition(SerializableData.Instance data, Pair<Entity, Entity> pair) {
        if (pair.getRight() instanceof LivingEntity) {
            Pair<Integer, Integer> hitsOnTarget = ((LivingEntityAccess)pair.getRight()).getHits().getOrDefault(pair.getLeft(), new Pair<>(0, 0));
            Comparison comparison = data.get("comparison");
            int compareTo = data.getInt("compare_to");
            return comparison.compare(hitsOnTarget.getLeft(), compareTo);
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
