package net.merchantpug.apugli.condition.factory.entity;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.platform.Services;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;

public class EntityInRadiusCondition implements IConditionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("condition", Services.CONDITION.entityDataType(), null)
            .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
            .add("radius", SerializableDataTypes.DOUBLE)
            .add("compare_to", SerializableDataTypes.INT, 1)
            .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL);
    }
    
    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        Predicate<Entity> entityCondition = Services.CONDITION.entityPredicate(data, "condition");
        Predicate<Tuple<Entity, Entity>> biEntityCondition = Services.CONDITION.biEntityPredicate(data, "bientity_condition");
        int stopAt = -1;
        Comparison comparison = data.get("comparison");
        int compareTo = data.getInt("compare_to");
        switch (comparison) {
            case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN -> stopAt = compareTo + 1;
            case LESS_THAN, GREATER_THAN_OR_EQUAL -> stopAt = compareTo;
        }
        int count = 0;
        for(Entity target : entity.level().getEntities(entity, entity.getBoundingBox().inflate(data.getDouble("radius")))) {
            if(target != null) {
                if((entityCondition == null || entityCondition.test(target)) &&
                   (biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, target)))
                ) {
                    count++;
                    if(count == stopAt) {
                        break;
                    }
                }
            }
        }
        return comparison.compare(count, compareTo);
    }

}
