package com.github.merchantpug.apugli.mixin.xplatforn.common;

import com.github.merchantpug.apugli.access.ExplosionAccess;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.Set;

@Mixin(Explosion.class)
public abstract class ExplosionMixin implements ExplosionAccess {
    @Unique private boolean apugli$rocketJumpExplosion;
    @Unique private Entity apugli$affectedEntity;
    @Unique private List<Modifier> apugli$explosionDamageModifiers;

    @Shadow @Final private Level world;

    @Shadow public abstract @Nullable LivingEntity getCausingEntity();

    @ModifyArg(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private float changeDamage(float amount) {
        if(this.isRocketJump()) {
            return amount = (float) ModifierUtil.applyModifiers(this.getCausingEntity(), apugli$explosionDamageModifiers, amount);
        }
        return amount;
    }

    @ModifyArgs(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private void modifyOtherEntitiesKnockback(Args args) {
        if(this.isRocketJump()) {
            args.set(0, (double)args.get(0) * 0.75);
            args.set(1, (double)args.get(1) * 0.5);
            args.set(2, (double)args.get(2) * 0.75);
        }
    }

    @Inject(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isImmuneToExplosion()Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void collectAffectedEntity(CallbackInfo ci, Set set, int k, float f, int l, int m, int r, int s, int t, int u, List list, Vec3 vec3d, int v, Entity entity) {
        this.apugli$affectedEntity = entity;
    }

    @ModifyExpressionValue(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isImmuneToExplosion()Z"))
    private boolean cancelDamagedEntity(boolean original) {
        if(this.isRocketJump()) {
            return original || !(apugli$affectedEntity instanceof LivingEntity) || apugli$affectedEntity instanceof ArmorStand;
        }
        return original;
    }

    @ModifyArg(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"), index = 5)
    private float reduceVolumeOfRocketJump(float volume) {
        if(this.isRocketJump()) {
            return volume = 0.25F;
        }
        return volume;
    }

    @ModifyArg(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"), index = 6)
    private float increasePitchOfRocketJump(float pitch) {
        if(this.isRocketJump()) {
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


    @Override
    public void setExplosionDamageModifiers(List<Modifier> value) {
        this.apugli$explosionDamageModifiers = value;
    }
}
