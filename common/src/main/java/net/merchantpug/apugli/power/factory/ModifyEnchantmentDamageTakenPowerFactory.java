package net.merchantpug.apugli.power.factory;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface ModifyEnchantmentDamageTakenPowerFactory<P> extends ModifyEnchantmentDamagePowerFactory<P> {

    default float applyModifiers(LivingEntity entity, DamageSource source, LivingEntity attacker, float originalAmount) {
        return this.applyModifiers(entity, source, originalAmount, attacker, entity);
    }

}
