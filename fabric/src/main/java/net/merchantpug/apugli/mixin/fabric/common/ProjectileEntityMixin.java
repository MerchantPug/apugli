package net.merchantpug.apugli.mixin.fabric.common;

import net.merchantpug.apugli.access.ProjectileEntityAccess;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mixin(Projectile.class)
public abstract class ProjectileEntityMixin implements ProjectileEntityAccess {

    @Shadow @Nullable public abstract Entity getOwner();

    @Unique
    private final Map<ResourceLocation, Integer> apugli$entitiesHit = new HashMap<>();

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V"))
    private void apugli$handleProjectileCollision(HitResult hitResult, CallbackInfo ci) {
        if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity living)
            Services.POWER.getPowers(living, ApugliPowers.ACTION_WHEN_PROJECTILE_HIT.get()).forEach(power -> ApugliPowers.ACTION_WHEN_PROJECTILE_HIT.get().execute(power, living, (Projectile)(Object)this));
        if (hitResult instanceof EntityHitResult entityHitResult && this.getOwner() instanceof LivingEntity living)
            Services.POWER.getPowers(living, ApugliPowers.ACTION_ON_PROJECTILE_HIT.get()).forEach(power -> {
                this.apugli$entitiesHit.compute(Services.POWER.getPowerId(power), (resourceLocation, integer) -> integer != null ? integer + 1 : 1);
                ApugliPowers.ACTION_ON_PROJECTILE_HIT.get().execute(power, living, entityHitResult.getEntity(), (Projectile)(Object)this, this.apugli$entitiesHit.getOrDefault(Services.POWER.getPowerId(power), 0));
            });
    }

    @Override
    public Set<ResourceLocation> apugli$powersThatHaveLanded() {
        return apugli$entitiesHit.keySet();
    }

}
