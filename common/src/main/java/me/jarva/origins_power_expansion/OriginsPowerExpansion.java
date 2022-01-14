package me.jarva.origins_power_expansion;

import me.jarva.origins_power_expansion.powers.factory.PowerFactories;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OriginsPowerExpansion {
    public static final String MOD_ID = "origins_power_expansion";
    public static final Logger LOGGER = LogManager.getLogger("OriginsPowerExpansion");
    
    public static void init() {
        PowerFactories.register();
    }

    public static ResourceLocation identifier(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
