package net.merchantpug.apugli.power.factory;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface ActionOnAttackerHurtPowerFactory<P> extends TargetHurtActionPowerFactory<P> {

    default void execute(LivingEntity entity, DamageSource source, float amount) {
        this.execute(entity, entity.getLastHurtByMob(), entity, source, amount);
    }

}
