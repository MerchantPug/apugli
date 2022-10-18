package com.github.merchantpug.apugli.access;

import net.minecraft.core.particles.ParticleOptions;

public interface ParticleAccess {
    ParticleOptions getParticleEffect();
    void setParticleEffect(ParticleOptions effect);
}
