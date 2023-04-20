package net.merchantpug.apugli.condition.factory.bientity;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

public class HitsOnTargetCondition implements IConditionFactory<Tuple<Entity, Entity>> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("comparison", ApoliDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.INT);
    }
    
    public boolean check(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        if(pair.getB() instanceof LivingEntityAccess access) {
            Tuple<Integer, Integer> hitsOnTarget = access.getHits().getOrDefault(pair.getA(), new Tuple<>(0, 0));
            Comparison comparison = data.get("comparison");
            int compareTo = data.getInt("compare_to");
            return comparison.compare(hitsOnTarget.getA(), compareTo);
        }
        return false;
    }
    
}
