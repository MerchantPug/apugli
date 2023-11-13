package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

public interface ModifyScalePowerFactory<P> extends ValueModifyingPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("scale_type", SerializableDataTypes.IDENTIFIER, null)
                .add("scale_types", SerializableDataType.list(SerializableDataTypes.IDENTIFIER), new ArrayList<>())
                .add("delay", SerializableDataTypes.INT, 0);
    }

    ResourceLocation getPowerId(P power);

    Object getApoliScaleModifier(P power, Entity entity);

    default Object getApoliScaleModifier(ResourceLocation powerId, Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return null;
        }
        Optional<P> power = Services.POWER.getPowers(living, this, true).stream().filter(p -> this.getPowerId(p).equals(powerId)).findFirst();
        return power.map(p -> getApoliScaleModifier(p, living)).orElse(null);
    }

    Set<ResourceLocation> getCachedScaleIds(P power, Entity entity);

}
