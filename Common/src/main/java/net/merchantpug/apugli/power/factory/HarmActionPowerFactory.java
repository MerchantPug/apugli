package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface HarmActionPowerFactory<P> extends CooldownPowerFactory<P> {

    static SerializableData getSerializableData() {
        return CooldownPowerFactory.getSerializableData()
                .add("bientity_action", Services.ACTION.biEntityDataType())
                .add("damage_condition", Services.CONDITION.damageDataType(), null)
                .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("amount_to_trigger", SerializableDataTypes.FLOAT, 1.0F)
                .add("overflow", SerializableDataTypes.BOOLEAN, false);
    }

    default void execute(P power, LivingEntity powerHolder, DamageSource source, float amount, LivingEntity attacker, LivingEntity target) {
        SerializableData.Instance data = getDataFromPower(power);
        if (canUse(power, powerHolder) && (Services.CONDITION.checkDamage(data, "damage_condition", source, amount)) && (Services.CONDITION.checkBiEntity(data, "bientity_condition", attacker, target))) {
            float triggerTimes = data.getBoolean("overflow") ? amount / data.getFloat("amount_to_trigger") : Math.min(target.getHealth(), amount) / data.getFloat("amount_to_trigger");
            for (int i = 0; i < triggerTimes; ++i) {
                Services.ACTION.executeBiEntity(data, "bientity_action", attacker, target);
            }
            this.use(power, powerHolder);
        }
    }

}