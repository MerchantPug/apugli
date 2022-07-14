package io.github.merchantpug.apugli.forge;

import io.github.merchantpug.apugli.ApugliClient;
import io.github.merchantpug.apugli.util.ApugliClientConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ApugliForgeClient {
    public static void initialize() {
        ApugliClient.register();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> ApugliForgeClient::buildConfigScreen);
    }

    private static Screen buildConfigScreen(MinecraftClient minecraftClient, Screen parent) {
        return AutoConfig.getConfigScreen(ApugliClientConfig.class, parent).get();
    }
}