package net.merchantpug.apugli.platform;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.*;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.modifier.IModifierOperation;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.platform.services.IPowerHelper;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.merchantpug.apugli.power.factory.SpecialPowerFactory;
import net.merchantpug.apugli.registry.ApugliRegisters;
import net.merchantpug.apugli.registry.services.RegistryObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@SuppressWarnings({"rawtypes", "unchecked"})
@AutoService(IPowerHelper.class)
public class FabricPowerHelper implements IPowerHelper<PowerTypeReference> {
    @Override
    public PowerFactory<?> unwrapSimpleFactory(PowerFactory<?> factory) {
        return factory;
    }

    @Override
    public <F extends SimplePowerFactory<?>> RegistryObject<F> registerFactory(F factory) {
        return ApugliRegisters.POWERS.register(factory.getSerializerId().getPath(), () -> factory);
    }

    @Override
    public <F extends SpecialPowerFactory<?>> RegistryObject<F> registerFactory(String name, Class<F> factoryClass) {
        F factory = Services.load(factoryClass);
        return (RegistryObject<F>) (Object) ApugliRegisters.POWERS.register(name, () -> (PowerFactory<?>) factory);
    }

    @Override
    public <P> P getPowerFromType(LivingEntity entity, PowerType<?> powerType) {
        return (P) powerType.get(entity);
    }

    @Override
    public <P extends Power> List<P> getPowers(LivingEntity entity, SimplePowerFactory<P> factory) {
        return PowerHolderComponent.getPowers(entity, factory.getPowerClass());
    }

    @Override
    public <P extends Power> List<P> getPowers(LivingEntity entity, SimplePowerFactory<P> factory, boolean includeInactive) {
        if (entity != null && PowerHolderComponent.KEY.isProvidedBy(entity)) {
            return PowerHolderComponent.KEY.get(entity).getPowers(factory.getPowerClass(), includeInactive);
        }
        return List.of();
    }

    @Override
    public <P> List<P> getPowers(LivingEntity entity, SpecialPowerFactory<P> factory) {
        List<P> list = new LinkedList<>();
        if (entity != null && PowerHolderComponent.KEY.isProvidedBy(entity)) {
            Class<P> cls = factory.getPowerClass();
            return (List<P>) PowerHolderComponent.KEY.get(entity).getPowers((Class<? extends Power>) cls);
        }
        return list;
    }

    @Override
    public <P> List<P> getPowers(LivingEntity entity, SpecialPowerFactory<P> factory, boolean includeInactive) {
        List<P> list = new LinkedList<>();
        if (entity != null && PowerHolderComponent.KEY.isProvidedBy(entity)) {
            Class<P> cls = factory.getPowerClass();
            List<? extends Power> powers = PowerHolderComponent.KEY.get(entity).getPowers((Class<? extends Power>) cls, includeInactive);
            for (Power power : powers) {
                if (includeInactive || power.isActive()) {
                    list.add((P) power);
                }
            }
        }
        return list;
    }

    @Override
    public <P extends Power> boolean hasPower(LivingEntity entity, SimplePowerFactory<P> factory) {
        return PowerHolderComponent.hasPower(entity, factory.getPowerClass());
    }

    @Override
    public <P> boolean hasPower(LivingEntity entity, SpecialPowerFactory<P> factory) {
        if (entity != null && PowerHolderComponent.KEY.isProvidedBy(entity)) {
            List<Power> powers = PowerHolderComponent.KEY.get(entity).getPowers();
            Class<P> cls = factory.getPowerClass();
            for (Power power : powers) {
                if (cls.isAssignableFrom(power.getClass()) && power.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public SerializableDataType<PowerTypeReference> getPowerTypeDataType() {
        return ApoliDataTypes.POWER_TYPE;
    }

    @Override
    public <P> boolean isActive(P power, LivingEntity entity) {
        return ((Power) power).isActive();
    }

    @Override
    public void syncPower(LivingEntity entity, PowerType<?> powerType) {
        if (entity instanceof ServerPlayer serverPlayer && serverPlayer.connection == null)
            return;

        PowerHolderComponent.syncPower(entity, powerType);
    }

    @Override
    public <P> void syncPower(LivingEntity entity, P power) {
        if (entity instanceof ServerPlayer serverPlayer && serverPlayer.connection == null)
            return;

        PowerHolderComponent.syncPower(entity, ((Power) power).getType());
    }

    @Override
    public OptionalInt getResource(LivingEntity entity, PowerTypeReference powerType) {
        Power power = powerType.get(entity);
        if (power instanceof VariableIntPower vip) {
            return OptionalInt.of(vip.getValue());
        } else if (power instanceof CooldownPower cdp) {
            return OptionalInt.of(cdp.getRemainingTicks());
        }
        Apugli.LOG.warn("Failed to get resource for power [{}], because it doesn't hold any resource!", powerType.getIdentifier());
        return OptionalInt.empty();
    }

    @Override
    public OptionalInt setResource(LivingEntity entity, PowerTypeReference powerType, int value) {
        Power power = powerType.get(entity);
        if (power instanceof VariableIntPower vip) {
            int result = vip.setValue(value);
            PowerHolderComponent.syncPower(entity, powerType);
            return OptionalInt.of(result);
        } else if (power instanceof CooldownPower cdp) {
            cdp.setCooldown(value);
            PowerHolderComponent.syncPower(entity, powerType);
            return OptionalInt.of(cdp.getRemainingTicks());
        }
        Apugli.LOG.warn("Failed to set resource for power [{}], because it doesn't hold any resource!", powerType.getIdentifier());
        return OptionalInt.empty();
    }

    @Override
    public <P> ResourceLocation getPowerFromParameter(P power) {
        return ((PowerTypeReference) power).getIdentifier();
    }

    @Override
    public <P> ResourceLocation getPowerId(P power) {
        return ((Power) power).getType().getIdentifier();
    }

    @Override
    public <P> P getPowerFromId(Entity entity, ResourceLocation powerId) {
        return (P) new PowerTypeReference<>(powerId).get(entity);
    }

    @Override
    public void grantPower(ResourceLocation powerId, ResourceLocation source, LivingEntity entity) {
        if (!PowerHolderComponent.KEY.isProvidedBy(entity) || !PowerTypeRegistry.contains(powerId)) return;
        PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
        component.addPower(PowerTypeRegistry.get(powerId), source);
        component.sync();
    }

    @Override
    public void revokePower(ResourceLocation powerId, ResourceLocation source, LivingEntity entity) {
        if (!PowerHolderComponent.KEY.isProvidedBy(entity) || !PowerTypeRegistry.contains(powerId)) return;
        PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
        PowerType<?> powerType = PowerTypeRegistry.get(powerId);
        if (component.hasPower(powerType, source)) {
            component.removePower(powerType, source);
            component.sync();
        }
    }

    @Override
    public boolean hasPowerType(ResourceLocation powerId, ResourceLocation source, LivingEntity entity) {
        if (!PowerTypeRegistry.contains(powerId))
            return false;
        return PowerHolderComponent.KEY.get(entity).hasPower(PowerTypeRegistry.get(powerId), source);
    }

    @Override
    public Map<ResourceLocation, Double> iterateThroughModifierForResources(LivingEntity entity, List<?> modifiers) {
        Map<ResourceLocation, Double> returnMap = new HashMap<>();
        List<Modifier> originalMods = (List<Modifier>) modifiers;

        for (Modifier modifier : originalMods) {
            if (modifier.getData().isPresent("resource")) {
                OptionalDouble doubleValue = getResource(entity, modifier.getData().get("resource")).stream().mapToDouble(i -> i).min();
                if (doubleValue.isPresent())
                    returnMap.put(((PowerTypeReference<?>)modifier.getData().get("resource")).getIdentifier(), doubleValue.getAsDouble());
            }

            if (modifier.getData().isPresent("modifier")) {
                returnMap.putAll(iterateThroughModifierForResources(entity, modifier.getData().get("modifier")));
            }
        }

        return returnMap;
    }

    @Override
    public Map<Integer, Map<ResourceLocation, Double>> getInBetweenResources(LivingEntity entity, List<?> modifiers, List<?> delayModifiers, double base, Map<ResourceLocation, Double> startingResources) {
        Map<Integer, Map<ResourceLocation, Double>> returnMap = new HashMap<>();
        List<Modifier> originalMods = (List<Modifier>) modifiers;
        int previousResourceValue = 0;
        Map<ResourceLocation, Double> resources = new HashMap<>(startingResources);
        Map<ResourceLocation, Double> targetResources = new HashMap<>();

        for (Map.Entry<ResourceLocation, Double> entry : resources.entrySet()) {
            OptionalInt resourceValue = getResource(entity, new PowerTypeReference(entry.getKey()));
            if (resourceValue.isPresent()) {
                targetResources.put(entry.getKey(), (double)resourceValue.getAsInt());
            }
        }

        if (originalMods.stream().noneMatch(modifier -> modifier.getData().isPresent("resource") && startingResources.containsKey(((PowerTypeReference<?>)modifier.getData().get("resource")).getIdentifier()))) {
            return returnMap;
        }

        while(resources.entrySet().stream().anyMatch(entry -> !Objects.equals(targetResources.get(entry.getKey()), entry.getValue()))) {
            for (Modifier modifier : originalMods) {
                if (modifier.getData().isPresent("resource") && startingResources.containsKey(((PowerTypeReference<?>)modifier.getData().get("resource")).getIdentifier())) {
                    int key = returnMap.keySet().isEmpty() ? 0 : returnMap.keySet().stream().max(Integer::compareTo).orElseThrow() + previousResourceValue;
                    returnMap.computeIfAbsent(key, k -> new HashMap<>());
                    OptionalDouble doubleValue = getResource(entity, modifier.getData().get("resource")).stream().mapToDouble(i -> i).min();
                    if (doubleValue.isPresent()) {
                        returnMap.get(key).put(((PowerTypeReference<?>) modifier.getData().get("resource")).getIdentifier(), doubleValue.getAsDouble());
                        previousResourceValue = (int) Services.PLATFORM.applyModifiers(entity, delayModifiers, base);
                    }
                }

                if (modifier.getData().isPresent("modifier")) {
                    Map<Integer, Map<ResourceLocation, Double>> innerMap = getInBetweenResources(entity, modifier.getData().get("modifier"), delayModifiers, base, startingResources);
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

        List<Modifier> originalMods = (List<Modifier>) modifiers;
        double currentValue = base;
        Map<ResourceLocation, Double> resources = new HashMap<>(startingResources);
        Map<ResourceLocation, Double> targetResources = new HashMap<>();

        for (Map.Entry<ResourceLocation, Double> entry : resources.entrySet()) {
            OptionalInt resourceValue = getResource(entity, new PowerTypeReference(entry.getKey()));
            if (resourceValue.isPresent()) {
                targetResources.put(entry.getKey(), (double)resourceValue.getAsInt());
            }
        }

        while(resources.entrySet().stream().anyMatch(entry -> !Objects.equals(targetResources.get(entry.getKey()).intValue(), entry.getValue().intValue()))) {
            currentValue += applyModifierWithSpecificValueAtIndex(entity, delayModifiers, base, resources);
            incrementMods(originalMods, resources, targetResources);
        }

        return currentValue;
    }

    private static void incrementMods(List<Modifier> modifiers, Map<ResourceLocation, Double> resources, Map<ResourceLocation, Double> targetResources) {
        for (Modifier modifier : modifiers) {
            if (modifier.getData().isPresent("modifier")) {
                incrementMods(modifier.getData().get("modifier"), resources, targetResources);
            }
            if (modifier.getData().isPresent("resource") && resources.containsKey(((PowerTypeReference<?>)modifier.getData().get("resource")).getIdentifier())) {
                ResourceLocation resourceId = ((PowerTypeReference<?>) modifier.getData().get("resource")).getIdentifier();
                if (Objects.equals(targetResources.get(resourceId).intValue(), resources.get(resourceId).intValue()))
                    return;

                int increment = targetResources.get(resourceId) < resources.get(resourceId) ? -1 : 1;
                resources.put(resourceId, resources.get(resourceId) + increment);
            }
        }
    }

    @Override
    public double applyModifierWithSpecificValueAtIndex(LivingEntity entity, List<?> modifiers, double base, Map<ResourceLocation, Double> resourceMap) {
        List<Modifier> modifierList = (List<Modifier>) modifiers;
        if (iterateThroughModifierForResources(entity, modifiers).keySet().stream().noneMatch(resourceMap::containsKey)) {
            return Services.PLATFORM.applyModifiers(entity, modifiers, base);
        }
        return ModifierUtil.applyModifiers(entity, remapModifiers(modifierList, resourceMap), base);
    }

    private Map<IModifierOperation, List<SerializableData.Instance>> remapModifiers(List<Modifier> modifiers, Map<ResourceLocation, Double> resourceMap) {
        Map<IModifierOperation, List<SerializableData.Instance>> map = new HashMap<>();
        for (Modifier modifier : modifiers) {
            List<SerializableData.Instance> list = map.computeIfAbsent(modifier.getOperation(), (op) -> new LinkedList<>());
            SerializableData.Instance instance = modifier.getData();
            if (instance.isPresent("resource") && resourceMap.containsKey(((PowerTypeReference<?>)instance.get("resource")).getIdentifier())) {
                SerializableData.Instance inst = modifier.getOperation().getData().new Instance();
                inst.set("value", resourceMap.get(((PowerTypeReference<?>)instance.get("resource")).getIdentifier()));
                inst.set("resource", null);
                if (modifier.getData().isPresent("modifier"))
                    inst.set("modifier", remapModifiersInner(modifier.getData().get("modifier"), resourceMap));
                else
                    inst.set("modifier", null);
                instance = inst;
            }
            list.add(instance);
        }
        return map;
    }

    private List<Modifier> remapModifiersInner(List<Modifier> modifiers, Map<ResourceLocation, Double> resourceMap) {
        List<Modifier> list = new ArrayList<>();
        for (Modifier modifier : modifiers) {
            if (modifier.getData().isPresent("resource") && resourceMap.containsKey(((PowerTypeReference<?>)modifier.getData().get("resource")).getIdentifier())) {
                SerializableData.Instance inst = modifier.getOperation().getData().new Instance();
                inst.set("value", resourceMap.get(((PowerTypeReference<?>)modifier.getData().get("resource")).getIdentifier()));
                inst.set("resource", null);
                if (modifier.getData().isPresent("modifier"))
                    inst.set("modifier", remapModifiersInner(modifier.getData().get("modifier"), resourceMap));
                else
                    inst.set("modifier", null);
                modifier = new Modifier(modifier.getOperation(), inst);
            }
            list.add(modifier);
        }
        return list;
    }

    @Override
    public Map<ResourceLocation, Double> getClosestToBaseScale(LivingEntity entity, List<?> modifiers, double base) {
        Map<ResourceLocation, Double> returnMap = new HashMap<>();
        List<Modifier> originalMods = (List<Modifier>) modifiers;

        for (Modifier modifier : originalMods) {
            if (modifier.getData().isPresent("resource") && (entity != null &&
                    PowerHolderComponent.KEY.get(entity).hasPower(((PowerTypeReference<?>) modifier.getData().get("resource")).getReferencedPowerType()))) {
                if ((((PowerTypeReference<?>) modifier.getData().get("resource")).get(entity) instanceof VariableIntPower vip)) {
                    returnMap.putAll(handleAdditionToReturnMap(entity, modifier, base, vip.getMin(), vip.getMax()));
                } else if (((PowerTypeReference<?>) modifier.getData().get("resource")).get(entity) instanceof CooldownPower clp) {
                    returnMap.putAll(handleAdditionToReturnMap(entity, modifier, base, 0, clp.cooldownDuration));
                }
            }
        }

        return returnMap;
    }

    private Map<ResourceLocation, Double> handleAdditionToReturnMap(LivingEntity entity, Modifier modifier, double base, int min, int max) {
        Map<ResourceLocation, Double> currentResourceMap = new HashMap<>();
        if (modifier.getData().isPresent("resource")) {
            for (int i = min; i < max; ++i) {
                for (Modifier modifier1 : (List<Modifier>) modifier.getData().get("modifier")) {
                    currentResourceMap.putAll(handleAdditionToReturnMap(entity, modifier1, base, min, max));
                }

                double distance = Math.abs(applyModifierWithSpecificValueAtIndex(entity, List.of(modifier), base, currentResourceMap) - base);
                ResourceLocation thisIndex = ((PowerTypeReference<?>)modifier.getData().get("resource")).getReferencedPowerType().getIdentifier();
                if (!currentResourceMap.containsKey(thisIndex) || distance < Math.abs(currentResourceMap.get(thisIndex) - base)) {
                    currentResourceMap.put(thisIndex, (double) i);
                }
            }
        }
        return currentResourceMap;
    }

}
