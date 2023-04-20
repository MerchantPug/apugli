package net.merchantpug.apugli.condition.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public record FabricConditionConfiguration<T>(SerializableData.Instance data, Predicate<T> condition) implements IDynamicFeatureConfiguration {
    
    public static <T> Codec<FabricConditionConfiguration<T>> codec(SerializableData data, BiPredicate<SerializableData.Instance, T> condition) {
        return data.xmap((x) -> new FabricConditionConfiguration<T>(x, t -> condition.test(x, t)), FabricConditionConfiguration::data).codec();
    }
    
}
