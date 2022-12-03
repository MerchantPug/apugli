package net.merchantpug.apugli.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.merchantpug.apugli.access.ParticleAccess;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(Particle.class)
@Implements(@Interface(iface = ParticleAccess.class, prefix = "apugli$"))
public class ParticleMixin {
    @Unique private ParticleEffect particleEffect;

    public ParticleEffect apugli$getParticleEffect() {
        return this.particleEffect;
    }

    public void apugli$setParticleEffect(ParticleEffect effect) {
        this.particleEffect = effect;
    }
}
