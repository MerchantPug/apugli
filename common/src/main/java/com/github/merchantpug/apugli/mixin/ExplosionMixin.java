package com.github.merchantpug.apugli.mixin;

import com.github.merchantpug.apugli.access.ExplosionAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


@Mixin(Explosion.class)
public abstract class ExplosionMixin implements ExplosionAccess {
    @Unique
    private boolean apugli$rocketJumpExplosion;

    @Shadow @Final
    private World world;

    @ModifyArg(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private float changeDamage(float amount) {
        if (this.isRocketJump()) {
            return amount = amount * 0.5F;
        }
        return amount;
    }

    @ModifyArgs(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private void modifyOtherEntitiesKnockback(Args args) {
        if (this.isRocketJump()) {
            args.set(0, (double)args.get(0) * 0.75);
            args.set(1, (double)args.get(1) * 0.5);
            args.set(2, (double)args.get(2) * 0.75);
        }
    }

    @Redirect(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isImmuneToExplosion()Z"))
    private boolean cancelDamagedEntity(Entity instance) {
        if (this.isRocketJump()) {
            return instance.isImmuneToExplosion() || !(instance instanceof LivingEntity) || instance instanceof ArmorStandEntity;
        }
        return instance.isImmuneToExplosion();
    }

    @ModifyArg(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"), index = 5)
    private float reduceVolumeOfRocketJump(float volume) {
        if (this.isRocketJump()) {
            return volume = 0.25F;
        }
        return volume;
    }

    @ModifyArg(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"), index = 6)
    private float increasePitchOfRocketJump(float pitch) {
        if (this.isRocketJump()) {
            return pitch = (1.4f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f;
        }
        return pitch;
    }

    @Override
    public void setRocketJump(boolean value) {
        this.apugli$rocketJumpExplosion = value;
    }

    @Override
    public boolean isRocketJump() {
        return this.apugli$rocketJumpExplosion;
    }
}
