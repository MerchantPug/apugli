package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.power.factory.CooldownPowerFactory;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractCooldownPower<P extends AbstractCooldownPower.Instance> extends PowerFactory<P> implements CooldownPowerFactory<P> {
    
    public AbstractCooldownPower(String id, SerializableData data, Function<SerializableData.Instance, BiFunction<PowerType<P>, LivingEntity, P>> factoryConstructor) {
        super(Apugli.asResource(id), data, factoryConstructor);
    }
    
    @Override
    public abstract Class<P> getPowerClass();
    
    public boolean canUse(P power, Entity entity) {
        return power.canUse();
    }
    
    public void use(P power, Entity entity) {
        power.use();
    }
    
    public int getRemainingTicks(P power, Entity entity) {
        return power.getRemainingTicks();
    }
    
    public int setRemainingTicks(P power, Entity entity, int value) {
        power.setCooldown(value);
        return power.getRemainingTicks();
    }
    
    public void sync(LivingEntity entity, P power) {
        PowerHolderComponent.syncPower(entity, power.getType());
    }
    
    @Override
    public SerializableData.Instance getDataFromPower(Instance power) {
        return power.data;
    }
    
    public static class Instance extends CooldownPower {
        protected final SerializableData.Instance data;
        
        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data.getInt("cooldown"), data.get("hud_render"));
            this.data = data;
        }
        
    }
    
}
