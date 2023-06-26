package net.merchantpug.apugli.condition.factory.entity;

import net.merchantpug.apugli.access.ParticleAccess;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.mixin.xplatform.client.accessor.ParticleEngineAccessor;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

public class ParticleInRadiusCondition implements IConditionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE, null)
            .add("particles", SerializableDataType.list(SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE), null)
            .add("radius", SerializableDataTypes.DOUBLE)
            .add("compare_to", SerializableDataTypes.INT, 1)
            .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL);
    }
    
    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        if(!entity.level().isClientSide()) return false;
        //Prepare for particle type check
        Predicate<ParticleOptions> particleFilter;
        List<ParticleOptions> particleOptions = new ArrayList<>();
        if(data.isPresent("particle")) particleOptions.add(data.get("particle"));
        if(data.isPresent("particles")) particleOptions.addAll(data.get("particles"));
        if(particleOptions.isEmpty()) return false;
        if(particleOptions.size() == 1) {
            ParticleOptions particleOption = particleOptions.get(0);
            //Use Object#equals rather than Collections#contains for optimization
            particleFilter = particleOption::equals;
        } else particleFilter = particleOptions::contains;
        //Prepare for particle count check
        Comparison comparison = data.get("comparison");
        int compareTo = data.getInt("compare_to");
        int stopAt = switch(comparison) {
            case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN -> compareTo + 1;
            case LESS_THAN, GREATER_THAN_OR_EQUAL -> compareTo;
            case NONE, NOT_EQUAL -> -1;
        };
        //Count all match until stop at proper point
        int count = 0;
        AABB aabb = entity.getBoundingBox().inflate(data.getDouble("radius"));
        ParticleEngineAccessor particleEngine = (ParticleEngineAccessor) Minecraft.getInstance().particleEngine;
        for(Queue<Particle> particleQueue : particleEngine.getParticles().values()) {
            for(Particle particle : particleQueue) {
                if(particleFilter.test(((ParticleAccess)particle).getParticleEffect()) &&
                    aabb.intersects(particle.getBoundingBox())
                ) {
                    count++;
                    if(count == stopAt) {
                        break;
                    }
                }
            }
        }
        return comparison.compare(count, compareTo);
    }

}
