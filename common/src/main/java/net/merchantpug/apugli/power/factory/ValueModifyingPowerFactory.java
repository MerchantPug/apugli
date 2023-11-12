package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.Entity;

import java.util.List;

public interface ValueModifyingPowerFactory<P> extends SpecialPowerFactory<P> {
    
    static SerializableData getSerializableData() {
        return new SerializableData()
                .add("modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("modifiers", Services.PLATFORM.getModifiersDataType(), null);
    }

    List<?> getModifiers(P power, Entity entity);

}
