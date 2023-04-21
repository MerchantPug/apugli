package net.merchantpug.apugli.power;

import com.mojang.serialization.Codec;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.power.ActiveCooldownPowerFactory.Simple;
import net.merchantpug.apugli.power.configuration.FabricActiveCooldownConfiguration;
import net.merchantpug.apugli.power.factory.ActiveCooldownPowerFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public abstract class AbstractActiveCooldownPower extends Simple<FabricActiveCooldownConfiguration> implements ActiveCooldownPowerFactory<ConfiguredPower<FabricActiveCooldownConfiguration, ?>> {

    protected AbstractActiveCooldownPower(Codec<FabricActiveCooldownConfiguration> codec) {
        super(codec);
    }

    @Override
    public int getRemainingTicks(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
        return super.getValue(power, entity);
    }

    @Override
    public int setRemainingTicks(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity, int value) {
        return super.assign(power, entity, value);
    }

    @Override
    public void sync(LivingEntity entity, ConfiguredPower<FabricActiveCooldownConfiguration, ?> power) {
        IPowerContainer.sync(entity);
    }

    @Override
    public SerializableData.Instance getDataFromPower(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power) {
        return power.getConfiguration().data();
    }

}
