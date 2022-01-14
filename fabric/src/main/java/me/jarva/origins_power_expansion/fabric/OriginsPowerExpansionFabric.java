package me.jarva.origins_power_expansion.fabric;

import me.jarva.origins_power_expansion.OriginsPowerExpansion;
import net.fabricmc.api.ModInitializer;

public class OriginsPowerExpansionFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        OriginsPowerExpansion.init();
    }
}
