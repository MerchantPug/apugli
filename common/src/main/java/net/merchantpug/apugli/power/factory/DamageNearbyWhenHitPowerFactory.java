package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface DamageNearbyWhenHitPowerFactory<P> extends DamageNearbyPowerFactory<P> {

    static SerializableData getSerializableData() {
        return DamageNearbyPowerFactory.getSerializableData()
                .add("attacker_self_bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("attacker_nearby_bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("self_nearby_bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("attacker_self_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("attacker_nearby_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("self_nearby_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("inflictor", SerializableDataType.enumValue(Inflictor.class), Inflictor.ATTACKER);
    }

    default void execute(P power, LivingEntity entity, DamageSource damageSource, float damageAmount) {
        this.execute(power, entity, damageSource, damageAmount, damageSource.getEntity(), entity,
                "attacker", "self", getDataFromPower(power).get("inflictor").equals(Inflictor.ATTACKER));
    }

    enum Inflictor {
        ATTACKER,
        SELF
    }
}
