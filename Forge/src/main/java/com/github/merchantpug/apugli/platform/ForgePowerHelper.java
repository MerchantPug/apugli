package com.github.merchantpug.apugli.platform;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.data.ApoliForgeDataTypes;
import com.github.merchantpug.apugli.mixin.forge.FabricPowerFactoryAccessor;
import com.github.merchantpug.apugli.platform.services.IPowerHelper;
import com.github.merchantpug.apugli.power.factory.SimplePowerFactory;
import com.github.merchantpug.apugli.power.factory.SpecialPowerFactory;
import com.github.merchantpug.apugli.registry.ApugliRegisters;
import com.github.merchantpug.apugli.registry.services.RegistryObject;
import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.fabric.FabricPowerFactory;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@AutoService(IPowerHelper.class)
public class ForgePowerHelper implements IPowerHelper<Holder<ConfiguredPower<?, ?>>> {
    
    @Override
    public io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory<?> unwrapSimpleFactory(PowerFactory<?> factory) {
        return factory.getWrapped();
    }
    
    @Override
    public <F extends SimplePowerFactory<?>> Supplier<F> registerFactory(F factory) {
        ApugliRegisters.POWERS.register(factory.getSerializerId().getPath(), factory::getWrapped);
        return () -> factory;
    }
    
    @Override
    public <F extends SpecialPowerFactory<?>> RegistryObject<F> registerFactory(String name, Class<F> factoryClass) {
        return (RegistryObject<F>)(Object)ApugliRegisters.POWERS.register(name, () -> (io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory<?>)Services.load(factoryClass));
    }
    
    @Override
    public <P extends Power> List<P> getPowers(LivingEntity entity, SimplePowerFactory<P> factory) {
        return IPowerContainer.getPowers(entity, (FabricPowerFactory<P>) factory.getWrapped()).stream()
            .map(holder -> ((FabricPowerFactoryAccessor<P>)holder.get().getFactory()).invokeGetPower(holder.get(), entity))
            .collect(Collectors.toList());
    }
    
    @Override
    public <P> List<P> getPowers(LivingEntity entity, SpecialPowerFactory<P> factory) {
        return IPowerContainer.getPowers(entity, (io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory<?>) factory)
            .stream().map(holder -> (P)holder.get()).collect(Collectors.toList());
    }
    
    @Override
    public <P extends Power> boolean hasPower(LivingEntity entity, SimplePowerFactory<P> factory) {
        return IPowerContainer.hasPower(entity, factory.getWrapped());
    }
    
    @Override
    public <P> boolean hasPower(LivingEntity entity, SpecialPowerFactory<P> factory) {
        return IPowerContainer.hasPower(entity, (io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory<?>) factory);
    }
    
    @Override
    public SerializableDataType<Holder<ConfiguredPower<?, ?>>> getPowerTypeDataType() {
        return ApoliForgeDataTypes.POWER_TYPE.get();
    }
    
    @Override
    public OptionalInt getResource(LivingEntity entity, Holder<ConfiguredPower<?,?>> holder) {
        var powerId = holder.unwrapKey();
        if(holder.isBound()) {
            ConfiguredPower<?, ?> power = holder.get();
            if(IPowerContainer.get(entity).resolve().flatMap(container -> {
                if(container == null) return Optional.empty();
                return powerId.map(container::hasPower);
            }).orElse(false)) {
                return power.getValue(entity);
            }
        }
        Apugli.LOG.warn("Failed to get resource for power [{}], because it doesn't hold any resource!", powerId.orElse(null));
        return OptionalInt.empty();
    }
    
    @Override
    public OptionalInt setResource(LivingEntity entity, Holder<ConfiguredPower<?,?>> holder, int value) {
        var powerId = holder.unwrapKey();
        if(holder.isBound()) {
            ConfiguredPower<?, ?> power = holder.get();
            if(IPowerContainer.get(entity).resolve().flatMap(container -> {
                if(container == null) return Optional.empty();
                return powerId.map(container::hasPower);
            }).orElse(false)) {
                OptionalInt result = power.assign(entity, value);
                if(result.isPresent()) {
                    ApoliAPI.synchronizePowerContainer(entity);
                    return result;
                }
            }
        }
        Apugli.LOG.warn("Failed to set resource for power [{}], because it doesn't hold any resource!", powerId.orElse(null));
        return OptionalInt.empty();
    }
    
}
