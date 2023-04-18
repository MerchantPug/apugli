package com.github.merchantpug.apugli.power.data;

import com.github.merchantpug.apugli.platform.Services;
import com.github.merchantpug.apugli.power.ActionOnEquipPower;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;

public interface IActionOnEquipPowerData extends IPowerData<ActionOnEquipPower> {
    @Override
    default SerializableData getSerializableData() {
        return new SerializableData()
                .add("slot", SerializableDataTypes.EQUIPMENT_SLOT)
                .add("item_condition", Services.PLATFORM.getItemConditionDataType(), null)
                .add("entity_action", Services.PLATFORM.getEntityActionDataType());
    }
}