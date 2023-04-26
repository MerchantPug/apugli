package net.merchantpug.apugli.mixin.xplatform.client;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private Minecraft minecraft;
    @Unique private ParticleOptions particleParameters;
    @Unique private double particleX;
    @Unique private double particleY;
    @Unique private double particleZ;

    @Redirect(method = "tickRain", at = @At(value = "FIELD", target = "Lnet/minecraft/client/ParticleStatus;MINIMAL:Lnet/minecraft/client/ParticleStatus;"))
    private ParticleStatus allowForRainToSpawnIfMinimal() {
        return null;
    }

    @Inject(method = "addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "HEAD"))
    private void captureParticleParameters(ParticleOptions parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        this.particleParameters = parameters;
        this.particleX = x;
        this.particleY = y;
        this.particleZ = z;
    }

    @ModifyArg(method = "addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;calculateParticleLevel(Z)Lnet/minecraft/client/ParticleStatus;"))
    private boolean forceParticleSpawn(boolean canSpawnOnMinimal) {
        if(this.minecraft.getCameraEntity() instanceof LivingEntity living && Services.POWER.getPowers(living, ApugliPowers.FORCE_PARTICLE_RENDER.get()).stream().anyMatch(power -> power.doesApply(particleParameters) && minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(particleX, particleY, particleZ) <= 1024.0D)) {
            return true;
        }
        return canSpawnOnMinimal;
    }
}
