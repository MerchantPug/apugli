package com.github.merchantpug.apugli.platform.services;

import com.github.merchantpug.apugli.power.data.IPowerData;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataType;
import net.minecraft.resources.ResourceLocation;
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
     * Registers a power type based on name and a power factory.
     *
     * @return The registered power
     */
    <P extends Power> PowerFactory<P> registerPowerFactory(ResourceLocation id, IPowerData power);

    <P extends Power> List<P> getPowers(LivingEntity entity, Class<P> powerClass, PowerFactory<P> powerFactory);

    <P extends Power> boolean hasPower(LivingEntity entity, Class<P> powerClass, PowerFactory<P> powerFactory);

    SerializableDataType<?> getBiEntityConditionDataType();

    SerializableDataType<?> getBiomeConditionDataType();

    SerializableDataType<?> getBlockConditionDataType();

    SerializableDataType<?> getDamageConditionDataType();

    SerializableDataType<?> getEntityConditionDataType();

    SerializableDataType<?> getFluidConditionDataType();

    SerializableDataType<?> getItemConditionDataType();

    SerializableDataType<?> getBiEntityActionDataType();

    SerializableDataType<?> getBlockActionDataType();

    SerializableDataType<?> getEntityActionDataType();

    SerializableDataType<?> getItemActionDataType();
}
