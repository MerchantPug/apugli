package net.merchantpug.apugli.registry;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.entity.CustomAreaEffectCloud;
import net.merchantpug.apugli.entity.CustomProjectile;
import net.merchantpug.apugli.registry.services.RegistrationProvider;
import net.merchantpug.apugli.registry.services.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class ApugliEntityTypes {
    private static final RegistrationProvider<EntityType<?>> ENTITY_TYPES = RegistrationProvider.get(BuiltInRegistries.ENTITY_TYPE, Apugli.ID);

    public static final RegistryObject<EntityType<CustomAreaEffectCloud>> CUSTOM_AREA_EFFECT_CLOUD = register("custom_area_effect_cloud", () -> EntityType.Builder.<CustomAreaEffectCloud>of(CustomAreaEffectCloud::new, MobCategory.MISC).fireImmune().sized(6.0F, 0.5F).clientTrackingRange(10).updateInterval(2147483647).build("apugli:custom_area_effect_cloud"));
    public static final RegistryObject<EntityType<CustomProjectile>> CUSTOM_PROJECTILE = register("custom_projectile", () -> EntityType.Builder.<CustomProjectile>of(CustomProjectile::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(64).updateInterval(10).build("apugli:custom_projectile"));

    public static void registerAll() {

    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, Supplier<EntityType<T>> entityType) {
        return ENTITY_TYPES.register(name, entityType);
    }
}
