package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

public interface ProjectileHitActionPowerFactory<P> extends CooldownPowerFactory<P> {

    static SerializableData getSerializableData() {
        return CooldownPowerFactory.getSerializableData()
                .add("bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("owner_bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("owner_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("stop_after", SerializableDataTypes.INT, 1);
    }

    default void execute(P power, LivingEntity powerHolder, Entity attacker, Entity target, Projectile projectile, int amountHit) {
        SerializableData.Instance data = getDataFromPower(power);
        if (canUse(power, powerHolder) && Services.CONDITION.checkBiEntity(data, "bientity_condition", projectile, target) && Services.CONDITION.checkBiEntity(data, "owner_bientity_condition", attacker, target)) {
            Services.ACTION.executeBiEntity(data, "bientity_action", projectile, target);
            Services.ACTION.executeBiEntity(data, "owner_bientity_action", attacker, target);
            if (amountHit >= data.getInt("stop_after")) {
                this.use(power, powerHolder);
            }
        }
    }

}
