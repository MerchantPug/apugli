package net.merchantpug.apugli.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.NamespaceAlias;
import net.merchantpug.apugli.access.FactoryInstanceAccess;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public record ConditionFactoryWrapperCodec<T>(Registry<ConditionFactory<T>> registry) implements Codec<ConditionFactory<T>.Instance> {

    @Override
    public <A> DataResult<Pair<ConditionFactory<T>.Instance, A>> decode(DynamicOps<A> ops, A input) {
        JsonElement json = ops.convertMap(JsonOps.INSTANCE, input);
        if (!(json instanceof JsonObject jsonObject)) {
            return DataResult.error(() -> "JSON from ConditionFactory is not a JsonObject.");
        }
        ResourceLocation factoryLocation = ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "type"));

        if (factoryLocation != null && NamespaceAlias.hasAlias(factoryLocation)) {
            factoryLocation = NamespaceAlias.resolveAlias(factoryLocation);
        }

        ConditionFactory<T> factory = registry.get(factoryLocation);
        if (factory == null) {
            ResourceLocation finalFactoryLocation = factoryLocation;
            return DataResult.error(() -> "ActionFactory `" + finalFactoryLocation + "` does not exist.");
        }

        ConditionFactory<T>.Instance instance = factory.read(jsonObject);
        ((FactoryInstanceAccess)instance).setJson(json);
        return DataResult.success(Pair.of(instance, ops.empty()));
    }

    @Override
    public <A> DataResult<A> encode(ConditionFactory<T>.Instance input, DynamicOps<A> ops, A prefix) {
        JsonElement json = ((FactoryInstanceAccess)input).getJson();
        if (json == null) {
            return DataResult.error(() -> "Could not find JSON associated with ConditionFactory.");
        }
        return DataResult.success(JsonOps.INSTANCE.convertTo(ops, json));
    }

}