package net.merchantpug.apugli.condition;

import net.merchantpug.apugli.condition.configuration.FabricConditionConfiguration;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.factory.BiomeCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiPredicate;

@ParametersAreNonnullByDefault
public class FabricBiomeCondition extends BiomeCondition<FabricConditionConfiguration<Biome>> {
    
    public FabricBiomeCondition(SerializableData data, BiPredicate<SerializableData.Instance, Biome> condition) {
        super(FabricConditionConfiguration.codec(data, condition));
    }
    
    @Override
    protected boolean check(FabricConditionConfiguration<Biome> config, Holder<Biome> biome) {
        return config.condition().test(biome.value());
    }
    
}
