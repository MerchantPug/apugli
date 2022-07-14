package com.github.merchantpug.apugli;

import com.github.merchantpug.apugli.networking.ApugliPacketsS2C;
import com.github.merchantpug.apugli.registry.KeybindRegistry;
import com.github.merchantpug.apugli.util.ApugliClientConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;

public class ApugliClient {
    public static ApugliClientConfig config;
    public static HashMap<String, Boolean> lastKeyBindingStates = new HashMap<>();
    public static boolean hasClearedKeySync = false;

    @Environment(EnvType.CLIENT)
    public static void register() {
        ApugliPacketsS2C.register();

        AutoConfig.register(ApugliClientConfig.class, JanksonConfigSerializer::new);
        ApugliClient.config = AutoConfig.getConfigHolder(ApugliClientConfig.class).getConfig();

        KeybindRegistry.register();
    }
}
