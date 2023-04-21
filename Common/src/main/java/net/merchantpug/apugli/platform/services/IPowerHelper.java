package net.merchantpug.apugli.platform.services;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SpecialPowerFactory;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.power.factory.ValueModifyingPowerFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface IPowerHelper<T> {
    
    Object unwrapSimpleFactory(PowerFactory<?> simplePowerFactory);
    
    <F extends SimplePowerFactory<?>> Supplier<F> registerFactory(F factory);
    
    <F extends SpecialPowerFactory<?>> Supplier<F> registerFactory(String name, Class<F> factoryClass);
    
    <P extends Power> List<P> getPowers(LivingEntity entity, SimplePowerFactory<P> factory);
    
    <P> List<P> getPowers(LivingEntity entity, SpecialPowerFactory<P> factory);
    
    <P extends Power> boolean hasPower(LivingEntity entity, SimplePowerFactory<P> factory);
    
    <P> boolean hasPower(LivingEntity entity, SpecialPowerFactory<P> factory);
    
    SerializableDataType<T> getPowerTypeDataType();

    void syncPower(LivingEntity entity, PowerType<?> factory);
    
    OptionalInt getResource(LivingEntity entity, T powerType);
    
    default OptionalInt getResource(LivingEntity entity, SerializableData.Instance data, String fieldName) {
        return getResource(entity, data.get(fieldName));
    }
    
    OptionalInt setResource(LivingEntity entity, T powerType, int value);
    
    default OptionalInt setResource(LivingEntity entity, SerializableData.Instance data, String fieldName, int value) {
        return setResource(entity, data.get(fieldName), value);
    }

}
