package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.BackportedDataTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.List;

public class ForceParticleRenderPower extends Power {
    private final List<ParticleEffect> particles = new ArrayList<>();

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ForceParticleRenderPower>(Apugli.identifier("force_particle_render"),
                new SerializableData()
                        .add("particle", BackportedDataTypes.PARTICLE_EFFECT_OR_TYPE, null)
                        .add("particles", SerializableDataType.list(BackportedDataTypes.PARTICLE_EFFECT_OR_TYPE), null),
                data ->
                        (type, player) -> {
                            ForceParticleRenderPower power = new ForceParticleRenderPower(type, player);
                            if(data.isPresent("particle")) {
                                power.addParticle((ParticleEffect)data.get("particle"));
                            }
                            if(data.isPresent("particles")) {
                                ((List<ParticleEffect>)data.get("particles")).forEach(power::addParticle);
                            }
                            return power;
                        })
                .allowCondition();
    }

    public boolean doesApply(ParticleEffect particle) {
        return particles.contains(particle);
    }

    public void addParticle(ParticleEffect particle) {
        this.particles.add(particle);
    }

    public ForceParticleRenderPower(PowerType<?> type, PlayerEntity player) {
        super(type, player);
    }
}
