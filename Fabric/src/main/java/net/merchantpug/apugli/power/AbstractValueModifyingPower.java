package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.power.factory.ValueModifyingPowerFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractValueModifyingPower<P extends AbstractValueModifyingPower.Instance> extends PowerFactory<P> implements ValueModifyingPowerFactory<P> {

    public AbstractValueModifyingPower(String id, SerializableData data, Function<SerializableData.Instance, BiFunction<PowerType<P>, LivingEntity, P>> factoryConstructor) {
        super(Apugli.asResource(id), data, factoryConstructor);
    }

    @Override
    public List<AttributeModifier> getModifiers(Instance power, Entity entity) {
        return power.getModifiers();
    }

    @Override
    public SerializableData.Instance getDataFromPower(P power) {
        return power.data;
    }

    public static class Instance extends ValueModifyingPower {
        protected final SerializableData.Instance data;

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity);
            data.ifPresent("modifier", this::addModifier);
            data.<List<AttributeModifier>>ifPresent("modifiers", mods -> mods.forEach(this::addModifier));
            this.data = data;
        }
    }

}