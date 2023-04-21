package net.merchantpug.apugli.power.configuration;

import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IValueModifyingPowerConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;

import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
public record FabricValueModifyingConfiguration(SerializableData.Instance data) implements IValueModifyingPowerConfiguration {

    @Override
    public ListConfiguration<ConfiguredModifier<?>> modifiers() {
        List<ConfiguredModifier<?>> modifiers = new ArrayList<>();
        data.<List<ConfiguredModifier<?>>>ifPresent("modifiers", modifiers::addAll);
        data.<ConfiguredModifier<?>>ifPresent("modifier", modifiers::add);
        return new ListConfiguration<>(modifiers);
    }
    
}
