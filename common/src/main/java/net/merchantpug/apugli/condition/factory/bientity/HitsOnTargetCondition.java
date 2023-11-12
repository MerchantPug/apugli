package net.merchantpug.apugli.condition.factory.bientity;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class HitsOnTargetCondition implements IConditionFactory<Tuple<Entity, Entity>> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("comparison", ApoliDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.INT);
    }
    
    public boolean check(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        if (pair.getB() instanceof LivingEntity living) {
            Tuple<Integer, Integer> hitsOnTarget = Services.PLATFORM.getHitsOnTarget(pair.getA(), living);
            Comparison comparison = data.get("comparison");
            int compareTo = data.getInt("compare_to");
            return comparison.compare(hitsOnTarget.getA(), compareTo);
        }
        return false;
    }
    
}
