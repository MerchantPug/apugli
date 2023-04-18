package com.github.merchantpug.apugli.power.data;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface IPowerData<P extends Power> {
    default SerializableData getSerializableData() {
        return new SerializableData();
    }

    default BiFunction<PowerType<P>, LivingEntity, P> getPowerConstructor(SerializableData.Instance data) {
        throw new NullPointerException("");
    }

    default Function<SerializableData.Instance, BiFunction<PowerType<P>, LivingEntity, P>> getPowerConstructorForge() {
        return this::getPowerConstructor;
    }

    default PowerFactory<P> createFabricFactory(ResourceLocation resourceLocation) {
        return new PowerFactory<>(resourceLocation,
                this.getSerializableData(), this::getPowerConstructor).allowCondition();
    }
}
