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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private Minecraft minecraft;
    @Unique private ParticleOptions apugli$particleParameters;
    @Unique private double apugli$particleX;
    @Unique private double apugli$particleY;
    @Unique private double apugli$particleZ;

    @Redirect(method = "tickRain", at = @At(value = "FIELD", target = "Lnet/minecraft/client/ParticleStatus;MINIMAL:Lnet/minecraft/client/ParticleStatus;"))
    private ParticleStatus apugli$allowForRainToSpawnIfMinimal() {
        return null;
    }

    @Inject(method = "addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "HEAD"))
    private void apugli$captureParticleParameters(ParticleOptions parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        this.apugli$particleParameters = parameters;
        this.apugli$particleX = x;
        this.apugli$particleY = y;
        this.apugli$particleZ = z;
    }

    @ModifyArg(method = "addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;calculateParticleLevel(Z)Lnet/minecraft/client/ParticleStatus;"))
    private boolean apugli$forceParticleSpawn(boolean canSpawnOnMinimal) {
        if(this.minecraft.getCameraEntity() instanceof LivingEntity living && Services.POWER.getPowers(living, ApugliPowers.FORCE_PARTICLE_RENDER.get()).stream().anyMatch(power -> power.doesApply(apugli$particleParameters) && minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(apugli$particleX, apugli$particleY, apugli$particleZ) <= 1024.0D)) {
            this.apugli$particleParameters = null;
            return true;
        }
        return canSpawnOnMinimal;
    }
}
