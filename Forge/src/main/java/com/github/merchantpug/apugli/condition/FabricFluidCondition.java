package com.github.merchantpug.apugli.condition;

import com.github.merchantpug.apugli.condition.configuration.FabricConditionConfiguration;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.factory.FluidCondition;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiPredicate;

@ParametersAreNonnullByDefault
public class FabricFluidCondition extends FluidCondition<FabricConditionConfiguration<FluidState>> {
    
    public FabricFluidCondition(SerializableData data, BiPredicate<SerializableData.Instance, FluidState> condition) {
        super(FabricConditionConfiguration.codec(data, condition));
    }
    
    @Override
    public boolean check(FabricConditionConfiguration<FluidState> config, FluidState fluidState) {
        return config.condition().test(fluidState);
    }
    
}
