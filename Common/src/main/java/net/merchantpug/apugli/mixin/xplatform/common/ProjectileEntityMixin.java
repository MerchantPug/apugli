package net.merchantpug.apugli.mixin.xplatform.common;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class ProjectileEntityMixin {
    @Shadow @Nullable public abstract Entity getOwner();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    private void handlePowerTick(CallbackInfo ci) {
        PowerHolderComponent.getPowers(this.getOwner(), ProjectileActionOverTimePower.class).forEach(power -> power.projectileTick((Projectile)(Object)this));
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
    private void handleProjectileCollision(HitResult hitResult, CallbackInfo ci) {
        PowerHolderComponent.getPowers(this.getOwner(), ActionOnProjectileHitPower.class).forEach(power -> power.onHit(((EntityHitResult)hitResult).getEntity(), (Projectile)(Object)this));
    }
}
