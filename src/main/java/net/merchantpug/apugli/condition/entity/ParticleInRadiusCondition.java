package net.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ParticleAccess;
import net.merchantpug.apugli.mixin.client.ClientPlayerEntityAccessor;
import net.merchantpug.apugli.mixin.client.ParticleManagerAccessor;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;

import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class ParticleInRadiusCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if (!entity.world.isClient()) {
            return false;
        }
        List<ParticleEffect> particles = data.get("particles");
        int stopAt = -1;
        Comparison comparison = data.get("comparison");
        int compareTo = data.getInt("compare_to");
        switch (comparison) {
            case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN -> stopAt = compareTo + 1;
            case LESS_THAN, GREATER_THAN_OR_EQUAL -> stopAt = compareTo;
        }
        int count = 0;
        if (entity instanceof ClientPlayerEntity) {
            ParticleManager particleManager = ((ClientPlayerEntityAccessor)entity).getClient().particleManager;
            for (Queue<Particle> particleQueue : ((ParticleManagerAccessor)particleManager).getParticles().values()) {
                for (Particle particle : particleQueue.stream().filter(particle -> entity.getBoundingBox().expand(data.getDouble("radius")).intersects(particle.getBoundingBox())).toList()) {
                    if (data.isPresent("particle") && ((ParticleAccess)particle).getParticleEffect() == data.get("particle") || data.isPresent("particles") &&  particles.stream().anyMatch(particleEffect -> ((ParticleAccess)particle).getParticleEffect() == particleEffect)) {
                        count++;
                        if (count == stopAt) {
                            break;
                        }
                    }
                }
            }
            return comparison.compare(count, compareTo);
        }
        return false;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("particle_in_radius"), new SerializableData()
                .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE, null)
                .add("particles", SerializableDataType.list(SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE), null)
                .add("radius", SerializableDataTypes.DOUBLE)
                .add("compare_to", SerializableDataTypes.INT, 1)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
                ParticleInRadiusCondition::condition
        );
    }
}
