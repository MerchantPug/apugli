package net.merchantpug.apugli.platform;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.ModifierData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.ModifierOperation;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
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
        if (entity instanceof ServerPlayer serverPlayer && serverPlayer.connection == null)
            return;

        ApugliPacketHandler.sendS2CTrackingAndSelf(new SyncSinglePowerPacket(entity.getId(), factory.getIdentifier(), factory.getConfiguredPower().serialize(ApoliAPI.getPowerContainer(entity))), entity);
    }

    @Override
    public <P> void syncPower(LivingEntity entity, P power) {
        if (entity instanceof ServerPlayer serverPlayer && serverPlayer.connection == null)
            return;

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
    public <P> P getPowerFromId(Entity entity, ResourceLocation powerId) {
        @Nullable Holder<ConfiguredPower<IDynamicFeatureConfiguration, io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory<IDynamicFeatureConfiguration>>> holder = ApoliAPI.getPowerContainer(entity).getPower(powerId);
        if (holder == null || !holder.isBound()) {
            return null;
        }
        return (P) holder;
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

    @Override
    public Map<ResourceLocation, Double> iterateThroughModifierForResources(LivingEntity entity, List<?> modifiers) {
        Map<ResourceLocation, Double> returnMap = new HashMap<>();
        List<ConfiguredModifier<?>> originalMods = (List<ConfiguredModifier<?>>) modifiers;

        for (ConfiguredModifier<?> modifier : originalMods) {
            if (modifier.getData().resource().isPresent()) {
                OptionalDouble doubleValue = getResource(entity, modifier.getData().resource().get()).stream().mapToDouble(i -> i).min();
                if (doubleValue.isPresent())
                    returnMap.put(modifier.getData().resource().get().unwrapKey().orElseThrow().location(), doubleValue.getAsDouble());
            }

            if (!modifier.getData().modifiers().isEmpty()) {
                returnMap.putAll(iterateThroughModifierForResources(entity, modifier.getData().modifiers()));
            }
        }

        return returnMap;
    }

    @Override
    public Map<Integer, Map<ResourceLocation, Double>> getInBetweenResources(LivingEntity entity, List<?> modifiers, List<?> delayModifiers, double base, Map<ResourceLocation, Double> startingResources) {
        Map<Integer, Map<ResourceLocation, Double>> returnMap = new HashMap<>();
        List<ConfiguredModifier<?>> originalMods = (List<ConfiguredModifier<?>>) modifiers;
        int previousResourceValue = 0;
        Map<ResourceLocation, Double> resources = new HashMap<>(startingResources);
        Map<ResourceLocation, Double> targetResources = new HashMap<>();

        for (Map.Entry<ResourceLocation, Double> entry : resources.entrySet()) {
            OptionalInt resourceValue = getResource(entity, ApoliAPI.getPowers().getHolderOrThrow(ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, entry.getKey())));
            if (resourceValue.isPresent()) {
                targetResources.put(entry.getKey(), (double)resourceValue.getAsInt());
            }
        }

        if (originalMods.stream().noneMatch(modifier -> modifier.getData().resource().isPresent() && modifier.getData().resource().get().isBound() && startingResources.containsKey(modifier.getData().resource().get().unwrapKey().orElseThrow().location()))) {
            return returnMap;
        }

        while(resources.entrySet().stream().anyMatch(entry -> !Objects.equals(targetResources.get(entry.getKey()), entry.getValue()))) {
            for (ConfiguredModifier<?> modifier : originalMods) {
                if (modifier.getData().resource().isPresent() && modifier.getData().resource().get().isBound() && startingResources.containsKey(modifier.getData().resource().get().unwrapKey().orElseThrow().location())) {
                    int key = returnMap.keySet().isEmpty() ? 0 : returnMap.keySet().stream().max(Integer::compareTo).orElseThrow() + previousResourceValue;
                    returnMap.computeIfAbsent(key, k -> new HashMap<>());
                    OptionalDouble doubleValue = getResource(entity, modifier.getData().resource().get()).stream().mapToDouble(i -> i).min();
                    if (doubleValue.isPresent()) {
                        returnMap.get(key).put(modifier.getData().resource().get().unwrapKey().orElseThrow().location(), doubleValue.getAsDouble());
                        previousResourceValue = (int) Services.PLATFORM.applyModifiers(entity, delayModifiers, base);
                    }
                }

                if (!modifier.getData().modifiers().isEmpty()) {
                    Map<Integer, Map<ResourceLocation, Double>> innerMap = getInBetweenResources(entity, modifier.getData().modifiers(), delayModifiers, base, startingResources);
                    innerMap.forEach((integer, resourceLocationDoubleMap) -> {
                        returnMap.merge(integer, resourceLocationDoubleMap, (map, map2) -> {
                            map.putAll(map2);
                            return map;
                        });
                    });
                }
            }
            incrementMods(originalMods, resources, targetResources);
        }

        return returnMap;
    }

    @Override
    public double addAllInBetweensOfResourceModifiers(LivingEntity entity, List<?> modifiers, List<?> delayModifiers, double base, Map<ResourceLocation, Double> startingResources) {
        if (startingResources.isEmpty()) {
            return base;
        }

        List<ConfiguredModifier<?>> originalMods = (List<ConfiguredModifier<?>>) modifiers;
        double currentValue = base;
        Map<ResourceLocation, Double> resources = new HashMap<>(startingResources);
        Map<ResourceLocation, Double> targetResources = new HashMap<>();

        for (Map.Entry<ResourceLocation, Double> entry : resources.entrySet()) {
            OptionalInt resourceValue = getResource(entity, ApoliAPI.getPowers().getHolderOrThrow(ResourceKey.create(ApoliDynamicRegistries.CONFIGURED_POWER_KEY, entry.getKey())));
            if (resourceValue.isPresent()) {
                targetResources.put(entry.getKey(), (double)resourceValue.getAsInt());
            }
        }

        while(resources.entrySet().stream().anyMatch(entry -> !Objects.equals(targetResources.get(entry.getKey()).intValue(), entry.getValue().intValue()))) {
            currentValue += applyModifierWithSpecificValueAtIndex(entity, modifiers, base, resources);
            incrementMods(originalMods, resources, targetResources);
        }

        return currentValue;
    }

    private static void incrementMods(List<ConfiguredModifier<?>> modifiers, Map<ResourceLocation, Double> resources, Map<ResourceLocation, Double> targetResources) {
        for (ConfiguredModifier<?> modifier : modifiers) {
            if (!modifier.getData().modifiers().isEmpty()) {
                incrementMods(modifier.getData().modifiers(), resources, targetResources);
            }
            if (modifier.getData().resource().isPresent() && modifier.getData().resource().get().isBound() && resources.containsKey(modifier.getData().resource().get().unwrapKey().orElseThrow().location())) {
                ResourceLocation resourceId = modifier.getData().resource().get().unwrapKey().orElseThrow().location();
                if (Objects.equals(targetResources.get(resourceId).intValue(), resources.get(resourceId).intValue()))
                    return;

                int increment = targetResources.get(resourceId) < resources.get(resourceId) ? -1 : 1;
                resources.put(resourceId, resources.get(resourceId) + increment);
            }
        }
    }

    @Override
    public double applyModifierWithSpecificValueAtIndex(LivingEntity entity, List<?> modifiers, double base, Map<ResourceLocation, Double> resourceMap) {
        List<ConfiguredModifier<?>> modifierList = (List<ConfiguredModifier<?>>) modifiers;
        return ModifierUtil.applyModifiers(entity, remapModifiers(modifierList, resourceMap), base);
    }

    private Map<ModifierOperation, List<ConfiguredModifier<?>>> remapModifiers(List<ConfiguredModifier<?>> modifiers, Map<ResourceLocation, Double> resourceMap) {
        Map<ModifierOperation, List<ConfiguredModifier<?>>> map = new HashMap<>();
        for (ConfiguredModifier<?> modifier : modifiers) {
            List<ConfiguredModifier<?>> list = map.computeIfAbsent(modifier.getFactory(), (op) -> new LinkedList<>());
            ConfiguredModifier<?> instance = modifier;
            if (modifier.getData().resource().isPresent() && modifier.getData().resource().get().isBound() && resourceMap.containsKey(modifier.getData().resource().get().unwrapKey().orElseThrow().location())) {
                instance = new ConfiguredModifier<>(modifier::getFactory, new ModifierData(resourceMap.get(modifier.getData().resource().get().unwrapKey().orElseThrow().location()), Optional.empty(), remapModifiersInner(modifier.getData().modifiers(), resourceMap)));;
            }
            list.add(instance);
        }
        return map;
    }

    private List<ConfiguredModifier<?>> remapModifiersInner(List<ConfiguredModifier<?>> modifiers, Map<ResourceLocation, Double> resourceMap) {
        List<ConfiguredModifier<?>> list = new ArrayList<>();
        for (ConfiguredModifier<?> modifier : modifiers) {
            if (modifier.getData().resource().isPresent() && modifier.getData().resource().get().isBound() && resourceMap.containsKey(modifier.getData().resource().get().unwrapKey().orElseThrow().location())) {
                modifier = new ConfiguredModifier<>(modifier::getFactory, new ModifierData(resourceMap.get(modifier.getData().resource().get().unwrapKey().orElseThrow().location()), Optional.empty(), remapModifiersInner(modifier.getData().modifiers(), resourceMap)));
            }
            list.add(modifier);
        }
        return list;
    }

    @Override
    public Map<ResourceLocation, Double> getClosestToBaseScale(LivingEntity entity, List<?> modifiers, double base) {
        Map<ResourceLocation, Double> returnMap = new HashMap<>();
        List<ConfiguredModifier<?>> originalMods = (List<ConfiguredModifier<?>>) modifiers;

        for (ConfiguredModifier<?> modifier : originalMods) {
            if (modifier.getData().resource().isPresent() && modifier.getData().resource().get().isBound() && (entity != null &&
                    ApoliAPI.getPowerContainer(entity).hasPower(modifier.getData().resource().get().unwrapKey().orElseThrow())) &&
                    modifier.getData().resource().get().value().asVariableIntPower().isPresent()) {
                returnMap.putAll(handleAdditionToReturnMap(entity, modifier, base, modifier.getData().resource().get().get().getMinimum(entity).orElse(0), modifier.getData().resource().get().get().getMaximum(entity).orElse(0)));
            }
        }

        return returnMap;
    }

    private Map<ResourceLocation, Double> handleAdditionToReturnMap(LivingEntity entity, ConfiguredModifier<?> modifier, double base, int min, int max) {
        Map<ResourceLocation, Double> currentResourceMap = new HashMap<>();
        if (modifier.getData().resource().isPresent() && modifier.getData().resource().get().isBound()) {
            for (int i = min; i < max; ++i) {
                for (ConfiguredModifier<?> modifier1 : modifier.getData().modifiers()) {
                    currentResourceMap.putAll(handleAdditionToReturnMap(entity, modifier1, base, min, max));
                }

                double distance = Math.abs(applyModifierWithSpecificValueAtIndex(entity, List.of(modifier), base, currentResourceMap) - base);
                ResourceLocation thisIndex = modifier.getData().resource().get().unwrapKey().orElseThrow().location();
                if (!currentResourceMap.containsKey(thisIndex) || distance < Math.abs(currentResourceMap.get(thisIndex) - base)) {
                    currentResourceMap.put(thisIndex, (double) i);
                }
            }
        }
        return currentResourceMap;
    }

}
