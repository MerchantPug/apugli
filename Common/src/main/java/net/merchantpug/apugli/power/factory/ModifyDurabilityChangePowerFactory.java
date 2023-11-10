package net.merchantpug.apugli.power.factory;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.data.ApugliDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;

public interface ModifyDurabilityChangePowerFactory<P> extends ValueModifyingPowerFactory<P> {
    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("item_condition", Services.CONDITION.itemDataType(), null)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
                .add("compare_to", SerializableDataTypes.INT, Integer.MIN_VALUE)
                .add("comparisons", ApugliDataTypes.comparisonMap(SerializableDataTypes.INT), Map.of());
    }

    default boolean doesApply(P power, Level level, ItemStack stack, int durabilityChange) {
        SerializableData.Instance data = getDataFromPower(power);
        return Services.CONDITION.checkItem(data, "item_condition", level, stack) && checkComparisons(data, durabilityChange);
    }

    default boolean checkComparisons(SerializableData.Instance data, int durabilityChange) {
        for (Map.Entry<Comparison, Integer> entry : ((Map<Comparison, Integer>) data.get("comparisons")).entrySet()) {
            if (!entry.getKey().compare(durabilityChange, entry.getValue())) {
                return false;
            }
        }

        return ((Comparison) data.get("comparison")).compare(durabilityChange, data.getInt("compare_to"));
    }

}
