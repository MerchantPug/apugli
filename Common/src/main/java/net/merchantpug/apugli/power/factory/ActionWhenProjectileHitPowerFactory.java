package net.merchantpug.apugli.power.factory;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

public interface ActionWhenProjectileHitPowerFactory<P> extends ProjectileHitActionPowerFactory<P> {

    default void execute(P power, LivingEntity entity, Projectile projectile) {
        if (!(projectile.getOwner() instanceof LivingEntity living)) return;
        this.execute(power, entity, living, entity, projectile, 1);
    }

}
