package io.github.merchantpug.apugli.registry.fabric;

import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModRegistries;
import net.minecraft.util.registry.Registry;

public class ApugliPowerFactoriesImpl {
    public static void register(PowerFactory<?> serializer) {
        Registry.register(ModRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
    }
}
