package me.jarva.origins_power_expansion.powers.factory.fabric;

import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModRegistries;
import me.jarva.origins_power_expansion.OriginsPowerExpansion;
import net.minecraft.core.Registry;

public class PowerFactoriesImpl {
    public static void register(PowerFactory<?> serializer) {
        Registry.register(ModRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
    }
}
