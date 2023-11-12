package net.merchantpug.apugli.condition.factory.bientity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.condition.factory.entity.DimensionsCondition;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.EnumSet;

public class CompareDimensionsCondition implements IConditionFactory<Tuple<Entity, Entity>> {
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("dimensions", SerializableDataType.enumSet(DimensionsCondition.Dimension.class, SerializableDataType.enumValue(DimensionsCondition.Dimension.class)), EnumSet.of(DimensionsCondition.Dimension.WIDTH, DimensionsCondition.Dimension.HEIGHT))
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL);
    }

    @Override
    public boolean check(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        Comparison comparison = data.get("comparison");
        EnumSet<DimensionsCondition.Dimension> dimensions = data.get("dimensions");

        for(DimensionsCondition.Dimension dimension : dimensions) {
            if (!switch (dimension) {
                case WIDTH -> comparison.compare(pair.getA().getDimensions(pair.getA().getPose()).width, pair.getB().getDimensions(pair.getB().getPose()).width);
                case HEIGHT -> comparison.compare(pair.getA().getDimensions(pair.getA().getPose()).height, pair.getB().getDimensions(pair.getB().getPose()).height);
            }) return false;
        }

        return true;
    }

}