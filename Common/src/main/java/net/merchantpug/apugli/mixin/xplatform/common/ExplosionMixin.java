package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.access.ExplosionAccess;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.apace100.apoli.util.modifier.Modifier;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(Explosion.class)
@Implements(@Interface(iface = ExplosionAccess.class, prefix = "apugli$"))
public abstract class ExplosionMixin {
    @Unique private boolean apugli$rocketJumpExplosion;
    @Unique private List<?> apugli$explosionDamageModifiers;
    @Unique private Predicate<Tuple<Entity, Entity>> apugli$rocketJumpBiEntityCondition;

    @Shadow @Final private Level level;

    @Shadow public abstract @Nullable LivingEntity getSourceMob();

    @Shadow @Final private double z;

    @ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private float changeDamage(float amount) {
        if (this.apugli$isRocketJump()) {
            return amount = (float) Services.PLATFORM.applyModifiers(this.getSourceMob(), apugli$explosionDamageModifiers, amount);
        }
        return amount;
    }

    @ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"), index = 0)
    private double modifyOtherEntitiesKnockbackX(double x) {
        if (this.apugli$isRocketJump()) {
            return x * 0.75;
        }
        return x;
    }

    @ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"), index = 1)
    private double modifyOtherEntitiesKnockbackY(double y) {
        if (this.apugli$isRocketJump()) {
            return y * 0.75;
        }
        return y;
    }


    @ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"), index = 2)
    private double modifyOtherEntitiesKnockbackZ(double z) {
        if (this.apugli$isRocketJump()) {
            return z * 0.75;
        }
        return z;
    }

    @ModifyArg(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"), index = 5)
    private float reduceVolumeOfRocketJump(float volume) {
        if (this.apugli$isRocketJump()) {
            return volume = 0.25F;
        }
        return volume;
    }

    @ModifyArg(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"), index = 6)
    private float increasePitchOfRocketJump(float pitch) {
        if (this.apugli$isRocketJump()) {
            return pitch = (1.4f + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2f) * 0.7f;
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
        if (((ExplosionAccess)(Object)this).isRocketJump()) {
            return original || this.getSourceMob() != null && ((ExplosionAccess)(Object)this).getBiEntityPredicate() != null && !((ExplosionAccess)(Object)this).getBiEntityPredicate().test(new Tuple<>(this.getSourceMob(), this.apugli$affectedEntity));
        }
        return original;
    }

    public void apugli$setRocketJump(boolean value) {
        this.apugli$rocketJumpExplosion = value;
    }

    public boolean apugli$isRocketJump() {
        return this.apugli$rocketJumpExplosion;
    }

    public void apugli$setExplosionDamageModifiers(List<Modifier> value) {
        this.apugli$explosionDamageModifiers = value;
    }

    public List<?> apugli$getExplosionDamageModifiers() {
        return apugli$explosionDamageModifiers;
    }

    public void apugli$setBiEntityPredicate(@Nullable Predicate<Tuple<Entity, Entity>> value) {
        this.apugli$rocketJumpBiEntityCondition = value;
    }

    public Predicate<Tuple<Entity, Entity>> getBiEntityPredicate() {
        return apugli$rocketJumpBiEntityCondition;
    }
}