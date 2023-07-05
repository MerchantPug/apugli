package net.merchantpug.apugli.registry;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.entity.CustomProjectile;
import net.merchantpug.apugli.registry.services.RegistrationProvider;
import net.merchantpug.apugli.registry.services.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class ApugliEntityTypes {
    private static final RegistrationProvider<EntityType<?>> ENTITY_TYPES = RegistrationProvider.get(Registry.ENTITY_TYPE, Apugli.ID);

    public static final RegistryObject<EntityType<CustomProjectile>> CUSTOM_PROJECTILE = register("custom_projectile", () -> EntityType.Builder.<CustomProjectile>of(CustomProjectile::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(64).updateInterval(10).build("apugli:custom_projectile"));

    public static void registerAll() {

    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, Supplier<EntityType<T>> entityType) {
        return ENTITY_TYPES.register(name, entityType);
    }
}
