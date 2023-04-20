package net.merchantpug.apugli.mixin.xplatform.client;

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/client/WorldRendererMixin.java
package net.merchantpug.apugli.mixin.client;

import net.merchantpug.apugli.power.ForceParticleRenderPower;
========
package com.github.merchantpug.apugli.mixin.xplatforn.client;

import the.great.migration.merchantpug.apugli.power.ForceParticleRenderPower;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/client/WorldRendererMixin.java
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private Minecraft client;
    @Unique private ParticleOptions particleParameters;
    @Unique private double particleX;
    @Unique private double particleY;
    @Unique private double particleZ;

    @Redirect(method = "tickRainSplashing", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/ParticlesMode;MINIMAL:Lnet/minecraft/client/option/ParticlesMode;"))
    private ParticleStatus allowForRainToSpawnIfMinimal() {
        return null;
    }

    @Inject(method = "spawnParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "HEAD"))
    private void captureParticleParameters(ParticleOptions parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        this.particleParameters = parameters;
        this.particleX = x;
        this.particleY = y;
        this.particleZ = z;
    }

    @ModifyArg(method = "spawnParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;getRandomParticleSpawnChance(Z)Lnet/minecraft/client/option/ParticlesMode;"))
    private boolean forceParticleSpawn(boolean canSpawnOnMinimal) {
        if(PowerHolderComponent.getPowers(this.client.getCameraEntity(), ForceParticleRenderPower.class).stream().anyMatch(power -> power.doesApply(particleParameters) && client.gameRenderer.getMainCamera().getPosition().distanceToSqr(particleX, particleY, particleZ) <= 1024.0D)) {
            return true;
        }
        return canSpawnOnMinimal;
    }
}
