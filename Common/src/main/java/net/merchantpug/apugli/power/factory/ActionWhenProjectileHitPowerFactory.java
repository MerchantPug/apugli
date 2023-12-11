package net.merchantpug.apugli.power.factory;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

public interface ActionWhenProjectileHitPowerFactory<P> extends ProjectileHitActionPowerFactory<P> {

    default void execute(P power, LivingEntity entity, Projectile projectile) {
        this.execute(power, entity, projectile.getOwner(), entity, projectile, getDataFromPower(power).getInt("stop_after"));
    }

}
