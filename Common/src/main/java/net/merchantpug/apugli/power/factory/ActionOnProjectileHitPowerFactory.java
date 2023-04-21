package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.platform.Services;

public interface ActionOnProjectileHitPowerFactory<P> extends CooldownPowerFactory<P> {

    static SerializableData getSerializableData() {
        return CooldownPowerFactory.getSerializableData()
                .add("bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("owner_bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("owner_bientity_condition", Services.CONDITION.biEntityDataType(), null);
    }
}