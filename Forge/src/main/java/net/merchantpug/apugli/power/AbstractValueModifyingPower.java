package net.merchantpug.apugli.power;

import com.mojang.serialization.Codec;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.IValueModifyingPower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ValueModifyingPowerFactory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class AbstractValueModifyingPower extends PowerFactory<FabricValueModifyingConfiguration> implements IValueModifyingPower<FabricValueModifyingConfiguration>, ValueModifyingPowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>> {

    protected AbstractValueModifyingPower(Codec<FabricValueModifyingConfiguration> codec) {
        super(codec);
    }

    @Override
    public List<AttributeModifier> getModifiers(ConfiguredPower<FabricValueModifyingConfiguration, ?> configuredPower, Entity entity) {
        return configuredPower.getConfiguration().modifiers().getContent();
    }

    @Override
    public SerializableData.Instance getDataFromPower(ConfiguredPower<FabricValueModifyingConfiguration, ?> power) {
        return power.getConfiguration().data();
    }

}
