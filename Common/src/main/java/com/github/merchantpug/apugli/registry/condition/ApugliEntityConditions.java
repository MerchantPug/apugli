package com.github.merchantpug.apugli.registry.condition;

import com.github.merchantpug.apugli.condition.factory.IConditionFactory;
import com.github.merchantpug.apugli.condition.factory.entity.*;
import com.github.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.Entity;

public class ApugliEntityConditions {
    
    public static void registerAll() {
        register("can_have_effect", new CanHaveEffectCondition());
        register("compare_resource", new CompareResourceCondition());
        register("entity_in_radius", new EntityInRadiusCondition());
        register("hostile", new HostileCondition());
        register("join_invulnerability_ticks", new JoinInvulnerabilityTicksCondition());
        register("key_pressed", new KeyPressedCondition());
        register("particle_in_radius", new ParticleInRadiusCondition());
        register("player_model_type", new PlayerModelTypeCondition());
        register("raycast", new RaycastCondition());
        register("structure", new StructureCondition());
        register("velocity", new VelocityCondition());
    }
    
    private static void register(String name, IConditionFactory<Entity> action) {
        Services.CONDITION.registerEntity(name, action);
    }
    
}
