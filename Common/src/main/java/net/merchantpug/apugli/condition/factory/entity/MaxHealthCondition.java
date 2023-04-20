package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class MaxHealthCondition implements IConditionFactory<Entity> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("compare_to", SerializableDataTypes.FLOAT)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL);
    }

    @Override
    public boolean check(SerializableData.Instance data, Entity instance) {
        Comparison comparison = data.get("comparison");
        float compareTo = data.getFloat("compare_to");
        if (instance instanceof LivingEntity living) {
            return comparison.compare(living.getMaxHealth(), compareTo);
        }
        return false;
    }

}
