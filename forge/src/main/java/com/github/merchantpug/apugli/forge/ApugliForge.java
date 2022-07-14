package com.github.merchantpug.apugli.forge;

import com.github.merchantpug.apugli.Apugli;
import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.maven.artifact.versioning.ArtifactVersion;

@Mod(Apugli.MODID)
public class ApugliForge {

    public ApugliForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Apugli.MODID, FMLJavaModLoadingContext.get().getModEventBus());

        ArtifactVersion version = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion();
        Apugli.VERSION = version.toString();

        Apugli.init();

        // Run our client code when appropriate
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ApugliForgeClient::initialize);
    }
}