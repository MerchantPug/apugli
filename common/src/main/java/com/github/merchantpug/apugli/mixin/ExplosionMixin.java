package com.github.merchantpug.apugli.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow public abstract DamageSource getDamageSource();

    @ModifyArg(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private float changeDamage(float amount) {
        if (getDamageSource().getName().equals("jumpExplosion") || getDamageSource().getName().equals("jumpExplosion.player")) {
            return amount = amount * 0.5F;
        }
        return amount;
    }
}
