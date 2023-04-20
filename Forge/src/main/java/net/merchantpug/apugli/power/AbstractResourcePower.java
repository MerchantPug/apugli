package net.merchantpug.apugli.power;

import net.merchantpug.apugli.power.configuration.FabricResourceConfiguration;
import net.merchantpug.apugli.power.factory.ResourcePowerFactory;
import com.mojang.serialization.Codec;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.power.HudRenderedVariableIntPowerFactory.Simple;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class AbstractResourcePower extends Simple<FabricResourceConfiguration> implements ResourcePowerFactory<ConfiguredPower<FabricResourceConfiguration, ?>> {
    
    protected AbstractResourcePower(Codec<FabricResourceConfiguration> codec) {
        super(codec);
    }
    
    public int assign(ConfiguredPower<FabricResourceConfiguration, ?> configuration, Entity player, int value) {
        int previous = this.get(configuration, player);
        int minimum = this.getMinimum(configuration, player);
        int maximum = this.getMaximum(configuration, player);
        value = Mth.clamp(value, minimum, maximum);
        this.set(configuration, player, value);
        FabricResourceConfiguration config = configuration.getConfiguration();
        if (previous != value) {
            if (value == minimum && config.minAction() != null) {
                config.minAction().execute(player);
            }
            if (value == maximum && config.maxAction() != null) {
                config.maxAction().execute(player);
            }
        }
        return value;
    }
    
    @Override
    public int getMin(ConfiguredPower<FabricResourceConfiguration, ?> power, Entity entity) {
        return power.getConfiguration().min();
    }
    
    @Override
    public int getMax(ConfiguredPower<FabricResourceConfiguration, ?> power, Entity entity) {
        return power.getConfiguration().max();
    }
    
    @Override
    public int increment(ConfiguredPower<FabricResourceConfiguration, ?> configuration, Entity player) {
        return super.increment(configuration, player);
    }
    
    @Override
    public int decrement(ConfiguredPower<FabricResourceConfiguration, ?> configuration, Entity player) {
        return super.decrement(configuration, player);
    }
    
    @Override
    public void sync(LivingEntity entity, ConfiguredPower<FabricResourceConfiguration, ?> power) {
        IPowerContainer.sync(entity);
    }
    
    @Override
    public SerializableData.Instance getDataFromPower(ConfiguredPower<FabricResourceConfiguration, ?> power) {
        return power.getConfiguration().data();
    }
    
}
