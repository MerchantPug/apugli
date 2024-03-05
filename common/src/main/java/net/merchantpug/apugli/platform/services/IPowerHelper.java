package net.merchantpug.apugli.platform.services;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.merchantpug.apugli.power.factory.SpecialPowerFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;

public interface IPowerHelper<T> {
    
    Object unwrapSimpleFactory(PowerFactory<?> simplePowerFactory);
    
    <F extends SimplePowerFactory<?>> Supplier<F> registerFactory(F factory);
    
    <F extends SpecialPowerFactory<?>> Supplier<F> registerFactory(String name, Class<F> factoryClass);

    <P> P getPowerFromType(LivingEntity entity, PowerType<?> powerType);

    <P extends Power> List<P> getPowers(LivingEntity entity, SimplePowerFactory<P> factory);

    <P extends Power> List<P> getPowers(LivingEntity entity, SimplePowerFactory<P> factory, boolean includeInactive);

    <P> List<P> getPowers(LivingEntity entity, SpecialPowerFactory<P> factory);

    <P> List<P> getPowers(LivingEntity entity, SpecialPowerFactory<P> factory, boolean includeInactive);

    <P extends Power> boolean hasPower(LivingEntity entity, SimplePowerFactory<P> factory);
    
    <P> boolean hasPower(LivingEntity entity, SpecialPowerFactory<P> factory);
    
    SerializableDataType<T> getPowerTypeDataType();

    <P> boolean isActive(P power, LivingEntity entity);

    void syncPower(LivingEntity entity, PowerType<?> factory);

    <P> void syncPower(LivingEntity entity, P power);
    
    OptionalInt getResource(LivingEntity entity, T powerType);
    
    default OptionalInt getResource(LivingEntity entity, SerializableData.Instance data, String fieldName) {
        return getResource(entity, data.get(fieldName));
    }
    
    OptionalInt setResource(LivingEntity entity, T powerType, int value);
    
    default OptionalInt setResource(LivingEntity entity, SerializableData.Instance data, String fieldName, int value) {
        return setResource(entity, data.get(fieldName), value);
    }

    <P> ResourceLocation getPowerFromParameter(P power);

    <P> ResourceLocation getPowerId(P power);

    default <P extends Power> P getPowerFromId(ResourceLocation powerId, SimplePowerFactory<P> factory, LivingEntity living, boolean includeInactive) {
        Optional<P> optional = getPowers(living, factory, includeInactive).stream().filter(p -> this.getPowerId(p).equals(powerId)).findFirst();
        return optional.orElse(null);
    }

    @Nullable
    default <P> P getPowerFromId(ResourceLocation powerId, SpecialPowerFactory<P> factory, LivingEntity living, boolean includeInactive) {
        Optional<P> optional = getPowers(living, factory, includeInactive).stream().filter(p -> this.getPowerId(p).equals(powerId)).findFirst();
        return optional.orElse(null);
    }

    void grantPower(ResourceLocation powerId, ResourceLocation source, LivingEntity entity);

    void revokePower(ResourceLocation powerId, ResourceLocation source, LivingEntity entity);

    boolean hasPowerType(ResourceLocation powerId, ResourceLocation source, LivingEntity entity);

    Map<ResourceLocation, Double> iterateThroughModifierForResources(LivingEntity entity, List<?> modifiers);
    Map<Integer, Map<ResourceLocation, Double>> getInBetweenResources(LivingEntity entity, List<?> modifiers, List<?> delayModifiers, double base, Map<ResourceLocation, Double> startingResources);
    double addAllInBetweensOfResourceModifiers(LivingEntity entity, List<?> modifiers, List<?> delayModifiers, double base, Map<ResourceLocation, Double> startingResources);
    double applyModifierWithSpecificValueAtIndex(LivingEntity entity, List<?> modifiers, double base, Map<ResourceLocation, Double> resourceMap);

    Map<ResourceLocation, Double> getClosestToBaseScale(LivingEntity entity, List<?> modifiers, double base);

}
