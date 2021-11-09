package io.github.merchantpug.apugli.mixin;

import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractDustParticleEffect.class)
public interface AbstractDustParticleEffectAccessor {
    @Accessor
    Vec3f getColor();

    @Accessor
    float getScale();
}
