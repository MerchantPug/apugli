package io.github.merchantpug.apugli.forge;

import io.github.merchantpug.apugli.Apugli;
import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Apugli.MOD_ID)
public class ApugliForge {
    public ApugliForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Apugli.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Apugli.init();

        // Run our client code when appropriate
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ApugliForgeClient::initialize);
    }
}