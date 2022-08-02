package com.github.merchantpug.apugli.mixin.client;

import com.github.merchantpug.apugli.power.ForceParticleRenderPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;
    @Unique private ParticleEffect particleParameters;
    @Unique private double particleX;
    @Unique private double particleY;
    @Unique private double particleZ;

    @ModifyExpressionValue(method = "tickRainSplashing", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;", ordinal = 1))
    private boolean allowForRainToSpawnIfMinimal(boolean original) {
        return false;
    }

    @Inject(method = "spawnParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "HEAD"))
    private void captureParticleParameters(ParticleEffect parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        this.particleParameters = parameters;
        this.particleX = x;
        this.particleY = y;
        this.particleZ = z;
    }

    @ModifyArg(method = "spawnParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;getRandomParticleSpawnChance(Z)Lnet/minecraft/client/option/ParticlesMode;"))
    private boolean forceParticleSpawn(boolean canSpawnOnMinimal) {
        if (PowerHolderComponent.getPowers(this.client.getCameraEntity(), ForceParticleRenderPower.class).stream().anyMatch(power -> power.doesApply(particleParameters) && client.gameRenderer.getCamera().getPos().squaredDistanceTo(particleX, particleY, particleZ) <= 1024.0D)) {
            return true;
        }
        return canSpawnOnMinimal;
    }
}
