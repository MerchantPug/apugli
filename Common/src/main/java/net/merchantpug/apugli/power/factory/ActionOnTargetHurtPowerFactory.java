package net.merchantpug.apugli.power.factory;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface ActionOnTargetHurtPowerFactory<P> extends TargetHurtActionPowerFactory<P> {

    default void execute(LivingEntity entity, DamageSource source, float amount) {
        this.execute(entity.getLastHurtByMob(), entity.getLastHurtByMob(), entity, source, amount);
    }

}
