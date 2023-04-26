package net.merchantpug.apugli.power.factory;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface ActionWhenHarmedPowerFactory<P> extends HarmActionPowerFactory<P> {

    default void execute(P power, LivingEntity entity, DamageSource source, float amount) {
        this.execute(power, entity, source, amount, source.getEntity(), entity);
    }

}
