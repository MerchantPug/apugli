package com.github.merchantpug.apugli.action.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record FabricActionConfiguration<T>(SerializableData.Instance data, Consumer<T> action) implements IDynamicFeatureConfiguration {
    
    public static <T> Codec<FabricActionConfiguration<T>> codec(SerializableData data, BiConsumer<SerializableData.Instance, T> action) {
        return data.xmap((x) -> new FabricActionConfiguration<T>(x, t -> action.accept(x, t)), FabricActionConfiguration::data).codec();
    }
    
}
