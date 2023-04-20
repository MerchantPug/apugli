package net.merchantpug.apugli.mixin.xplatform.client;

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/client/ParticleMixin.java
package net.merchantpug.apugli.mixin.client;
========
package com.github.merchantpug.apugli.mixin.xplatforn.client;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/client/ParticleMixin.java

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.merchantpug.apugli.access.ParticleAccess;
import net.minecraft.client.particle.Particle;
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/client/ParticleMixin.java
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
========
import net.minecraft.core.particles.ParticleOptions;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/client/ParticleMixin.java
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(Particle.class)
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/client/ParticleMixin.java
@Implements(@Interface(iface = ParticleAccess.class, prefix = "apugli$"))
public class ParticleMixin {
    @Unique private ParticleEffect particleEffect;

    public ParticleEffect apugli$getParticleEffect() {
        return this.particleEffect;
    }

    public void apugli$setParticleEffect(ParticleEffect effect) {
========
public class ParticleMixin implements ParticleAccess {
    @Unique private ParticleOptions particleEffect;

    @Override
    public ParticleOptions getParticleEffect() {
        return this.particleEffect;
    }

    @Override
    public void setParticleEffect(ParticleOptions effect) {
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/client/ParticleMixin.java
        this.particleEffect = effect;
    }
}
