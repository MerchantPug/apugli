package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.List;

public class ForceParticleRenderPower extends Power {
    private final List<ParticleEffect> particles = new ArrayList<>();

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ForceParticleRenderPower>(Apugli.identifier("force_particle_render"),
                new SerializableData()
                        .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE, null)
                        .add("particles", SerializableDataType.list(SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE), null),
                        data ->
                        (type, entity) -> {
                            ForceParticleRenderPower power = new ForceParticleRenderPower(type, entity);
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

    public ForceParticleRenderPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }
}
