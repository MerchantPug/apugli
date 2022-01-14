package io.github.merchantpug.apugli.registry.forge;

import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;

public class PowerFactoriesImpl {
    public static void register(PowerFactory<?> serializer) {
        ModRegistriesArchitectury.POWER_FACTORY.register(serializer.getSerializerId(), () -> serializer);
    }
}
