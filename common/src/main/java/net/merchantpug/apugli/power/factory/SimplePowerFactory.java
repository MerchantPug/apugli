package net.merchantpug.apugli.power.factory;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.platform.Services;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A simple impl of {@link IPowerFactory} using the shared {@link PowerFactory}, doesn't need to be load with {@link Services}. <br>
 * @param <P> Subclass of {@link Power}, should not extend any special Power on Fabric's end.
 */
public abstract class SimplePowerFactory<P extends Power> extends PowerFactory<P> implements IPowerFactory<P> {
    
    public SimplePowerFactory(String name, SerializableData data, Function<SerializableData.Instance, BiFunction<PowerType<P>, LivingEntity, P>> factoryConstructor) {
        super(Apugli.asResource(name), data, factoryConstructor);
    }
    
    @Override
    public abstract Class<P> getPowerClass();
    
}