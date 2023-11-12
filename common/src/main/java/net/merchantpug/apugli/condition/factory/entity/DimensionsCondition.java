package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.minecraft.world.entity.Entity;

import java.util.EnumSet;

public class DimensionsCondition implements IConditionFactory<Entity> {
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("dimensions", SerializableDataType.enumSet(Dimension.class, SerializableDataType.enumValue(Dimension.class)), EnumSet.of(Dimension.WIDTH, Dimension.HEIGHT))
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
                .add("compare_to", SerializableDataTypes.FLOAT);
    }

    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        EnumSet<DimensionsCondition.Dimension> dimensions = data.get("dimensions");
        Comparison comparison = data.get("comparison");
        float compareTo = data.get("compare_to");

        for(DimensionsCondition.Dimension dimension : dimensions) {
            if (!switch (dimension) {
                case WIDTH -> comparison.compare(entity.getDimensions(entity.getPose()).width, compareTo);
                case HEIGHT -> comparison.compare(entity.getDimensions(entity.getPose()).height, compareTo);
            }) return false;
        }

        return true;
    }

    public enum Dimension {
        WIDTH,
        HEIGHT
    }
}
