package com.github.merchantpug.apugli.mixin.xplatforn.client;

import com.github.merchantpug.apugli.access.ParticleAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(Particle.class)
public class ParticleMixin implements ParticleAccess {
    @Unique private ParticleOptions particleEffect;

    @Override
    public ParticleOptions getParticleEffect() {
        return this.particleEffect;
    }

    @Override
    public void setParticleEffect(ParticleOptions effect) {
        this.particleEffect = effect;
    }
}
