package io.github.merchantpug.apugli;

import io.github.merchantpug.apugli.networking.ApugliPacketsS2C;
import io.github.merchantpug.apugli.registry.KeybindRegistry;
import io.github.merchantpug.apugli.util.ApugliClientConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ApugliClient {
    public static ApugliClientConfig config;

    @Environment(EnvType.CLIENT)
    public static void register() {
        ApugliPacketsS2C.register();

        AutoConfig.register(ApugliClientConfig.class, JanksonConfigSerializer::new);
        ApugliClient.config = AutoConfig.getConfigHolder(ApugliClientConfig.class).getConfig();

        KeybindRegistry.register();
    }
}
