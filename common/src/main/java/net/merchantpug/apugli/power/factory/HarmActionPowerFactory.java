package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface HarmActionPowerFactory<P> extends CooldownPowerFactory<P> {

    static SerializableData getSerializableData() {
        return CooldownPowerFactory.getSerializableData()
                .add("entity_action", Services.ACTION.entityDataType(), null)
                .add("bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("damage_condition", Services.CONDITION.damageDataType(), null)
                .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("amount_to_trigger", SerializableDataTypes.FLOAT, 1.0F)
                .add("overflow", SerializableDataTypes.BOOLEAN, false)
                .add("limit", SerializableDataTypes.FLOAT, Float.NaN);
    }

    default void execute(P power, LivingEntity powerHolder, DamageSource source, float amount, Entity attacker, LivingEntity target) {
        SerializableData.Instance data = getDataFromPower(power);
        if (canUse(power, powerHolder) && (Services.CONDITION.checkDamage(data, "damage_condition", source, amount)) && (Services.CONDITION.checkBiEntity(data, "bientity_condition", attacker, target))) {
            float triggerTimes = data.getBoolean("overflow") ? amount / data.getFloat("amount_to_trigger") : Math.min(target.getHealth(), amount) / data.getFloat("amount_to_trigger");
            float limit = data.getFloat("limit");
            for (int i = 0; i < Math.min(triggerTimes, Float.isNaN(limit) ? target.getMaxHealth() * 4 : limit); ++i) {
                Services.ACTION.executeEntity(data, "entity_action", powerHolder);
                if (attacker != null && target != null)
                    Services.ACTION.executeBiEntity(data, "bientity_action", attacker, target);
            }
            this.use(power, powerHolder);
        }
    }

}