package me.jarva.origins_power_expansion.forge;

import me.jarva.origins_power_expansion.OriginsPowerExpansionClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OriginsPowerExpansionForgeClient {
    public static void initialize() {
        OriginsPowerExpansionClient.register();
    }
}