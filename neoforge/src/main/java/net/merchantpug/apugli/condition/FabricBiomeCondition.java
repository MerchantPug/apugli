package net.merchantpug.apugli.condition;

import net.merchantpug.apugli.condition.configuration.FabricConditionConfiguration;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.factory.BiomeCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiPredicate;

@ParametersAreNonnullByDefault
public class FabricBiomeCondition extends BiomeCondition<FabricConditionConfiguration<Holder<Biome>>> {
    
    public FabricBiomeCondition(SerializableData data, BiPredicate<SerializableData.Instance, Holder<Biome>> condition) {
        super(FabricConditionConfiguration.codec(data, condition));
    }
    
    @Override
    protected boolean check(FabricConditionConfiguration<Holder<Biome>> config, Holder<Biome> biome) {
        return config.condition().test(biome);
    }
    
}
