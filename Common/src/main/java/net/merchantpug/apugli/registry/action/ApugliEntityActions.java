package net.merchantpug.apugli.registry.action;

import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.action.factory.entity.*;
import net.merchantpug.apugli.action.factory.entity.meta.PacketAction;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.Entity;

public class ApugliEntityActions {
    
    public static void registerAll() {
        register("add_velocity", new AddVelocityAction());
        register("custom_projectile", new CustomProjectileAction());
        register("explode", new ExplodeAction());
        register("fire_projectile", new FireProjectileAction());
        register("item_cooldown", new ItemCooldownAction());
        register("raycast", new RaycastAction());
        register("resource_transfer", new ResourceTransferAction());
        register("explosion_raycast", new ExplosionRaycastAction());
        register("packet", new PacketAction());
        register("set_no_gravity", new SetNoGravityAction());
        register("spawn_custom_effect_cloud", new SpawnCustomEffectCloudAction());
        register("spawn_item", new SpawnItemAction());
        register("spawn_particles", new SpawnParticlesAction());
        register("zombify_villager", new ZombifyVillagerAction());
    }
    
    private static void register(String name, IActionFactory<Entity> factory) {
        Services.ACTION.registerEntity(name, factory);
    }
    
}
