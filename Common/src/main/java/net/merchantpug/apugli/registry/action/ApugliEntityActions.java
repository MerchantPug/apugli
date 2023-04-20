package net.merchantpug.apugli.registry.action;

import net.merchantpug.apugli.action.factory.IActionFactory;
import com.github.merchantpug.apugli.action.factory.entity.*;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.action.factory.entity.*;
import net.minecraft.world.entity.Entity;

public class ApugliEntityActions {
    
    public static void registerAll() {
        register("explode", new ExplodeAction());
        register("fire_projectile", new FireProjectileAction());
        register("ray_cast", new RaycastAction());
        register("set_no_gravity", new SetNoGravityAction());
        register("spawn_item", new SpawnItemAction());
        register("zombify_villager", new ZombifyVillagerAction());
    }
    
    private static void register(String name, IActionFactory<Entity> factory) {
        Services.ACTION.registerEntity(name, factory);
    }
    
}
