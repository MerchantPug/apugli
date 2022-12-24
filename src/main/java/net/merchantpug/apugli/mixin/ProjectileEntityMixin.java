package net.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.power.ActionOnProjectileHitPower;
import net.merchantpug.apugli.power.ProjectileActionOverTimePower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {
    @Shadow @Nullable public abstract Entity getOwner();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V"))
    private void handlePowerTick(CallbackInfo ci) {
        PowerHolderComponent.getPowers(this.getOwner(), ProjectileActionOverTimePower.class).forEach(power -> power.projectileTick((ProjectileEntity)(Object)this));
    }

    @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;emitGameEvent(Lnet/minecraft/world/event/GameEvent;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/event/GameEvent$Emitter;)V"))
    private void handleProjectileCollision(HitResult hitResult, CallbackInfo ci) {
        PowerHolderComponent.getPowers(this.getOwner(), ActionOnProjectileHitPower.class).forEach(power -> power.onHit(((EntityHitResult)hitResult).getEntity(), (ProjectileEntity)(Object)this));
    }
}
