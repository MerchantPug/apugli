package com.github.merchantpug.apugli.registry.forge;

import io.github.apace100.origins.power.factory.PowerFactory;

public class ApugliPowerFactoriesImpl {
    public static void register(PowerFactory<?> serializer) {
        ApugliRegistriesArchitectury.POWER_FACTORY.register(serializer.getSerializerId(), () -> serializer);
    }
}
