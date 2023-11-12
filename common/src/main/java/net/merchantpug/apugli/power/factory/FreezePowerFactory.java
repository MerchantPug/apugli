package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.Entity;

public interface FreezePowerFactory<P> extends ValueModifyingPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("should_damage_condition", Services.CONDITION.entityDataType(), null)
                .add("should_damage", SerializableDataTypes.BOOLEAN, true);
    }

    default boolean shouldDamage(P power, Entity entity) {
        SerializableData.Instance data = getDataFromPower(power);

        if (!Services.CONDITION.checkEntity(data, "should_damage_condition", entity)) {
            return false;
        }

        return data.getBoolean("should_damage");
    }

}
