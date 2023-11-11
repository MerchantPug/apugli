package net.merchantpug.apugli.condition.factory.bientity.integration.pehkui;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.integration.pehkui.PehkuiUtil;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompareScalesCondition implements IConditionFactory<Tuple<Entity, Entity>> {

    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("scale_type", SerializableDataTypes.IDENTIFIER, null)
                .add("scale_types", SerializableDataTypes.IDENTIFIERS, null)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL);
    }

    @Override
    public boolean check(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        if (!Services.PLATFORM.isModLoaded("pehkui")) {
            return true;
        }

        Comparison comparison = data.get("comparison");

        Set<ResourceLocation> scaleTypeSet = new HashSet<>();
        data.ifPresent("scale_type", scaleTypeSet::add);
        data.<List<ResourceLocation>>ifPresent("scale_type", scaleTypeSet::addAll);

        return scaleTypeSet.stream().allMatch(id -> comparison.compare(PehkuiUtil.getScale(pair.getA(), id), PehkuiUtil.getScale(pair.getB(), id)));
    }

}
