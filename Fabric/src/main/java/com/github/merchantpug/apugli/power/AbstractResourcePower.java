package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.power.factory.ResourcePowerFactory;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ResourcePower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractResourcePower<P extends AbstractResourcePower.Instance> extends PowerFactory<P> implements ResourcePowerFactory<P> {
    
    public AbstractResourcePower(String id, SerializableData data, Function<SerializableData.Instance, BiFunction<PowerType<P>, LivingEntity, P>> factoryConstructor) {
        super(Apugli.asResource(id), data, factoryConstructor);
    }
    
    @Override
    public abstract Class<P> getPowerClass();
    
    @Override
    public int getMin(P power, Entity entity) {
        return power.getMin();
    }
    
    @Override
    public int getMax(P power, Entity entity) {
        return power.getMax();
    }
    
    @Override
    public int assign(P power, Entity entity, int value) {
        return power.setValue(value);
    }
    
    @Override
    public int getValue(P power, Entity entity) {
        return power.getValue();
    }
    
    @Override
    public int increment(P power, Entity entity) {
        return power.increment();
    }
    
    @Override
    public int decrement(P power, Entity entity) {
        return power.decrement();
    }
    
    @Override
    public void sync(LivingEntity entity, P power) {
        PowerHolderComponent.syncPower(entity, power.getType());
    }
    
    @Override
    public SerializableData.Instance getDataFromPower(P power) {
        return power.data;
    }
    
    public static class Instance extends ResourcePower {
        protected final SerializableData.Instance data;
    
        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity,
                data.get("hud_render"),
                data.getInt("start_value"),
                data.getInt("min"),
                data.getInt("max"),
                data.get("min_action"),
                data.get("max_action")
            );
            this.data = data;
        }
    }
    
}
