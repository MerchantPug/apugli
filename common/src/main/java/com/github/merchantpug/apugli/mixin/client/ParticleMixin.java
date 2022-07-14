package com.github.merchantpug.apugli.mixin.client;

import com.github.merchantpug.apugli.access.ParticleAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(Particle.class)
public class ParticleMixin implements ParticleAccess {
    @Unique
    private ParticleEffect particleEffect;

    @Override
    public ParticleEffect getParticleEffect() {
        return this.particleEffect;
    }

    @Override
    public void setParticleEffect(ParticleEffect effect) {
        this.particleEffect = effect;
    }
}