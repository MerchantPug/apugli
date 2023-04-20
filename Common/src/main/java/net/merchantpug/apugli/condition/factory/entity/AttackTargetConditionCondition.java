package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AttackTargetConditionCondition extends AttackConditionsCondition {

    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        if (entity instanceof LivingEntity living && living.getLastHurtByMob() != null) {
            return check(data, living.getLastHurtByMob(), living);
        }
        return false;
    }

}
