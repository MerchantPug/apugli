package com.github.merchantpug.apugli.platform.services;

import io.github.apace100.calio.data.SerializableDataType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();
    
    /**
     * Get the supported modifier type by platform due to the fact that new Modifier hasn't implemented on Forge's end.
     * @return SerializableDataType of Modifier(on Fabric) or AttributeModifier(on Forge).
     */
    SerializableDataType<?> getModifierDataType();
    
    /**
     * Get the supported modifier type by platform due to the fact that new Modifier hasn't implemented on Forge's end.
     * @return SerializableDataType of List of Modifier(on Fabric) or AttributeModifier(on Forge).
     */
    SerializableDataType<?> getModifiersDataType();
    
    /**
     * Apply generic modifier objects to value for a certain living entity.
     * @param living The living entity involved in the modification.
     * @param modifiers Generic modifiers, could be Modifier/Attribute Modifiers.
     * @param value The value to be modified.
     * @return The modified value.
     */
    double applyModifiers(LivingEntity living, List<?> modifiers, double value);
    
    /**
     * Get the modified reach distance for the entity, Fabric uses REA and Forge use its own attribute.
     * @param entity The entity to check.
     * @return The reach distance of the entity.
     */
    double getReachDistance(Entity entity);
    
    /**
     * Get the modified attack range for the entity, Fabric uses REA and Forge use its own attribute.
     * @param entity The entity to check.
     * @return The attack range of the entity.
     */
    double getAttackRange(Entity entity);
    
}
