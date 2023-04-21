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
    @Unique private Entity apugli$affectedEntity;
    @Unique private List<?> apugli$explosionDamageModifiers;
    @Unique private Predicate<Tuple<Entity, Entity>> apugli$rocketJumpBiEntityCondition;

    @Shadow @Final private Level level;

    @Shadow public abstract @Nullable LivingEntity getSourceMob();

    @ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private float changeDamage(float amount) {
        if (this.apugli$isRocketJump()) {
            return amount = (float) Services.PLATFORM.applyModifiers(this.getSourceMob(), apugli$explosionDamageModifiers, amount);
        }
        return amount;
    }

    @ModifyArgs(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private void modifyOtherEntitiesKnockback(Args args) {
        if (this.apugli$isRocketJump()) {
            args.set(0, (double)args.get(0) * 0.75);
            args.set(1, (double)args.get(1) * 0.5);
            args.set(2, (double)args.get(2) * 0.75);
        }
    }

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;ignoreExplosion()Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void collectAffectedEntity(CallbackInfo ci, Set $$0, float $$18, int $$19, int $$20, int $$21, int $$22, int $$23, int $$24, List $$25, Vec3 $$26, int $$27, Entity entity) {
        this.apugli$affectedEntity = entity;
    }

    @ModifyExpressionValue(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;ignoreExplosion()Z"))
    private boolean cancelDamagedEntity(boolean original) {
        if (this.apugli$isRocketJump()) {
            return original || this.getSourceMob() != null && apugli$rocketJumpBiEntityCondition != null && !apugli$rocketJumpBiEntityCondition.test(new Tuple<>(this.getSourceMob(), this.apugli$affectedEntity));
        }
        return original;
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

    public void apugli$setRocketJump(boolean value) {
        this.apugli$rocketJumpExplosion = value;
    }

    public boolean apugli$isRocketJump() {
        return this.apugli$rocketJumpExplosion;
    }

    public void apugli$setExplosionDamageModifiers(List<Modifier> value) {
        this.apugli$explosionDamageModifiers = value;
    }

    public void apugli$setBiEntityPredicate(Predicate<Tuple<Entity, Entity>> value) {
        this.apugli$rocketJumpBiEntityCondition = value;
    }
}