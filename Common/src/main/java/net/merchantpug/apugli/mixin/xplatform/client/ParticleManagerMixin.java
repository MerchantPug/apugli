package net.merchantpug.apugli.mixin.xplatform.client;

import net.merchantpug.apugli.access.ParticleAccess;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ParticleEngine.class)
public class ParticleManagerMixin {
    @Shadow protected ClientLevel level;

    @Inject(method = "createParticle", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private <T extends ParticleOptions> void linkParticleEffectToParticleClass(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<@Nullable Particle> cir, ParticleProvider<T> factory) {
        if(factory == null) return;
        Particle particle = factory.createParticle(parameters, this.world, x, y, z, velocityX, velocityY, velocityZ);
        if(particle == null) return;
        ((ParticleAccess)particle).setParticleEffect(parameters);
        cir.setReturnValue(particle);
    }
}
