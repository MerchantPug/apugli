package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface DamageNearbyWhenHitPowerFactory<P> extends DamageNearbyPowerFactory<P> {

    static SerializableData getSerializableData() {
        return DamageNearbyPowerFactory.getSerializableData()
                .add("attacker_self_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("attacker_nearby_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("self_nearby_bientity_condition", Services.CONDITION.biEntityDataType(), null);
    }

    default void execute(P power, LivingEntity entity, DamageSource source, float damageAmount) {
        this.execute(power, entity, damageAmount, source.getEntity(), entity,
                "attacker", "self");
    }
}
