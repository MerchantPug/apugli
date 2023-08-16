package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;

public interface FreezePowerFactory<P> extends ValueModifyingPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("should_damage", SerializableDataTypes.BOOLEAN, true);
    }

    default boolean shouldDamage(P power) {
        return getDataFromPower(power).getBoolean("should_damage");
    }

}
