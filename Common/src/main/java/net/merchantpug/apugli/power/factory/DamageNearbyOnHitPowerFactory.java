package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.LivingEntity;

public interface DamageNearbyOnHitPowerFactory<P> extends DamageNearbyPowerFactory<P> {

    static SerializableData getSerializableData() {
        return DamageNearbyPowerFactory.getSerializableData()
                .add("self_target_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("self_nearby_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("target_nearby_bientity_condition", Services.CONDITION.biEntityDataType(), null);
    }

    default void execute(P power, LivingEntity entity, float damageAmount, LivingEntity target) {
        this.execute(power, entity, damageAmount, entity, target,
                "self", "target");
    }
}
