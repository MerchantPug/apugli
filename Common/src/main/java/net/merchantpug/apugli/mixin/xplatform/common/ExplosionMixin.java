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

    @Shadow public abstract @Nullable Entity getDirectSourceEntity();

    @ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private float changeDamage(float amount) {
        if (this.apugli$isRocketJump()) {
            return amount = (float) Services.PLATFORM.applyModifiers(this.getDirectSourceEntity(), apugli$explosionDamageModifiers, amount);
        }
        return amount;
    }

    @ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 modifyOtherEntitiesKnockbackZ(Vec3 original) {
        if (this.apugli$isRocketJump()) {
            return new Vec3(original.x() * 0.75, original.y() * 0.75, original.z() * 0.75);
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



    @Unique
    private Entity apugli$affectedEntity;

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;ignoreExplosion()Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void collectAffectedEntity(CallbackInfo ci, Set set, float q, int k, int l, int r, int s, int t, int u, List list, Vec3 vec3, int v, Entity entity) {
        this.apugli$affectedEntity = entity;
    }

    @ModifyExpressionValue(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;ignoreExplosion()Z"))
    private boolean cancelDamagedEntity(boolean original) {
        if (((ExplosionAccess)(Object)this).isRocketJump()) {
            return original || this.getDirectSourceEntity() != null && ((ExplosionAccess)(Object)this).getBiEntityPredicate() != null && !((ExplosionAccess)(Object)this).getBiEntityPredicate().test(new Tuple<>(this.getDirectSourceEntity(), this.apugli$affectedEntity));
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

    public Predicate<Tuple<Entity, Entity>> apugli$getBiEntityPredicate() {
        return apugli$rocketJumpBiEntityCondition;
    }
}