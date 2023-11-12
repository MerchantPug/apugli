package net.merchantpug.apugli.mixin.xplatform.client;

import net.merchantpug.apugli.access.ParticleAccess;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Particle.class)
public class ParticleMixin implements ParticleAccess {
    @Unique private ParticleOptions apugli$particleEffect;

    public ParticleOptions apugli$getParticleEffect() {
        return this.apugli$particleEffect;
    }

    public void apugli$setParticleEffect(ParticleOptions effect) {
        this.apugli$particleEffect = effect;
    }

}
