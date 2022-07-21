package com.github.merchantpug.apugli.forge;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.ApugliClient;
import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
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

        MinecraftForge.EVENT_BUS.addListener(ApugliForge::clearUponLogOut);

        // Run our client code when appropriate
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ApugliForgeClient::initialize);
    }

    private static void clearUponLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Apugli.keysToCheck.remove(event.getPlayer().getUuid());
        Apugli.currentlyUsedKeys.remove(event.getPlayer().getUuid());
    }
}