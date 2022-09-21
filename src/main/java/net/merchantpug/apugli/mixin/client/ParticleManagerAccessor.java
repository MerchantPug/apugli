package net.merchantpug.apugli.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Queue;

@Environment(EnvType.CLIENT)
@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {
    @Accessor
    Map<ParticleTextureSheet, Queue<Particle>> getParticles();
}
