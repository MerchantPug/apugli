package net.merchantpug.apugli.platform;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.*;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.platform.services.IPowerHelper;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.merchantpug.apugli.power.factory.SpecialPowerFactory;
import net.merchantpug.apugli.registry.ApugliRegisters;
import net.merchantpug.apugli.registry.services.RegistryObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedList;
import java.util.List;
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
        return ApugliRegisters.POWERS.register(factory.getSerializerId().getPath(), ()  -> factory);
    }
    
    @Override
    public <F extends SpecialPowerFactory<?>> RegistryObject<F> registerFactory(String name, Class<F> factoryClass) {
        F factory = Services.load(factoryClass);
        return (RegistryObject<F>)(Object)ApugliRegisters.POWERS.register(name, () -> (PowerFactory<?>) factory);
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
        if (PowerHolderComponent.KEY.isProvidedBy(entity)) {
            return PowerHolderComponent.KEY.get(entity).getPowers(factory.getPowerClass(), includeInactive);
        }
        return List.of();
    }

    @Override
    public <P> List<P> getPowers(LivingEntity entity, SpecialPowerFactory<P> factory) {
        List<P> list = new LinkedList<>();
        if (PowerHolderComponent.KEY.isProvidedBy(entity)) {
            Class<P> cls = factory.getPowerClass();
            return (List<P>) PowerHolderComponent.KEY.get(entity).getPowers((Class<? extends Power>) cls);
        }
        return list;
    }

    @Override
    public <P> List<P> getPowers(LivingEntity entity, SpecialPowerFactory<P> factory, boolean includeInactive) {
        List<P> list = new LinkedList<>();
        if (PowerHolderComponent.KEY.isProvidedBy(entity)) {
            Class<P> cls = factory.getPowerClass();
            List<? extends Power> powers = PowerHolderComponent.KEY.get(entity).getPowers((Class<? extends Power>) cls, includeInactive);
            for(Power power : powers) {
                if(includeInactive || power.isActive()) {
                    list.add((P)power);
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
        List<Power> powers = PowerHolderComponent.KEY.get(entity).getPowers();
        Class<P> cls = factory.getPowerClass();
        List<P> list = new LinkedList<>();
        for(Power power : powers) {
            if(cls.isAssignableFrom(power.getClass()) && power.isActive()) {
                return true;
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
        return ((Power)power).isActive();
    }

    @Override
    public void syncPower(LivingEntity entity, PowerType<?> powerType) {
        PowerHolderComponent.syncPower(entity, powerType);
    }

    @Override
    public OptionalInt getResource(LivingEntity entity, PowerTypeReference powerType) {
        Power power = powerType.get(entity);
        if(power instanceof VariableIntPower vip) {
            return OptionalInt.of(vip.getValue());
        } else if(power instanceof CooldownPower cdp) {
            return OptionalInt.of(cdp.getRemainingTicks());
        }
        Apugli.LOG.warn("Failed to get resource for power [{}], because it doesn't hold any resource!", powerType.getIdentifier());
        return OptionalInt.empty();
    }
    @Override
    public OptionalInt setResource(LivingEntity entity, PowerTypeReference powerType, int value) {
        Power power = powerType.get(entity);
        if(power instanceof VariableIntPower vip) {
            int result = vip.setValue(value);
            PowerHolderComponent.syncPower(entity, powerType);
            return OptionalInt.of(result);
        } else if(power instanceof CooldownPower cdp) {
            cdp.setCooldown(value);
            PowerHolderComponent.syncPower(entity, powerType);
            return OptionalInt.of(cdp.getRemainingTicks());
        }
        Apugli.LOG.warn("Failed to set resource for power [{}], because it doesn't hold any resource!", powerType.getIdentifier());
        return OptionalInt.empty();
    }

    @Override
    public <P> ResourceLocation getPowerFromParameter(P power) {
        return ((PowerTypeReference)power).getIdentifier();
    }

    @Override
    public <P> ResourceLocation getPowerId(P power) {
        return ((Power)power).getType().getIdentifier();
    }

    @Override
    public void grantPower(ResourceLocation powerId, ResourceLocation source, LivingEntity entity) {
        if (!PowerTypeRegistry.contains(powerId)) return;
        PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
        component.addPower(PowerTypeRegistry.get(powerId), source);
        component.sync();
    }

    @Override
    public void revokePower(ResourceLocation powerId, ResourceLocation source, LivingEntity entity) {
        if (!PowerTypeRegistry.contains(powerId)) return;
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

}
