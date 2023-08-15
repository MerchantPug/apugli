package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;

public interface ActionWhenLightningStruckPowerFactory<P> extends CooldownPowerFactory<P> {

    static SerializableData getSerializableData() {
        return CooldownPowerFactory.getSerializableData()
                .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("entity_action", Services.ACTION.entityDataType());
    }

    default void execute(P power, LivingEntity entity, LightningBolt bolt) {
        if (!canUse(power, entity) || getDataFromPower(power).isPresent("bientity_condition") && Services.CONDITION.checkBiEntity(getDataFromPower(power), "bientity_condition", entity, bolt)) return;
        Services.ACTION.executeEntity(getDataFromPower(power), "entity_action", entity);
        this.use(power, entity);
    }

}
