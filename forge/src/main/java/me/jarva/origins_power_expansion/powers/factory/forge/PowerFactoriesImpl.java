package me.jarva.origins_power_expansion.powers.factory.forge;

import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import me.jarva.origins_power_expansion.OriginsPowerExpansion;

public class PowerFactoriesImpl {
    public static void register(PowerFactory<?> serializer) {
        ModRegistriesArchitectury.POWER_FACTORY.register(serializer.getSerializerId(), () -> serializer);
    }
}
