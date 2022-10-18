package com.github.merchantpug.apugli.power;

import the.great.migration.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;

public class ForceParticleRenderPower extends Power {
    private final List<ParticleOptions> particles = new ArrayList<>();

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ForceParticleRenderPower>(Apugli.identifier("force_particle_render"),
                new SerializableData()
                        .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE, null)
                        .add("particles", SerializableDataType.list(SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE), null),
                        data ->
                        (type, entity) -> {
                            ForceParticleRenderPower power = new ForceParticleRenderPower(type, entity);
                            if(data.isPresent("particle")) {
                                power.addParticle((ParticleOptions)data.get("particle"));
                            }
                            if(data.isPresent("particles")) {
                                ((List<ParticleOptions>)data.get("particles")).forEach(power::addParticle);
                            }
                            return power;
                        })
                .allowCondition();
    }

    public boolean doesApply(ParticleOptions particle) {
        return particles.contains(particle);
    }

    public void addParticle(ParticleOptions particle) {
        this.particles.add(particle);
    }

    public ForceParticleRenderPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }
}
