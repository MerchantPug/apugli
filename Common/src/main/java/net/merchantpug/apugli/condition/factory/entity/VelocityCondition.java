package net.merchantpug.apugli.condition.factory.entity;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class VelocityCondition implements IConditionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("x", SerializableDataTypes.DOUBLE, null)
            .add("y", SerializableDataTypes.DOUBLE, null)
            .add("z", SerializableDataTypes.DOUBLE, null)
            .add("axes", SerializableDataTypes.AXIS_SET, null)
            .add("compare_to", SerializableDataTypes.DOUBLE, null)
            .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL);
    }
    
    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        Comparison comparison = data.get("comparison");
        EnumSet<Direction.Axis> axes = data.get("axes");
        Vec3 velocity = entity.getDeltaMovement();
        if(axes != null && !axes.isEmpty() && data.isPresent("compare_to")) {
            double compareTo = data.getDouble("compare_to");
            for(Direction.Axis axis : axes) {
                if(!switch(axis) {
                    case X -> comparison.compare(compareTo, velocity.x);
                    case Y -> comparison.compare(compareTo, velocity.y);
                    case Z -> comparison.compare(compareTo, velocity.z);
                }) return false;
            }
        }
        if(data.isPresent("x") && comparison.compare(data.getDouble("x"), velocity.x)) {
            return false;
        }
        if(data.isPresent("y") && comparison.compare(data.getDouble("y"), velocity.y)) {
            return false;
        }
        if(data.isPresent("z") && comparison.compare(data.getDouble("z"), velocity.z)) {
            return false;
        }
        return true;
    }

}
