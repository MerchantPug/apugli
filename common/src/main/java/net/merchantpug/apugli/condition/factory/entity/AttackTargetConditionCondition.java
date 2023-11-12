package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AttackTargetConditionCondition extends AttackConditionsCondition {

    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        if (entity instanceof LivingEntity living && living.getLastHurtMob() != null) {
            return check(data, living, living.getLastHurtMob());
        }
        return false;
    }

}
