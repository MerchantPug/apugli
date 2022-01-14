package me.jarva.origins_power_expansion.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import me.jarva.origins_power_expansion.OriginsPowerExpansion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(OriginsPowerExpansion.MOD_ID)
public class OriginsPowerExpansionForge {
    public OriginsPowerExpansionForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(OriginsPowerExpansion.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        OriginsPowerExpansion.init();

        // Run our client code when appropriate
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> OriginsPowerExpansionForgeClient::initialize);
    }
}