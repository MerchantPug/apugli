package net.merchantpug.apugli.condition.factory.entity.integration.pehkui;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.integration.pehkui.PehkuiUtil;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScaleCondition implements IConditionFactory<Entity> {

    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("scale_type", SerializableDataTypes.IDENTIFIER, null)
                .add("scale_types", SerializableDataTypes.IDENTIFIERS, null)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
                .add("compare_to", SerializableDataTypes.FLOAT);
    }

    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        if (!Services.PLATFORM.isModLoaded("pehkui")) {
            if (!Apugli.hasDataBeenErrorLogged(data)) {
                Apugli.LOG.warn("Attempted to load 'apugli:scale' data type.");
                Apugli.addToErrorLoggedData(data);
            }
            return false;
        }

        Set<ResourceLocation> scaleTypeSet = new HashSet<>();
        data.ifPresent("scale_type", scaleTypeSet::add);
        data.<List<ResourceLocation>>ifPresent("scale_type", scaleTypeSet::addAll);

        Comparison comparison = data.get("comparison");
        float compareTo = data.getFloat("compare_to");

        return scaleTypeSet.stream().allMatch(id -> comparison.compare(PehkuiUtil.getScale(entity, id), compareTo));
    }

}
