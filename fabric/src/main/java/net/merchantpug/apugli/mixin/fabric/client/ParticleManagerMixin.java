package net.merchantpug.apugli.mixin.fabric.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.merchantpug.apugli.access.ParticleAccess;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
public class ParticleManagerMixin {
    @Shadow protected ClientLevel level;

    @Unique
    private ParticleOptions apugli$particleData;

    @Inject(method = "makeParticle", at = @At(value = "RETURN", ordinal = 1))
    private <T extends ParticleOptions> void apugli$captureParticleEffect(T particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> cir) {
        this.apugli$particleData = particleData;
    }

    @ModifyReturnValue(method = "makeParticle", at = @At(value = "RETURN", ordinal = 1))
    private <T extends ParticleOptions> Particle apugli$linkParticleEffectToParticleClass(Particle particle) {
        if (particle != null) {
            ((ParticleAccess)particle).apugli$setParticleEffect(apugli$particleData);
        }
        this.apugli$particleData = null;
        return particle;
    }
}
