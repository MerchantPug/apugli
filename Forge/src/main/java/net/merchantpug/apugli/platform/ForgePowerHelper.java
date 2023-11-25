package net.merchantpug.apugli.platform;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.fabric.FabricPowerFactory;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.data.ApoliForgeDataTypes;
import net.merchantpug.apugli.mixin.forge.common.accessor.FabricPowerFactoryAccessor;
import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.network.s2c.SyncSinglePowerPacket;
import net.merchantpug.apugli.platform.services.IPowerHelper;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.merchantpug.apugli.power.factory.SpecialPowerFactory;
import net.merchantpug.apugli.registry.ApugliRegisters;
import net.merchantpug.apugli.registry.services.RegistryObject;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
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
    public <P> P getPowerFromType(LivingEntity entity, PowerType<?> powerType) {
        return (P) powerType.getConfiguredPower();
    }

    @Override
    public <P extends Power> List<P> getPowers(LivingEntity entity, SimplePowerFactory<P> factory) {
        return IPowerContainer.get(entity).map(x -> x.getPowers((FabricPowerFactory<P>) factory.getWrapped())).orElseGet(ImmutableList::of)
                .stream()
                .map(holder -> ((FabricPowerFactoryAccessor<P>)holder.get().getFactory()).apugli$invokeGetPower(holder.get(), entity))
                .collect(Collectors.toList());
    }

    @Override
    public <P extends Power> List<P> getPowers(LivingEntity entity, SimplePowerFactory<P> factory, boolean includeInactive) {
        return IPowerContainer.get(entity).map(x -> x.getPowers((FabricPowerFactory<P>) factory.getWrapped(), includeInactive)).orElseGet(ImmutableList::of)
                .stream()
                .map(holder -> ((FabricPowerFactoryAccessor<P>)holder.get().getFactory()).apugli$invokeGetPower(holder.get(), entity))
                .collect(Collectors.toList());
    }

    @Override
    public <P> List<P> getPowers(LivingEntity entity, SpecialPowerFactory<P> factory) {
        return IPowerContainer.get(entity).map(x -> x.getPowers((io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory<?>) factory)).orElseGet(ImmutableList::of)
                .stream().map(holder -> (P)holder.get()).collect(Collectors.toList());
    }

    @Override
    public <P> List<P> getPowers(LivingEntity entity, SpecialPowerFactory<P> factory, boolean includeInactive) {
        return IPowerContainer.get(entity).map(x -> x.getPowers((io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory<?>) factory, includeInactive)).orElseGet(ImmutableList::of)
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
        return ApoliForgeDataTypes.POWER_TYPE;
    }

    @Override
    public <P> boolean isActive(P power, LivingEntity entity) {
        return ((ConfiguredPower<?, ?>)power).isActive(entity);
    }

    @Override
    public void syncPower(LivingEntity entity, PowerType<?> factory) {
        ApugliPacketHandler.sendS2CTrackingAndSelf(new SyncSinglePowerPacket(entity.getId(), factory.getIdentifier(), factory.getConfiguredPower().serialize(ApoliAPI.getPowerContainer(entity))), entity);
    }

    @Override
    public <P> void syncPower(LivingEntity entity, P power) {
        ConfiguredPower<?, ?> p = (ConfiguredPower<?, ?>)power;
        ApugliPacketHandler.sendS2CTrackingAndSelf(new SyncSinglePowerPacket(entity.getId(), p.getRegistryName(), p.serialize(ApoliAPI.getPowerContainer(entity))), entity);
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

    @Override
    public <P> ResourceLocation getPowerFromParameter(P power) {
        var holder = ((Holder<ConfiguredPower<?, ?>>)power);
        if (holder.isBound() && holder.unwrapKey().isPresent()) {
            return holder.unwrapKey().get().location();
        }
        return null;
    }

    @Override
    public <P> ResourceLocation getPowerId(P power) {
        return ((ConfiguredPower<?, ?>)power).getRegistryName();
    }

    @Override
    public void grantPower(ResourceLocation powerId, ResourceLocation source, LivingEntity entity) {
        IPowerContainer container = ApoliAPI.getPowerContainer(entity);
        if (container != null) {
            container.addPower(powerId, source);
            container.sync();
        }
    }

    @Override
    public void revokePower(ResourceLocation powerId, ResourceLocation source, LivingEntity entity) {
        IPowerContainer container = ApoliAPI.getPowerContainer(entity);
        if (container != null) {
            container.removePower(powerId, source);
            container.sync();
        }
    }

    @Override
    public boolean hasPowerType(ResourceLocation powerId, ResourceLocation source, LivingEntity entity) {
        IPowerContainer container = ApoliAPI.getPowerContainer(entity);
        if (container != null) {
            return container.hasPower(powerId, source);
        }
        return false;
    }

}
