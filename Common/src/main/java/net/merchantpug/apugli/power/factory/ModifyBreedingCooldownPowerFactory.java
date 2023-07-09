package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface ModifyBreedingCooldownPowerFactory<P> extends ValueModifyingPowerFactory<P> {
    
    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("bientity_condition", Services.CONDITION.biEntityDataType(), null);
    }

    default boolean doesApply(P power, LivingEntity entity, Entity animal) {
        SerializableData.Instance data = getDataFromPower(power);
        return Services.CONDITION.checkBiEntity(data, "bientity_condition", entity, animal);
    }
    
}
