package net.merchantpug.apugli.mixin.xplatform.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.merchantpug.apugli.access.ExplosionAccess;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;

@Mixin(Explosion.class)
@Implements(@Interface(iface = ExplosionAccess.class, prefix = "apugli$"))
public abstract class ExplosionMixin {
    @Shadow @Nullable public abstract Entity getDirectSourceEntity();

    @Unique private List<?> apugli$explosionDamageModifiers;
    @Unique private List<?> apugli$explosionKnockbackModifiers;
    @Unique private List<?> apugli$explosionVolumeModifiers;
    @Unique private List<?> apugli$explosionPitchModifiers;
    @Unique private Object apugli$rocketJumpBiEntityCondition;

    @ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private float changeDamage(float amount) {
        if (this.apugli$explosionDamageModifiers != null) {
            float returnValue = (float) Services.PLATFORM.applyModifiers(this.getDirectSourceEntity(), apugli$explosionDamageModifiers, amount);
            this.apugli$explosionDamageModifiers = null;
            return returnValue;
        }
        return amount;
    }

    @ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V", ordinal = 2), index = 0)
    private double modifyOtherEntitiesKnockbackX(double x) {
        if (this.apugli$explosionKnockbackModifiers != null) {
            return x = (float) Services.PLATFORM.applyModifiers(this.getDirectSourceEntity(), apugli$explosionKnockbackModifiers, x);
        }
        return x;
    }

    @ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V", ordinal = 2), index = 1)
    private double modifyOtherEntitiesKnockbackY(double y) {
        if (this.apugli$explosionKnockbackModifiers != null) {
            return y = (float) Services.PLATFORM.applyModifiers(this.getDirectSourceEntity(), apugli$explosionKnockbackModifiers, y);
        }
        return y;
    }


    @ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V", ordinal = 2), index = 2)
    private double modifyOtherEntitiesKnockbackZ(double z) {
        if (this.apugli$explosionKnockbackModifiers != null) {
            double newReturn = (float) Services.PLATFORM.applyModifiers(this.getDirectSourceEntity(), apugli$explosionKnockbackModifiers, z);
            this.apugli$explosionKnockbackModifiers = null;
            return newReturn;
        }
        return z;
    }

    @ModifyArg(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"), index = 5)
    private float reduceVolumeOfRocketJump(float volume) {
        if (this.apugli$explosionVolumeModifiers != null) {
            float newReturn = (float) Services.PLATFORM.applyModifiers(this.getDirectSourceEntity(), apugli$explosionVolumeModifiers, volume);
            this.apugli$explosionVolumeModifiers = null;
            return newReturn;
        }
        return volume;
    }

    @ModifyArg(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"), index = 6)
    private float increasePitchOfRocketJump(float pitch) {
        if (this.apugli$explosionPitchModifiers != null) {
            return pitch = (float) Services.PLATFORM.applyModifiers(this.getDirectSourceEntity(), apugli$explosionPitchModifiers, pitch);
        }
        return pitch;
    }

    @Unique
    private Entity apugli$affectedEntity;

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;ignoreExplosion()Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void collectAffectedEntity(CallbackInfo ci, Set set, int i, float f2, int k1, int l1, int i2, int i1, int j2, int j1, List list, Vec3 vec3, int k2, Entity entity) {
        this.apugli$affectedEntity = entity;
    }

    @ModifyExpressionValue(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;ignoreExplosion()Z"))
    private boolean cancelDamagedEntity(boolean original) {
        if (this.getDirectSourceEntity() != null && ((ExplosionAccess) this).getBiEntityPredicate() != null && !Services.CONDITION.checkBiEntity(((ExplosionAccess) this).getBiEntityPredicate(), this.getDirectSourceEntity(), this.apugli$affectedEntity))  {
            this.apugli$affectedEntity = null;
            return true;
        }
        return original;
    }

    public void apugli$setExplosionDamageModifiers(List<?> value) {
        this.apugli$explosionDamageModifiers = value;
    }

    public List<?> apugli$getExplosionDamageModifiers() {
        return this.apugli$explosionDamageModifiers;
    }

    public void apugli$setExplosionKnockbackModifiers(List<?> value) {
        this.apugli$explosionKnockbackModifiers = value;
    }

    public List<?> apugli$getExplosionKnockbackModifiers() {
        return this.apugli$explosionKnockbackModifiers;
    }

    public <M> void apugli$setBiEntityPredicate(@Nullable M value) {
        this.apugli$rocketJumpBiEntityCondition = value;
    }

    @Nullable public Object apugli$getBiEntityPredicate() {
        return this.apugli$rocketJumpBiEntityCondition;
    }

    public void apugli$setExplosionVolumeModifiers(List<?> value) {
        this.apugli$explosionVolumeModifiers = value;
    }

    public List<?> apugli$getExplosionVolumeModifiers() {
        return this.apugli$explosionVolumeModifiers;
    }

    public void apugli$setExplosionPitchModifiers(List<?> value) {
        this.apugli$explosionPitchModifiers = value;
    }

    public List<?> apugli$getExplosionPitchModifiers() {
        return this.apugli$explosionPitchModifiers;
    }

}