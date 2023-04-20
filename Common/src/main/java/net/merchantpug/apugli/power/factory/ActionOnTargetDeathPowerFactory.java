package net.merchantpug.apugli.power.factory;

import net.merchantpug.apugli.platform.Services;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface ActionOnTargetDeathPowerFactory<P> extends CooldownPowerFactory<P> {
    
    static SerializableData getSerializableData() {
        return CooldownPowerFactory.getSerializableData()
            .add("bientity_action", Services.ACTION.biEntityDataType())
            .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
            .add("damage_condition", Services.CONDITION.damageDataType(), null)
            .add("includes_prime_adversary", SerializableDataTypes.BOOLEAN, true);
    }
    
    default void onTargetDeath(LivingEntity actor, Entity target, DamageSource damageSource, float damageAmount) {
        for(P power : Services.POWER.getPowers(actor, this)) {
            if(canUse(power, actor)) {
                SerializableData.Instance data = getDataFromPower(power);
                if(Services.CONDITION.checkBiEntity(data, "bientity_condition", actor, target) &&
                    Services.CONDITION.checkDamage(data, "damage_condition", damageSource, damageAmount)
                ) {
                    Services.ACTION.executeBiEntity(data, "bientity_action", actor, target);
                    use(power, actor);
                }
            }
        }
    }
    
}