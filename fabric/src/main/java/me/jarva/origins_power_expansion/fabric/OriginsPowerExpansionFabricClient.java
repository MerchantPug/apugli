package me.jarva.origins_power_expansion.fabric;

import me.jarva.origins_power_expansion.OriginsPowerExpansionClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OriginsPowerExpansionFabricClient implements ClientModInitializer {
    public void onInitializeClient() {
        OriginsPowerExpansionClient.register();
    }
}
