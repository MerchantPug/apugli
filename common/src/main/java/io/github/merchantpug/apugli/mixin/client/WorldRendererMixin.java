package io.github.merchantpug.apugli.mixin.client;

import io.github.apace100.origins.component.OriginComponent;
import io.github.merchantpug.apugli.powers.ForceParticleRenderPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.ParticlesMode;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;
    @Unique private ParticleEffect particleParameters;
    @Unique private double particleX;
    @Unique private double particleY;
    @Unique private double particleZ;

    @Redirect(method = "tickRainSplashing", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/ParticlesMode;MINIMAL:Lnet/minecraft/client/options/ParticlesMode;"))
    private ParticlesMode allowForRainToSpawnIfMinimal() {
        return null;
    }

    @Inject(method = "spawnParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "HEAD"))
    private void captureParticleParameters(ParticleEffect parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        this.particleParameters = parameters;
        this.particleX = x;
        this.particleY = y;
        this.particleZ = z;
    }

    @ModifyArg(method = "spawnParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;getRandomParticleSpawnChance(Z)Lnet/minecraft/client/options/ParticlesMode;"))
    private boolean forceParticleSpawn(boolean canSpawnOnMinimal) {
        if (OriginComponent.getPowers(this.client.getCameraEntity(), ForceParticleRenderPower.class).stream().anyMatch(power -> power.doesApply(particleParameters) && client.gameRenderer.getCamera().getPos().squaredDistanceTo(particleX, particleY, particleZ) <= 1024.0D)) {
            return true;
        }
        return canSpawnOnMinimal;
    }
}