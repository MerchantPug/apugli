package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public interface ModifyScalePowerFactory<P> extends ValueModifyingPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("scale_type", SerializableDataTypes.IDENTIFIER, null)
                .add("scale_types", SerializableDataType.list(SerializableDataTypes.IDENTIFIER), new ArrayList<>())
                .add("delay", SerializableDataTypes.INT, null);
    }

    ResourceLocation getPowerId(P power);

    default ResourceLocation getMappedScaleModifierId(P power) {
        return Apugli.asResource("modifyscalepower/" + getPowerId(power).getNamespace() + "/" + getPowerId(power).getPath());
    }

}
