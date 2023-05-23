package net.merchantpug.apugli.registry.condition;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.condition.factory.entity.*;
import net.minecraft.world.entity.Entity;

public class ApugliEntityConditions {
    
    public static void registerAll() {
        register("attacker_condition", new AttackerConditionCondition());
        register("attack_target_condition", new AttackTargetConditionCondition());
        register("base_enchantment", new BaseEnchantmentCondition());
        register("can_have_effect", new CanHaveEffectCondition());
        register("can_take_damage", new CanTakeDamageCondition());
        register("compare_resource", new CompareResourceCondition());
        register("entity_in_radius", new EntityInRadiusCondition());
        register("hostile", new HostileCondition());
        register("join_invulnerability_ticks", new JoinInvulnerabilityTicksCondition());
        register("key_pressed", new KeyPressedCondition());
        register("max_health", new MaxHealthCondition());
        register("moving", new MovingCondition());
        register("particle_in_radius", new ParticleInRadiusCondition());
        register("player_model_type", new PlayerModelTypeCondition());
        register("raycast", new RaycastCondition());
        register("structure", new StructureCondition());
        register("trident_enchantment", new TridentEnchantmentCondition());
        register("velocity", new VelocityCondition());
    }
    
    private static void register(String name, IConditionFactory<Entity> action) {
        Services.CONDITION.registerEntity(name, action);
    }
    
}
