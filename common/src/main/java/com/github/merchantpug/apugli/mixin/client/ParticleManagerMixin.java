package com.github.merchantpug.apugli.mixin.client;

import com.github.merchantpug.apugli.access.ParticleAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Shadow protected ClientWorld world;

    @Inject(method = "createParticle", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private <T extends ParticleEffect> void linkParticleEffectToParticleClass(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<@Nullable Particle> cir, ParticleFactory<T> factory) {
        if (factory == null) return;
        Particle particle = factory.createParticle(parameters, this.world, x, y, z, velocityX, velocityY, velocityZ);
        if (particle == null) return;
        ((ParticleAccess)particle).setParticleEffect(parameters);
        cir.setReturnValue(particle);
    }
}
