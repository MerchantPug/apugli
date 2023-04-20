package net.merchantpug.apugli.power;

import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.CooldownPowerFactory;
import com.mojang.serialization.Codec;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.power.CooldownPowerFactory.Simple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public abstract class AbstractCooldownPower extends Simple<FabricCooldownConfiguration> implements CooldownPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {
    
    protected AbstractCooldownPower(Codec<FabricCooldownConfiguration> codec) {
        super(codec);
    }
    
    @Override
    public int getRemainingTicks(ConfiguredPower<FabricCooldownConfiguration, ?> power, Entity entity) {
        return super.getValue(power, entity);
    }
    
    @Override
    public int setRemainingTicks(ConfiguredPower<FabricCooldownConfiguration, ?> power, Entity entity, int value) {
        return super.assign(power, entity, value);
    }
    
    @Override
    public void sync(LivingEntity entity, ConfiguredPower<FabricCooldownConfiguration, ?> power) {
        IPowerContainer.sync(entity);
    }
    
    @Override
    public SerializableData.Instance getDataFromPower(ConfiguredPower<FabricCooldownConfiguration, ?> power) {
        return power.getConfiguration().data();
    }
    
}
