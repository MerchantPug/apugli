package net.merchantpug.apugli.power.configuration;

import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IValueModifyingPowerConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
public record FabricValueModifyingConfiguration(SerializableData.Instance data) implements IValueModifyingPowerConfiguration {

    @Override
    public ListConfiguration<AttributeModifier> modifiers() {
        List<AttributeModifier> modifiers = new ArrayList<>();
        data.<List<AttributeModifier>>ifPresent("modifiers", modifiers::addAll);
        data.<AttributeModifier>ifPresent("modifier", modifiers::add);
        return new ListConfiguration<>(modifiers);
    }
    
}
