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
                    case X -> comparison.compare(velocity.x, compareTo);
                    case Y -> comparison.compare(velocity.y, compareTo);
                    case Z -> comparison.compare(velocity.z, compareTo);
                }) return false;
            }
        }
        if(data.isPresent("x") && comparison.compare(velocity.x, data.getDouble("x"))) {
            return false;
        }
        if(data.isPresent("y") && comparison.compare(velocity.y, data.getDouble("y"))) {
            return false;
        }
        if(data.isPresent("z") && comparison.compare(velocity.z, data.getDouble("z"))) {
            return false;
        }
        return true;
    }

}
