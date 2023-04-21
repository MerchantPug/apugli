package net.merchantpug.apugli.power.factory;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

public interface ActionOnProjectileHitPowerFactory<P> extends ProjectileHitActionPowerFactory<P> {

    default void execute(P power, LivingEntity entity, Entity target, Projectile projectile) {
        this.execute(power, entity, entity, target, projectile);
    }

}
