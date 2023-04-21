package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ActiveCooldownPowerFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractActiveCooldownPower<P extends AbstractActiveCooldownPower.Instance> extends AbstractCooldownPower<P> implements ActiveCooldownPowerFactory<P> {

    public AbstractActiveCooldownPower(String id, SerializableData data, Function<SerializableData.Instance, BiFunction<PowerType<P>, LivingEntity, P>> factoryConstructor) {
        super(id, data, factoryConstructor);
    }

    @Override
    public void execute(P power, Entity entity) {
        if (power.canUse()) {
            power.onUse();
            power.use();
        }
    }

    @Override
    public abstract Class<P> getPowerClass();
    
    public static class Instance extends AbstractCooldownPower.Instance implements Active {

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }

        @Override
        public void onUse() {

        }

        @Override
        public Key getKey() {
            return data.get("key");
        }

        @Override
        public void setKey(Key key) {

        }
    }
    
}
