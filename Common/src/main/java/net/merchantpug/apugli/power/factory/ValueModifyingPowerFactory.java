package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;

public interface ValueModifyingPowerFactory<P> extends SpecialPowerFactory<P> {
    
    static SerializableData getSerializableData() {
        return new SerializableData()
                .add("modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
                .add("modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null);
    }

    List<AttributeModifier> getModifiers(P power, Entity entity);

}
