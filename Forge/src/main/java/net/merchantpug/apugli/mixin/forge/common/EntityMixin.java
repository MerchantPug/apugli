package net.merchantpug.apugli.mixin.forge.common;

import net.merchantpug.apugli.access.ProjectileEntityAccess;
import net.merchantpug.apugli.capability.entity.EntitiesHitCapability;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "discard", at = @At("HEAD"))
    private void apugli$startCooldownIfDiscarded(CallbackInfo ci) {
        if ((Entity)(Object)this instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity livingOwner) {
            var aophPowers = Services.POWER.getPowers(livingOwner, ApugliPowers.ACTION_ON_PROJECTILE_HIT.get(), true);
            aophPowers.forEach(power -> {
                if (ApugliPowers.ACTION_ON_PROJECTILE_HIT.get().canUse(power, livingOwner) && projectile.getCapability(EntitiesHitCapability.INSTANCE).map(cap -> cap.apugli$powersThatHaveLanded().contains(Services.POWER.getPowerId(power))).orElse(false)) {
                    ApugliPowers.ACTION_ON_PROJECTILE_HIT.get().use(power, livingOwner);
                }
            });
        }
    }
}
