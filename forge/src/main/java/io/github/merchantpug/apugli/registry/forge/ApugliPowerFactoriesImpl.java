package io.github.merchantpug.apugli.registry.forge;

import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.merchantpug.apugli.registry.ApugliRegistriesArchitectury;

public class ApugliPowerFactoriesImpl {
    public static void register(PowerFactory<?> serializer) {
        ApugliRegistriesArchitectury.POWER_FACTORY.register(serializer.getSerializerId(), () -> serializer);
    }
}
