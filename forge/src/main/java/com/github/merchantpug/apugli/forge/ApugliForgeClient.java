package com.github.merchantpug.apugli.forge;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.ApugliClient;
import com.github.merchantpug.apugli.util.ApugliClientConfig;
import io.github.apace100.origins.screen.GameHudRender;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.Iterator;

@OnlyIn(Dist.CLIENT)
public class ApugliForgeClient {
    public static void initialize() {
        ApugliClient.register();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> ApugliForgeClient::buildConfigScreen);

        MinecraftForge.EVENT_BUS.addListener(ApugliForgeClient::clearUponLogOut);
    }

    private static void clearUponLogOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        Apugli.keysToCheck.clear();
        Apugli.currentlyUsedKeys.clear();
        ApugliClient.lastKeyBindingStates.clear();
        ApugliClient.hasClearedKeySync = false;
    }

    private static Screen buildConfigScreen(MinecraftClient minecraftClient, Screen parent) {
        return AutoConfig.getConfigScreen(ApugliClientConfig.class, parent).get();
    }
}