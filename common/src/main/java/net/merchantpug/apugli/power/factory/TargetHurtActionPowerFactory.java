package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface TargetHurtActionPowerFactory<P> extends CooldownPowerFactory<P> {
    
    static SerializableData getSerializableData() {
        return CooldownPowerFactory.getSerializableData()
            .add("bientity_action", Services.ACTION.biEntityDataType())
            .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
            .add("damage_condition", Services.CONDITION.damageDataType(), null);
    }
    
    default void execute(LivingEntity powerHolder, Entity actor, Entity target, DamageSource damageSource, float damageAmount) {
        for(P power : Services.POWER.getPowers(powerHolder, this)) {
            if (canUse(power, powerHolder)) {
                SerializableData.Instance data = getDataFromPower(power);
                if (Services.CONDITION.checkBiEntity(data, "bientity_condition", actor, target) && Services.CONDITION.checkDamage(data, "damage_condition", damageSource, damageAmount)) {
                    Services.ACTION.executeBiEntity(data, "bientity_action", actor, target);
                    use(power, powerHolder);
                }
            }
        }
    }
    
}