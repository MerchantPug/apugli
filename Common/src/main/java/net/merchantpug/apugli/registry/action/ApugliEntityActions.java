package net.merchantpug.apugli.registry.action;

import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.action.factory.entity.*;
import net.minecraft.world.entity.Entity;

public class ApugliEntityActions {
    
    public static void registerAll() {
        register("clamped_add_velocity", new ClampedAddVelocityAction());
        register("explode", new ExplodeAction());
        register("fire_projectile", new FireProjectileAction());
        register("raycast", new RaycastAction());
        register("resource_transfer", new ResourceTransferAction());
        register("rocket_jump_raycast", new RocketJumpRaycastAction());
        register("set_no_gravity", new SetNoGravityAction());
        register("spawn_item", new SpawnItemAction());
        register("spawn_particles", new SpawnParticlesAction());
        register("zombify_villager", new ZombifyVillagerAction());
    }
    
    private static void register(String name, IActionFactory<Entity> factory) {
        Services.ACTION.registerEntity(name, factory);
    }
    
}
