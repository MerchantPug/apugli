package net.merchantpug.apugli.condition.factory.entity;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.platform.Services;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.OptionalInt;

public class CompareResourceCondition implements IConditionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("resource", Services.POWER.getPowerTypeDataType())
            .add("compare_to", Services.POWER.getPowerTypeDataType())
            .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL);
    }
    
    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        if(!(entity instanceof LivingEntity living)) return false;
        Comparison comparison = data.get("comparison");
        OptionalInt resource = Services.POWER.getResource(living, data, "resource");
        if(resource.isEmpty()) return false;
        OptionalInt compareTo = Services.POWER.getResource(living, data, "compare_to");
        if(compareTo.isEmpty()) return false;
        return comparison.compare(resource.getAsInt(), compareTo.getAsInt());
    }

}
